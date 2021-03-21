package univie.servicerepository.network;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.net.URI;

@Service
public class RetryService {

    @Autowired
    private IFeignClient iFeignClient;

    @Retryable(value = {Exception.class}, maxAttempts = 3, backoff = @Backoff(100))
    public HttpStatus retryWhenError(URI uri) {
        return iFeignClient.health(uri);
    }

    @Recover
    public HttpStatus recover(Exception e) throws Exception {
        return HttpStatus.NOT_FOUND;
    }


}
