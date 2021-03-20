package univie.distributedcalculation.service;

import java.util.ArrayList;
import java.util.List;

public class Prime implements Runnable {

    private Integer threadId = null;
    private Integer limit = null;

    // Number of threads, that is supported, to make calculations parallel
    final static int THREADS = Runtime.getRuntime().availableProcessors();
    private List<Integer> primeNumbers = new ArrayList<>();


    public Prime() {}

    /**
     * Prime class that implements Runnable interface
     * @param threadId A current thread's number
     * @param limit The last element, all values should be calculated before limit
     * @param primeNumbers A list of prime numbers
     */
    public Prime(Integer threadId, Integer limit, List<Integer> primeNumbers) {
        this.limit = limit;
        this.threadId = threadId;
        this.primeNumbers = primeNumbers;
    }

    /**
     * Checks if a number prime or not
     * @param number A number that should be proved, whether it is prime or not
     * @return Whether a digit prime or not
     */
    public boolean isPrime(Integer number) {
        int end = number / 2;
        for(int i = 2; i <= end; i++)
            if(number % i == 0)
                return false;
        return true;
    }

    /**
     * everyBlock is a number of current values divided by available threads, in order to divide a work fairly between all threads
     */
    @Override
    public void run() {
        int everyBlock = limit / THREADS;
        int end = everyBlock * threadId + 2;
        for(int i = end - everyBlock; i < end; i++){
            if(isPrime(i)){
                synchronized (primeNumbers){
                    primeNumbers.add(i);
                }
            }
        }
    }
}
