import com.fasterxml.jackson.databind.ObjectMapper;
import models.Purchase;
import models.PurchaseItem;
import okhttp3.*;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;


public class APIClient {
        private MediaType JSON;
        private OkHttpClient client;
        private String postBody;
        private String url;
        private Random r;

        public APIClient(Properties properties, int storeID) throws Exception {
            this.JSON = MediaType.get("application/json; charset=utf-8");
            this.client = new OkHttpClient();
            this.r = new Random();
            this.postBody = this.generatePostBody(properties);
            this.url = this.generateUrl(properties, storeID);
        }

        public String getUrl(){return this.url;}
        public String getPostBody(){return this.postBody;}

        private String generateUrl(Properties props, int storeID){
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


        private String generatePostBody(Properties props) throws Exception {
            Random r = new Random();
            Purchase purchase = new Purchase();
            List<PurchaseItem> items = new ArrayList<>();
            int numPurchases = Integer.parseInt(props.getProperty("purchasePerHour"));
            int maxItemID = Integer.parseInt(props.getProperty("maxItemId"));
            int itemsPerPurchase = Integer.parseInt(props.getProperty("itemsPerPurchase"));
            // Create PurchaseItem instances
            for (int i=0; i<numPurchases; i++){
                int itemId = this.r.ints(0, maxItemID).findFirst().getAsInt();
                PurchaseItem item = new PurchaseItem();
                item.setItemID(String.valueOf(itemId));
                item.setNumberOfItems(itemsPerPurchase);
                items.add(item);
            }
            purchase.setItems(items);
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(purchase);
        }


        public String post() throws IOException {
            RequestBody body = RequestBody.create(this.JSON, this.postBody);
            Request request = new Request.Builder()
                    .url(this.url)
                    .post(body)
                    .build();
            try (Response response = this.client.newCall(request).execute()) {
                return response.body().string();
            }
        }

}
