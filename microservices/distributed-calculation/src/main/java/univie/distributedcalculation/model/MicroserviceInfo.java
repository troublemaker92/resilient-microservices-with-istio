package univie.distributedcalculation.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;

@Data
@AllArgsConstructor
public class MicroserviceInfo {

    public final String msName;
    public final int msPort;
    public final String msDescription;
    public final String msFunction;

}
