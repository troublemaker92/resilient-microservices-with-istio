package univie.servicerepository.resource;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import univie.servicerepository.controller.Controller;
import univie.servicerepository.exceptions.GenericException;
import univie.servicerepository.model.MicroserviceInfo;

import java.util.List;

@RestController
@Slf4j
public class RestContoller {

    @Autowired
    private Controller controller;

    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> registerMicroservice(@RequestBody MicroserviceInfo msInfo) {
        controller.registerMicroservice(msInfo);
        return new ResponseEntity<>("Service has been successfully added", HttpStatus.OK);
    }

    @GetMapping(value = "/getRegisteredServices", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<MicroserviceInfo>> getRegisteredServices() {
        return new ResponseEntity<>(controller.getRegisteredMicroservices(), HttpStatus.OK);
    }


    // TODO return json
    @ExceptionHandler({ GenericException.class })
    public ResponseEntity<String> handleException(GenericException ex) {
        // TODO if ex instance of
        return new ResponseEntity<>(ex.getErrorName() + ", " + ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

}
