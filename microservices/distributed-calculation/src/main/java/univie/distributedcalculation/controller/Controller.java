package univie.distributedcalculation.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import univie.distributedcalculation.model.CalculationObject;
import univie.distributedcalculation.model.ECalculationType;
import univie.distributedcalculation.model.MicroserviceInfo;
import univie.distributedcalculation.service.CalculationService;
import univie.distributedcalculation.service.Fibonacci;
import univie.distributedcalculation.service.Prime;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class Controller {

    private final int SLEEP_TIMEOUT = 10000;

    private Map<ECalculationType, AtomicInteger> endpointWorkload;

    @Value("${server.port}")
    private int port;

    @Value("${ms1.url}")
    private String serviceRepositoryUrl;

    @Autowired
    private CalculationService calculationService;

    public void registerMe() {
        String hostName = null;
        try {
            hostName = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        List<MicroserviceInfo> endpointsToRegister = List.of(
                new MicroserviceInfo(hostName, port, "Addition of two numbers.", "add"),
                new MicroserviceInfo(hostName, port, "Multiplication of two numbers.", "mult"),
                new MicroserviceInfo(hostName, port, "Calculate prime numbers.", "prime"),
                new MicroserviceInfo(hostName, port, "Calculate fibonacci value for the given number.", "fibonacci"));
        System.out.println("endpoints = " + endpointsToRegister);
        System.out.println("repository URL = " + serviceRepositoryUrl);
        System.out.println("port = " + port);
        for (MicroserviceInfo ms: endpointsToRegister) {
            new RestTemplate().postForObject(serviceRepositoryUrl, ms, String.class);
        }
    }

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

    /**
     * Calculate Fibonacci number
     * @param n digit, for calculation the n-th Fibonacci number
     * @return result of calculation
     */
    public BigInteger calculateFibonacci(final Integer n) {
        endpointWorkload.get(ECalculationType.FIBONACCI).incrementAndGet();
        Fibonacci f = new Fibonacci();
        Map<Integer,BigInteger> fibMap = new HashMap<>();
        f.fillFirstElements(fibMap);
        if(n < 7) return fibMap.get(n);
        for(int i = 7; i <= n; i+=1){
            Runnable h1 = new Fibonacci(i, fibMap.get(i-3), fibMap.get(i-6), fibMap);
            Runnable h2 = new Fibonacci(i+1, fibMap.get(i-2), fibMap.get(i-5), fibMap);
            Runnable h3 = new Fibonacci(i+2, fibMap.get(i-1), fibMap.get(i-4),fibMap);
            Thread t1 = new Thread(h1);
            Thread t2 = new Thread(h2);
            Thread t3 = new Thread(h3);
            t1.start();
            t2.start();
            t3.start();
            try {
                t1.join();
                t2.join();
                t3.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            i += 2;
            if(fibMap.size() > 14){
                Map<Integer, BigInteger> temp = new HashMap<>();
                for(int j = 0; j < 7; j++){
                    temp.put(i-j, fibMap.get(i-j));
                }
                fibMap.clear();
                fibMap.putAll(temp);
            }

        }
        endpointWorkload.get(ECalculationType.FIBONACCI).decrementAndGet();
        return fibMap.get(n);
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
