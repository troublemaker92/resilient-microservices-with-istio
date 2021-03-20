package univie.distributedcalculation.resource;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

@org.springframework.web.bind.annotation.RestController
public class RestController {

    @GetMapping("/health")
    private HttpStatus health() {
        return HttpStatus.OK;
    }


}
