package univie.servicerepository.controller;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import univie.servicerepository.exceptions.ServiceAlreadyRegisteredException;
import univie.servicerepository.model.MicroserviceInfo;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Component
@Data
@Slf4j
public class Controller {

    private List<MicroserviceInfo> registeredMicroservices = new ArrayList<>();

    public void registerMicroservice(MicroserviceInfo msInfo) {
        isServiceRegistered(msInfo);
        registeredMicroservices.add(msInfo);
        log.info("Successfully registered: {}", msInfo);
    }

    private void deregisterMicroservice(MicroserviceInfo msInfo) {
        registeredMicroservices.remove(msInfo);
        log.info("Successfully removed: {}", msInfo);
    }

    private void isServiceRegistered(MicroserviceInfo msInfo) throws ServiceAlreadyRegisteredException {
        if (registeredMicroservices.stream().anyMatch(msInfo::equals)) {
            log.warn("Microservice is already registered: {}", msInfo);
            throw new ServiceAlreadyRegisteredException(ServiceAlreadyRegisteredException.class.getSimpleName(),
                    "This microservice is already registered.");
        }
    }

    @Scheduled(fixedRate = 1000)
    private void removeDeadMicroservices() {
        List<MicroserviceInfo> toRemove = new ArrayList<>();
        for (MicroserviceInfo ms: registeredMicroservices) {
            HttpStatus health;
            try {
                health = checkHealth(ms);
                if (HttpStatus.OK != health) {
                    toRemove.add(ms);
                }
            } catch (ResourceAccessException ignore) {
                toRemove.add(ms);
            }
        }
        for (MicroserviceInfo ms: toRemove) {
            deregisterMicroservice(ms);
        }
    }

    private HttpStatus checkHealth(MicroserviceInfo ms) {
        String url = "http://" + ms.getMsName() + ":" + ms.getMsPort() + "/health";
        RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder().setConnectTimeout(Duration.ofMillis(1000));
        return restTemplateBuilder.build().getForObject(url, HttpStatus.class);
    }




}
