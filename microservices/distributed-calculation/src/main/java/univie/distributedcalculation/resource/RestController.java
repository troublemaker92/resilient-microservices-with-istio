package univie.distributedcalculation.resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import univie.distributedcalculation.controller.Controller;
import univie.distributedcalculation.exceptions.GenericException;
import univie.distributedcalculation.model.CalculationObject;

import javax.validation.Valid;

@org.springframework.web.bind.annotation.RestController
public class RestController {

    @Autowired
    private Controller controller;

    @GetMapping("/health")
    private HttpStatus health() {
        return HttpStatus.OK;
    }

    @PostMapping(value = "/add", consumes = MediaType.APPLICATION_JSON_VALUE)
    private ResponseEntity<String> add(@RequestBody @Valid CalculationObject object) {
        return new ResponseEntity<>(controller.add(object), HttpStatus.OK);
    }


    @ExceptionHandler({ GenericException.class })
    public ResponseEntity<String> handleException(GenericException ex) {
        // TODO if ex instance of
        return new ResponseEntity<>(ex.getErrorName() + ", " + ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

}
