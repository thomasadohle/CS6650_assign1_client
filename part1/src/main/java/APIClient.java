import com.fasterxml.jackson.databind.ObjectMapper;
import models.Purchase;
import models.PurchaseItem;
import okhttp3.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;


public class APIClient {
    /**
     * Provides a simple way to generate/send HTTP POST requests
     * Generates the URL from attributes in runtime.properties
     */
        private final MediaType JSON;
        private final OkHttpClient client;
        private final String postBody;
        private final String url;
        private final Random r;
        private final Properties props;

        public APIClient(int storeID) throws Exception {
            InputStream input = new FileInputStream("src/runtime.properties");
            this.props = new Properties();
            this.props.load(input);
            this.JSON = MediaType.get("application/json; charset=utf-8");
            this.client = new OkHttpClient();
            this.r = new Random();
            this.postBody = this.generatePostBody();
            this.url = this.generateUrl(storeID);
        }

        public String getUrl(){return this.url;}
        public String getPostBody(){return this.postBody;}

        private String generateUrl(int storeID){
            int custPerStore = Integer.parseInt(props.getProperty("custPerStore"));
            String date = props.getProperty("date");
            String url = props.getProperty("baseUrl");
            int customerID = this.r.ints(storeID*1000, storeID*1000 + custPerStore).findFirst().getAsInt();
            url += String.valueOf(storeID);
            url += "/customer/";
            url += String.valueOf(customerID);
            url += "/date/";
            url += date;
            return url;
        }


        private String generatePostBody() throws Exception {
            Random r = new Random();
            Purchase purchase = new Purchase();
            List<PurchaseItem> items = new ArrayList<>();
            int maxItemID = Integer.parseInt(props.getProperty("maxItemId"));
            int itemsPerPurchase = Integer.parseInt(props.getProperty("itemsPerPurchase"));
            // Create PurchaseItem instances
            for (int i=0; i<itemsPerPurchase; i++){
                int itemId = this.r.ints(0, maxItemID).findFirst().getAsInt();
                PurchaseItem item = new PurchaseItem();
                item.setItemID(String.valueOf(itemId));
                item.setNumberOfItems(1);
                items.add(item);
            }
            purchase.setItems(items);
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(purchase);
        }

        public Response post() throws IOException {
            RequestBody body = RequestBody.create(this.JSON, this.postBody);
            Response response;
            Request request = new Request.Builder()
                    .url(this.url)
                    .post(body)
                    .build();
            try{
                response = this.client.newCall(request).execute();
            } catch(SocketTimeoutException | SocketException ste){
                ste.printStackTrace();
                response = this.client.newCall(request).execute();
            }
            response.close();
            return response;
        }
}
