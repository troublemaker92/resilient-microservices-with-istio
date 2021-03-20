package univie.distributedcalculation.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import univie.distributedcalculation.model.CalculationObject;
import univie.distributedcalculation.model.ECalculationType;
import univie.distributedcalculation.service.CalculationService;
import univie.distributedcalculation.service.Prime;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class Controller {

    private final int SLEEP_TIMEOUT = 10000;

    private Map<ECalculationType, AtomicInteger> endpointWorkload;

    @Autowired
    private CalculationService calculationService;

    public String add(CalculationObject calculationObject) {
        endpointWorkload.get(ECalculationType.ADD).incrementAndGet();
        BigDecimal result = calculationService.add(calculationObject.getDecimalOne(), calculationObject.getDecimalTwo());
        sleep(SLEEP_TIMEOUT);
        endpointWorkload.get(ECalculationType.ADD).decrementAndGet();
        return String.valueOf(result);
    }

    public String multiply(CalculationObject calculationObject) {
        endpointWorkload.get(ECalculationType.MULT).incrementAndGet();
        BigDecimal result = calculationService.multiply(calculationObject.getDecimalOne(),
                calculationObject.getDecimalTwo());
        sleep(SLEEP_TIMEOUT);
        endpointWorkload.get(ECalculationType.MULT).decrementAndGet();
        return String.valueOf(result);
    }



    private void sleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Calculate Prime numbers
     * @param n digit, for calculation all numbers up to the n-th Prime number
     * @return A list of prime numbers to nth element
     */
    public List<Integer> calculatePrime(final Integer n) {
        endpointWorkload.get(ECalculationType.PRIME).incrementAndGet();

        List<Integer> primes = new ArrayList<>();
        Integer numThreads = univie.distributedcalculation.service.Prime.THREADS;
        Thread[] t = new Thread[numThreads + 1];

        univie.distributedcalculation.service.Prime p = new Prime();
        for(int i = 1; i < numThreads + 1; i++) {
            t[i] = new Thread(new Prime(i, n, primes));
            t[i].start();
        }

        int begin = (n - (n % numThreads)) + 2;
        for(; begin <= n; begin++){
            if(p.isPrime(begin))
                synchronized (primes){
                    primes.add(begin);
                }
        }

        for(int i = 1; i < numThreads + 1; i++){
            try {
                t[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        endpointWorkload.get(ECalculationType.PRIME).decrementAndGet();
        return primes;
    }

    @PostConstruct
    public void fillWorkloadMap() {
        endpointWorkload = Map.of(
                ECalculationType.ADD, new AtomicInteger(0),
                ECalculationType.FIBONACCI, new AtomicInteger(0),
                ECalculationType.MULT, new AtomicInteger(0),
                ECalculationType.PRIME, new AtomicInteger(0)
        );

    }


    public AtomicInteger getEndpointWorkloadByType(ECalculationType type) {
        return endpointWorkload.get(type);
    }
}
