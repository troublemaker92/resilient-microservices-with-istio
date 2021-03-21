package univie.servicerepository.network;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.net.URI;

@FeignClient(name = "healthCheck", url = "urlPlaceholder")
public interface IFeignClient {

    @RequestMapping(method = RequestMethod.GET, value = "/health")
    HttpStatus health(URI baseUrl);

}
