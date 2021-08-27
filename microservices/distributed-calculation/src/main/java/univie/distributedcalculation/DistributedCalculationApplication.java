package univie.distributedcalculation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import univie.distributedcalculation.controller.Controller;

import javax.annotation.PostConstruct;

@SpringBootApplication
//@EnableScheduling
public class DistributedCalculationApplication {

    public static void main(String[] args) {
        SpringApplication.run(DistributedCalculationApplication.class, args);
    }


}
