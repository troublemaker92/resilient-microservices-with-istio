package univie.distributedcalculation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import univie.distributedcalculation.controller.Controller;

import javax.annotation.PostConstruct;

@SpringBootApplication
public class DistributedCalculationApplication {

    @Autowired
    private Controller controller;

    public static void main(String[] args) {
        SpringApplication.run(DistributedCalculationApplication.class, args);
    }

    @PostConstruct
    void postConstruct() {
        controller.registerMe();
    }

}
