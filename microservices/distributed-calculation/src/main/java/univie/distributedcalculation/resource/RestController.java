package univie.distributedcalculation.resource;

import feign.Response;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.ResourceAccessException;
import univie.distributedcalculation.controller.Controller;
import univie.distributedcalculation.exceptions.GenericException;
import univie.distributedcalculation.model.CalculationObject;
import univie.distributedcalculation.model.ECalculationType;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@org.springframework.web.bind.annotation.RestController
@Slf4j
public class RestController {

    @Autowired
    private Controller controller;

    private Integer timeout = null;
    private Integer responseCode = 200;

//    private long lastTimeCalled = System.currentTimeMillis();

    @GetMapping("/health")
    private ResponseEntity<String> health() {
//        this.lastTimeCalled = System.currentTimeMillis();
        return new ResponseEntity<>("OK", HttpStatus.OK);
    }

    @GetMapping("/version")
    private ResponseEntity<String> version() {
        return new ResponseEntity<>("1.3", HttpStatus.OK);
    }

    @PostMapping(value = "/add", consumes = MediaType.APPLICATION_JSON_VALUE)
    private ResponseEntity<String> add(@RequestBody @Valid CalculationObject object) {
        sleep();
        return new ResponseEntity<>(controller.add(object), HttpStatus.valueOf(responseCode));
    }

    @PostMapping(value = "/multiply", consumes = MediaType.APPLICATION_JSON_VALUE)
    private ResponseEntity<String> multiply(@RequestBody @Valid CalculationObject object) {
        sleep();
        return new ResponseEntity<>(controller.multiply(object), HttpStatus.valueOf(responseCode));
    }

    /**
     * /prime to calculate the n-th prime number
     * @return result of calculation of the n-th prime number
     */
    @PostMapping(value = "/prime")
    public ResponseEntity<String> prime(@Valid @RequestParam("n") @NotNull @Min(0) Integer boundary) {
        List<Integer> result = controller.calculatePrime(boundary);
        sleep();
        return new ResponseEntity<>(result.toString(), HttpStatus.valueOf(responseCode));
    }

    /**
     * /fibonacci to calculate the n-th Fibonacci number
     * @return result of calculation of the n-th Fibonacci number
     */
    @PostMapping(value = "/fibonacci")
    public ResponseEntity<String> fibonacci(@Valid @RequestParam("n") @NotNull @Min(0) Integer nthElement) {
        BigInteger result = controller.calculateFibonacci(nthElement);
        sleep();
        return new ResponseEntity<>(result.toString(), HttpStatus.valueOf(responseCode));
    }

    @GetMapping(value = "/getWorkloadByType/{type}")
    private ResponseEntity<AtomicInteger> getWorkloadByType(@PathVariable("type") String type) {
        sleep();
        return new ResponseEntity<>(
                controller.getEndpointWorkloadByType(ECalculationType.valueOf(type.toUpperCase())), HttpStatus.valueOf(responseCode));
    }


    @PostMapping(value = "/timeout")
    public ResponseEntity<Integer> timeout(@RequestParam("n") int n) {
        timeout = n;
        return new ResponseEntity<>(timeout, HttpStatus.OK);
    }

    @PostMapping(value = "/responseCode")
    public ResponseEntity<Integer> responseCode(@RequestParam("n") int n) {
        responseCode = n;
        return new ResponseEntity<>(responseCode, HttpStatus.OK);
    }

    @ExceptionHandler({ GenericException.class })
    public ResponseEntity<String> handleException(GenericException ex) {
        // TODO if ex instance of
        return new ResponseEntity<>(ex.getErrorName() + ", " + ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    // not needed with istio
//    @Scheduled(fixedRate = 5000)
//    private void removeDeadMicroservices() {
//        long diff = System.currentTimeMillis() - lastTimeCalled;
//        if (diff > 60000) {
//            log.info("Registration service was offline for 60s. Shutting down...");
//            System.exit(1);
//        } else if (diff > 5000) {
//            log.info("Trying to re-register service.");
//            controller.registerMe();
//        }
//    }

    private void sleep() {
        if (timeout != null) {
            try {
                Thread.sleep(timeout);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
