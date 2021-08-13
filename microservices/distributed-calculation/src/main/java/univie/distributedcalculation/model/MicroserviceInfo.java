package univie.distributedcalculation.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MicroserviceInfo {

    public final String msName;
    public final int msPort;
    public final String msDescription;
    public final String msFunction;
    public final String msType;

}
