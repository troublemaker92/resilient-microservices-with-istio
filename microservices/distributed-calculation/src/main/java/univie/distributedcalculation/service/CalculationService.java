package univie.distributedcalculation.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class CalculationService {

    public BigDecimal add(BigDecimal firstDecimal, BigDecimal secondDecimal) {
        return firstDecimal.add(secondDecimal);
    }

}
