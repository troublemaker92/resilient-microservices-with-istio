package univie.distributedcalculation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.client.RestTemplate;
import univie.distributedcalculation.controller.Controller;
import univie.distributedcalculation.model.MicroserviceInfo;

import javax.annotation.PostConstruct;

@SpringBootApplication
public class DistributedCalculationApplication {


    @Value("${server.port}")
    private int port;

    public static void main(String[] args) {
        SpringApplication.run(DistributedCalculationApplication.class, args);
    }

//    @PostConstruct
//    void postConstruct(){
//        new RestTemplate().postForObject("http://localhost:7000/register", new MicroserviceInfo(
//                "localhost", port, "Addition of two numbers.", "add"), String.class);
//    }

}
