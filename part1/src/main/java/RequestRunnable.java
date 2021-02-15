import okhttp3.OkHttpClient;
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
    private final PhaseBlocker blocker = PhaseBlocker.getInstance();

    public RequestRunnable(
            OkHttpClient client,
            int storeId,
            int numPurchases,
            AtomicInteger successCounter,
            AtomicInteger failureCounter
    ) throws Exception {
        this.storeId = storeId;
        this.client = new APIClient(storeId, client);
        this.successCounter = successCounter;
        this.failureCounter = failureCounter;
        this.numPurchases = numPurchases;
    }

    @Override
    public void run() {
        for (int i=0; i<numPurchases*3; i++){
            this.executePost();
        }
        // Alert Main thread to start Phase 2
        synchronized(blocker){
            blocker.startPhaseTwo();
        }

        for (int j=numPurchases*3; j<numPurchases*5; j++){
            this.executePost();
        }
        // Alert Main thread to start Phase 3
        synchronized(blocker){
           blocker.startPhaseThree();
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
