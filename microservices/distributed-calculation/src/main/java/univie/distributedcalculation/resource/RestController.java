package univie.distributedcalculation.resource;

import feign.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import univie.distributedcalculation.controller.Controller;
import univie.distributedcalculation.exceptions.GenericException;
import univie.distributedcalculation.model.CalculationObject;
import univie.distributedcalculation.model.ECalculationType;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@org.springframework.web.bind.annotation.RestController
public class RestController {

    @Autowired
    private Controller controller;

    @GetMapping("/health")
    private ResponseEntity<String> health() {
        return new ResponseEntity<>("OK", HttpStatus.OK);
    }

    @PostMapping(value = "/add", consumes = MediaType.APPLICATION_JSON_VALUE)
    private ResponseEntity<String> add(@RequestBody @Valid CalculationObject object) {
        return new ResponseEntity<>(controller.add(object), HttpStatus.OK);
    }

    @PostMapping(value = "/multiply", consumes = MediaType.APPLICATION_JSON_VALUE)
    private ResponseEntity<String> multiply(@RequestBody @Valid CalculationObject object) {
        return new ResponseEntity<>(controller.multiply(object), HttpStatus.OK);
    }

    /**
     * /prime to calculate the n-th prime number
     * @return result of calculation of the n-th prime number
     */
    @PostMapping(value = "/prime")
    public ResponseEntity<String> prime(@Valid @RequestParam("n") @NotNull @Min(0) Integer boundary) {
        List<Integer> result = controller.calculatePrime(boundary);
        return new ResponseEntity<>(result.toString(), HttpStatus.OK);
    }

    /**
     * /fibonacci to calculate the n-th Fibonacci number
     * @return result of calculation of the n-th Fibonacci number
     */
    @PostMapping(value = "/fibonacci")
    public ResponseEntity<String> fibonacci(@Valid @RequestParam("n") @NotNull @Min(0) Integer nthElement) {
        BigInteger result = controller.calculateFibonacci(nthElement);
        return new ResponseEntity<>(result.toString(), HttpStatus.OK);
    }

    @GetMapping(value = "/getWorkloadByType/{type}")
    private ResponseEntity<AtomicInteger> multiply(@PathVariable("type") String type) {
        return new ResponseEntity<>(
                controller.getEndpointWorkloadByType(ECalculationType.valueOf(type.toUpperCase())), HttpStatus.OK);
    }


    @ExceptionHandler({ GenericException.class })
    public ResponseEntity<String> handleException(GenericException ex) {
        // TODO if ex instance of
        return new ResponseEntity<>(ex.getErrorName() + ", " + ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

}
