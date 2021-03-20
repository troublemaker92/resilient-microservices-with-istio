package univie.distributedcalculation.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;

@Data
@AllArgsConstructor
public class MicroserviceInfo {

    private final String msName;
    private final int msPort;
    private final String msDescription;
    private final String msFunction;

}
