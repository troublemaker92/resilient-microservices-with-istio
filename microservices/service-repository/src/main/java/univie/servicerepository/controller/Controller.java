package univie.servicerepository.controller;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import univie.servicerepository.exceptions.ServiceAlreadyRegisteredException;
import univie.servicerepository.model.MicroserviceInfo;
import univie.servicerepository.model.MicroserviceInfoMetrics;
import univie.servicerepository.network.IFeignClient;
import univie.servicerepository.network.RetryService;

import javax.lang.model.type.MirroredTypeException;
import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Data
@Slf4j
public class Controller {

    @Autowired
    private RetryService retryService;

    @Autowired
    private IFeignClient iFeignClient;

    private Map<String, List<MicroserviceInfo>> registeredMicroservices = new HashMap<>();

    public void registerMicroservice(MicroserviceInfo msInfo) {
        String msUrl = getUrlFromMsInfo(msInfo);
        isServiceRegistered(msInfo, msUrl);
        registerMs(msInfo, msUrl);
        log.info("Successfully registered: {}", msInfo);
    }

    public List<MicroserviceInfo> getRegisteredServices() {
        Collection<List<MicroserviceInfo>> msInfoValues = registeredMicroservices.values();
        return msInfoValues.stream()
                        .flatMap(List::stream)
                        .collect(Collectors.toList());
    }

    public List<MicroserviceInfo> getRegisteredServicesByFunction(String function) {
        if (function == null) {
            return new ArrayList<>();
        }
        var registeredServices = getRegisteredServices();

        List<MicroserviceInfo> result = new ArrayList<>();
        for (MicroserviceInfo msInfo: registeredServices) {
            if ("calculation".equalsIgnoreCase(msInfo.getMsType())) {
                String url = getUrlFromMsInfo(msInfo);
                Integer workload = iFeignClient.getWorkloadByMsType(URI.create(url), msInfo.getMsFunction());
                msInfo.setWorkload(workload);
            }
            result.add(msInfo);
        }
        return result.stream()
                .filter(s -> function.equalsIgnoreCase(s.getMsFunction()))
                .collect(Collectors.toList());
    }


    public List<MicroserviceInfoMetrics> getMetrics() {
        List<MicroserviceInfo> msInfoValues = getRegisteredServices();
        List<MicroserviceInfoMetrics> result = new ArrayList<>();
        for (MicroserviceInfo msInfo: msInfoValues) {
            if ("calculation".equalsIgnoreCase(msInfo.getMsType())) {
                String url = getUrlFromMsInfo(msInfo);
                Integer workload = iFeignClient.getWorkloadByMsType(URI.create(url), msInfo.getMsFunction());
                result.add(new MicroserviceInfoMetrics(msInfo, workload));
            }
        }
        return result;
    }

    private void registerMs(MicroserviceInfo msInfo, String msUrl) {
        List<MicroserviceInfo> microserviceInfos = registeredMicroservices.get(msUrl);
        if (microserviceInfos == null) {
            microserviceInfos = new ArrayList<>();
        }
        microserviceInfos.add(msInfo);
        registeredMicroservices.put(msUrl, microserviceInfos);
    }

    private String getUrlFromMsInfo(MicroserviceInfo msInfo) {
        return "http://" + msInfo.getMsName() + ":" + msInfo.getMsPort();
    }

    private void deregisterMicroservice(String msUrl) {
        List<MicroserviceInfo> msToRemove = registeredMicroservices.remove(msUrl);
        registeredMicroservices.remove(msUrl);
        for (MicroserviceInfo msInfo: msToRemove) {
            log.info("Successfully removed: {}", msInfo);
        }
    }

    private void isServiceRegistered(MicroserviceInfo msInfo, String msUrl) throws ServiceAlreadyRegisteredException {
        List<MicroserviceInfo> registeredMsInfo = registeredMicroservices.get(msUrl);
        if (registeredMsInfo != null) {
            if (registeredMsInfo.stream().anyMatch(msInfo::equals)) {
                log.warn("Microservice is already registered: {}", msInfo);
                throw new ServiceAlreadyRegisteredException(ServiceAlreadyRegisteredException.class.getSimpleName(),
                        "This microservice is already registered.");
            }
        }
    }

    @Scheduled(fixedRate = 1000)
    private void removeDeadMicroservices() {
        List<String> toRemove = new ArrayList<>();
        for (String msUrl: registeredMicroservices.keySet()) {
            try {
                if (!"OK".equals(checkHealth(msUrl))) {
                    toRemove.add(msUrl);
                }
            } catch (ResourceAccessException ignore) {
                toRemove.add(msUrl);
            }
        }
        for (String ms: toRemove) {
            deregisterMicroservice(ms);
        }
    }

    private String checkHealth(String msUrl) {
        URI uri = URI.create(msUrl);
//        System.out.println(iFeignClient.health(uri));
        return retryService.retryWhenError(uri);
    }

}
