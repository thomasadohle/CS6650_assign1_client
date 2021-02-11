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

        public APIClient(Properties properties, int storeID){
            this.JSON = MediaType.get("application/json; charset=utf-8");
            this.client = new OkHttpClient();

            //this.postBody = this.generatePostBody(properties);
            this.url = this.generateUrl(properties, storeID);
        }

        public String getUrl(){return this.url;}

        private String generateUrl(Properties props, int storeID){
            Random r = new Random();
            int custPerStore = Integer.parseInt(props.getProperty("custPerStore"));
            String date = props.getProperty("date");
            String url = props.getProperty("baseUrl");
            int customerID = r.ints(storeID*1000, storeID*1000 + custPerStore).findFirst().getAsInt();
            url += String.valueOf(storeID);
            url += "/customer/";
            url += String.valueOf(customerID);
            url += "/date/";
            url += date;
            return url;
        }


        private String generatePostBody(Properties props){
            Purchase purchase = new Purchase();
            List<PurchaseItem> items = new ArrayList<>();
            return "";}


        String post(String url, String json) throws IOException {
            RequestBody body = RequestBody.create(JSON, json);
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();
            try (Response response = client.newCall(request).execute()) {
                return response.body().string();
            }
        }

        String generatePostData(){
            JSONObject inner = new JSONObject();
            inner.put("itemID", "5");
            inner.put("numberOfItems", "2");
            JSONObject outer = new JSONObject();
            JSONObject[] in = new JSONObject[1];
            in[0] = inner;
            outer.put("items", in);
           return outer.toString();
        }

    public static void main(String[] args) throws FileNotFoundException, IOException {
//        Please example = new Please();
//        String url = "http://localhost:8083/java_8_war_exploded/purchase/5/customer/10/date/20210202";
//        String response = example.post(url, example.generatePostData());
//        System.out.println(response);
    }
}
