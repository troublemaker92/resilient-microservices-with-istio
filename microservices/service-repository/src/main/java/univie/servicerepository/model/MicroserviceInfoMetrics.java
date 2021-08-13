package univie.servicerepository.model;

import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class MicroserviceInfoMetrics {

    private MicroserviceInfo microserviceInfo;
    private Integer workload;

    public MicroserviceInfoMetrics(MicroserviceInfo microserviceInfo, Integer workload) {
        this.microserviceInfo = microserviceInfo;
        this.workload = workload;
    }

}
