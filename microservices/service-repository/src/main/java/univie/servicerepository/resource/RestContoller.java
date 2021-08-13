package univie.servicerepository.resource;


import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import univie.servicerepository.controller.Controller;
import univie.servicerepository.exceptions.GenericException;
import univie.servicerepository.model.MicroserviceInfo;
import univie.servicerepository.model.MicroserviceInfoMetrics;

import javax.validation.Valid;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
public class RestContoller {

    @Autowired
    private Controller controller;

    //static final Logger log = LogManager.getLogger(RestContoller.class.getName());


    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> registerMicroservice(@Valid @RequestBody MicroserviceInfo msInfo) {
        controller.registerMicroservice(msInfo);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(value = "/getRegisteredServices", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<MicroserviceInfo>> getRegisteredServices() throws IOException {
        return new ResponseEntity<>(controller.getRegisteredServices(), HttpStatus.OK);
    }

    @GetMapping(value = "/findServiceByFunction", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<MicroserviceInfo>> findServiceByFunction(@RequestParam String function) throws IOException {
        return new ResponseEntity<>(controller.getRegisteredServicesByFunction(function), HttpStatus.OK);
    }

    @GetMapping(value = "/getMetrics", produces = MediaType.APPLICATION_JSON_VALUE)
    @CrossOrigin
    public ResponseEntity<List<MicroserviceInfoMetrics>> getMetrics() {
        return new ResponseEntity<>(controller.getMetrics(), HttpStatus.OK);
    }


    // TODO return json
    @ExceptionHandler({ GenericException.class })
    public ResponseEntity<String> handleException(GenericException ex) {
        // TODO if ex instance of2
        return new ResponseEntity<>(ex.getErrorName() + ", " + ex.getMessage(), HttpStatus.BAD_REQUEST);
    }


}
