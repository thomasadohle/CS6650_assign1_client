import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public class EntryPoint {

    public static void main(String[] args) throws Exception{
        InputStream input = new FileInputStream("src/runtime.properties");
        Properties props = new Properties();
        props.load(input);
        APIClient client = new APIClient(props, 1);
        System.out.println(client.getUrl());
    }
}
