package univie.distributedcalculation.service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

public class Fibonacci implements Runnable {

    private int index;
    private BigInteger par_n3 = new BigInteger("0");
    private BigInteger par_n6 = new BigInteger("0");
    private Map<Integer, BigInteger> fibList = new HashMap<>();


    public Fibonacci() {}

    /**
     * Fibonacci class that implements Runnable interface
     * F(n) = 4 * F(n-3) + F(n-6) - With this formula calculations divided into 3 threads
     * @param index An index which should be founded
     * @param par_n3 N - 3
     * @param par_n6 N - 6
     * @param fibList A map of fibonacci numbers, key - index of fibonacci, value - a value which contains a given key
     */
    public Fibonacci(int index, BigInteger par_n3, BigInteger par_n6, Map<Integer, BigInteger> fibList) {
        this.index = index;
        this.par_n3 = par_n3;
        this.par_n6 = par_n6;
        this.fibList = fibList;
    }

    /**
     * Fill first values of Fibonacci, to start calculations of the Fibonacci numbers
     * @param fib A map that should be filled
     */
    public void fillFirstElements(Map<Integer, BigInteger> fib){
        fib.put(0, new BigInteger("0"));
        fib.put(1, new BigInteger("1"));
        fib.put(2, new BigInteger("1"));
        fib.put(3, new BigInteger("2"));
        fib.put(4, new BigInteger("3"));
        fib.put(5, new BigInteger("5"));
        fib.put(6, new BigInteger("8"));
    }

    public Map<Integer, BigInteger> getFibList() {
        return fibList;
    }

    @Override
    public void run() {
        fibList.put(index, par_n3.multiply(new BigInteger("4")).add(par_n6));
    }
}

