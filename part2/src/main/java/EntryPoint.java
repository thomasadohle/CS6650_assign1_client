import okhttp3.OkHttpClient;

import javax.xml.crypto.Data;
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
        OkHttpClient client = new OkHttpClient();
        AtomicInteger successCounter = new AtomicInteger(0);
        AtomicInteger failureCounter = new AtomicInteger(0);
        PhaseBlocker blocker = PhaseBlocker.getInstance();
        Properties props = getProps();
        int numStores = Integer.parseInt(props.getProperty("maxStores"));
        int hourlyPurchases = Integer.parseInt(props.getProperty("purchasePerHour"));
        Thread[] threads = new Thread[numStores];
        Date start = new Date();
        System.out.println("Starting phase 1");
        for (int i=0; i<numStores/4; i++){
            RequestRunnable rt = new RequestRunnable(
                    client,
                    i,
                    hourlyPurchases,
                    successCounter,
                    failureCounter
            );
            Thread t = new Thread(rt);
            threads[i] = t;
            threads[i].start();
        }
        while(! blocker.phaseTwo()){
            synchronized(blocker){
                blocker.wait();
            }
        }
        System.out.println("Starting phase 2");
        for (int j=numStores/4; j<numStores/2; j++){
            RequestRunnable rt = new RequestRunnable(
                    client,
                    j,
                    hourlyPurchases,
                    successCounter,
                    failureCounter
            );
            Thread t = new Thread(rt);
            threads[j] = t;
            threads[j].start();
        }
        while(! blocker.phaseThree()){
            synchronized(blocker){
                blocker.wait();
            }
        }
        System.out.println("Starting phase 3");
        for (int k=numStores/2; k<numStores; k++){
            RequestRunnable rt = new RequestRunnable(
                    client,
                    k,
                    hourlyPurchases,
                    successCounter,
                    failureCounter
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
        DataRecorder recorder = DataRecorder.getInstance();
        DataCalculator calculator = new DataCalculator(recorder.getFileName());
        System.out.println(calculator.printData());
    }
}
