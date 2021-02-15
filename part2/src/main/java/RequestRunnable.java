import okhttp3.OkHttpClient;
import okhttp3.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
    private final DataRecorder recorder = DataRecorder.getInstance();
    private List<String[]> records;
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
        this.records = new ArrayList<>();
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
        this.recorder.writeRecords(this.records);

    }

    private void executePost() {
        /**
         * Call post() method on APIClient and incrememnt counters if request is successful or not
         */
        Date start = new Date();
        Date end;
        int respCode;
        try {
            Response resp = client.post();
            if (resp.isSuccessful()){
                successCounter.incrementAndGet();
            }
            else {
                failureCounter.incrementAndGet();
            }
            end = new Date();
            respCode = resp.code();
        } catch (Exception e) {
            e.printStackTrace();
            failureCounter.incrementAndGet();
            end = new Date();
            respCode = 500;
        }
        String[] record = this.generateRecord(start, end, respCode);
        this.records.add(record);
    }

    private String[] generateRecord(Date start, Date end, int respCode){
        long latency = end.getTime() - start.getTime();
        String[] dataArr = new String[4];
        dataArr[0] = String.valueOf(start.getTime());
        dataArr[1] = "POST";
        dataArr[2] = String.valueOf(latency);
        dataArr[3] = String.valueOf(respCode);
        return dataArr;
    }


}
