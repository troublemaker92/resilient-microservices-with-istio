package univie.servicerepository.exceptions;

import lombok.Data;

@Data
public class ExceptionInfo {
    public final String errorName;
    public final String errorMessage;

    public ExceptionInfo(String errorName, Exception errorMessage) {
        this.errorName = errorName;
        this.errorMessage = errorMessage.getLocalizedMessage();
    }
}
