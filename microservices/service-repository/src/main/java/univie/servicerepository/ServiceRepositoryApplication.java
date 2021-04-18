package univie.servicerepository;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableFeignClients
@EnableRetry(proxyTargetClass=true)
public class ServiceRepositoryApplication {


    public static void main(String[] args) {
        SpringApplication.run(ServiceRepositoryApplication.class, args);
    }

}
