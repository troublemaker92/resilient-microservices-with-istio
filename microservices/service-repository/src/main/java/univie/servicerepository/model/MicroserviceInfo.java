package univie.servicerepository.model;

import lombok.Data;

@Data
public class MicroserviceInfo {

    private String msName;
    private int msPort;
    private String msDescription;
    private String msFunction;

}
