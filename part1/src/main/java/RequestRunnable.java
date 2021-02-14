import okhttp3.Response;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A Runnable that sends HTTP Requests using APIClient.
 */
public class RequestRunnable implements Runnable {

    private final int storeId;
    private final APIClient client;
    private final AtomicInteger successCounter;
    private final AtomicInteger failureCounter;
    private final int numPurchases;
    private final AtomicBoolean startPhaseTwo;
    private final AtomicBoolean startPhaseThree;

    public RequestRunnable(
            int storeId,
            int numPurchases,
            AtomicInteger successCounter,
            AtomicInteger failureCounter,
            AtomicBoolean startPhaseTwo,
            AtomicBoolean startPhaseThree
    ) throws Exception {
        this.storeId = storeId;
        this.client = new APIClient(storeId);
        this.successCounter = successCounter;
        this.failureCounter = failureCounter;
        this.numPurchases = numPurchases;
        this.startPhaseTwo = startPhaseTwo;
        this.startPhaseThree = startPhaseThree;
    }

    @Override
    public void run() {
        for (int i=0; i<numPurchases*3; i++){
            this.executePost();
        }
        // Alert Main thread to start Phase 2
        this.startPhaseTwo.set(true);
        synchronized(startPhaseTwo){
            startPhaseTwo.notify();
        }

        for (int j=numPurchases*3; j<numPurchases*5; j++){
            this.executePost();
        }
        // Alert Main thread to start Phase 3
        this.startPhaseThree.set(true);
        synchronized(startPhaseThree){
            startPhaseThree.notify();
        }
        for (int k=numPurchases*5; k<numPurchases*9; k++){
            this.executePost();
        }

    }

    private void executePost(){
        /**
         * Call post() method on APIClient and incrememnt counters if request is successful or not
         */
        try {
            Response resp = client.post();
            if (resp.isSuccessful()){
                successCounter.incrementAndGet();
            }
            else {
                failureCounter.incrementAndGet();
            }
        } catch (Exception e) {
            e.printStackTrace();
            failureCounter.incrementAndGet();
        }
    }
}
