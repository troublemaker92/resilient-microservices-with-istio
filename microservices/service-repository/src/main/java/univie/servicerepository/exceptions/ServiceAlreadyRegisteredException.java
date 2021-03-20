package univie.servicerepository.exceptions;

public class ServiceAlreadyRegisteredException extends GenericException {

    public ServiceAlreadyRegisteredException(String errorName, String errorMessage) {
        super(errorName, errorMessage);
    }

}
