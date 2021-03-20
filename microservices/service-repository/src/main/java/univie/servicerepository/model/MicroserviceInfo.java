package univie.servicerepository.model;

import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class MicroserviceInfo {

    @NotNull(message = "User name cannot be empty")
    @Size(min=1, message = "User name cannot be empty")
    private String msName;

    @NotNull(message = "User name cannot be empty")
    @Range(min = 1, max = 65535, message = "Port should be between {min} and {max}")
    private int msPort;

    @NotNull(message = "User name cannot be empty")
    @Size(min=1, message = "User name cannot be empty")
    private String msDescription;

    @NotNull(message = "User name cannot be empty")
    @Size(min=1, message = "User name cannot be empty")
    private String msFunction;

}
