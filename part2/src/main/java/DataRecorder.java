import com.opencsv.CSVWriter;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

public class DataRecorder {
    private static final DataRecorder instance = new DataRecorder();
    private final String fileName = "post_data_" + new Date().getTime() + ".csv";
    private DataRecorder(){}

    public static DataRecorder getInstance(){
        return instance;
    }

    public synchronized void writeRecord(Date start, Date end, int responseCode) {
        long latency = end.getTime() - start.getTime();
        try {
            CSVWriter writer = new CSVWriter(new FileWriter(fileName, true),',', CSVWriter.NO_QUOTE_CHARACTER);
            String[] dataArr = new String[4];
            dataArr[0] = String.valueOf(start.getTime());
            dataArr[1] = "POST";
            dataArr[2] = String.valueOf(latency);
            dataArr[3] = String.valueOf(responseCode);
            writer.writeNext(dataArr);
            writer.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        DataRecorder rec = DataRecorder.getInstance();
        rec.writeRecord(new Date(), new Date(), 404);
    }

    public String getFileName(){return this.fileName;}

}
