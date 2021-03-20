package univie.servicerepository.resource;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import univie.servicerepository.controller.Controller;
import univie.servicerepository.model.MicroserviceInfo;

@RestController
@Slf4j
public class RestContoller {

    @Autowired
    private Controller controller;

    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> registerMicroservice(@RequestBody MicroserviceInfo msInfo) {
        return new ResponseEntity<>("Service has been successfully added", HttpStatus.OK);
    }



}
