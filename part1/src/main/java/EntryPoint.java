import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class EntryPoint {

    public static Properties getProps() throws IOException {
        InputStream input = new FileInputStream("src/runtime.properties");
        Properties props = new Properties();
        props.load(input);
        return props;
    }

    public static void main(String[] args) throws Exception{
        AtomicInteger successCounter = new AtomicInteger(0);
        AtomicInteger failureCounter = new AtomicInteger(0);
        AtomicBoolean startPhaseTwo = new AtomicBoolean(false);
        AtomicBoolean startPhaseThree = new AtomicBoolean(false);
        Properties props = getProps();
        int numStores = Integer.parseInt(props.getProperty("maxStores"));
        int hourlyPurchases = Integer.parseInt(props.getProperty("purchasePerHour"));
        Thread[] threads = new Thread[numStores];
        Date start = new Date();
        for (int i=0; i<numStores/4; i++){
            RequestRunnable rt = new RequestRunnable(
                    i,
                    hourlyPurchases,
                    successCounter,
                    failureCounter,
                    startPhaseTwo,
                    startPhaseThree
            );
            Thread t = new Thread(rt);
            threads[i] = t;
            threads[i].start();
        }
        while(! startPhaseTwo.get()){
            synchronized(startPhaseTwo){
                startPhaseTwo.wait();
            }
        }
        for (int j=numStores/4; j<numStores/2; j++){
            RequestRunnable rt = new RequestRunnable(
                    j,
                    hourlyPurchases,
                    successCounter,
                    failureCounter,
                    startPhaseTwo,
                    startPhaseThree
            );
            Thread t = new Thread(rt);
            threads[j] = t;
            threads[j].start();
        }
        while(! startPhaseThree.get()){
            synchronized(startPhaseThree){
                startPhaseThree.wait();
            }
        }
        for (int k=numStores/2; k<numStores; k++){
            RequestRunnable rt = new RequestRunnable(
                    k,
                    hourlyPurchases,
                    successCounter,
                    failureCounter,
                    startPhaseTwo,
                    startPhaseThree
            );
            Thread t = new Thread(rt);
            threads[k] = t;
            threads[k].start();
        }
        for (int i=0; i<numStores;i++){
            threads[i].join();
        }
        Date end = new Date();
        System.out.println("Success: " + successCounter.toString());
        System.out.println("Failure: " + failureCounter.toString());
        double wallTime = ((double)end.getTime()-(double)start.getTime())/1000.0;
        System.out.println("Wall time: " + wallTime);
        System.out.println("Throughput: " + ((double)successCounter.get() + (double)failureCounter.get())/wallTime);
    }
}
