package univie.servicerepository.network;

import org.springframework.beans.factory.annotation.Autowired;
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
    public String retryWhenError(URI uri) {
        return iFeignClient.health(uri);
    }

    @Recover
    public String recover(Exception e) throws Exception {
        return null;
    }


}
