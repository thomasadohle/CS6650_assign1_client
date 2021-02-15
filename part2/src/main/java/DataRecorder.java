import com.opencsv.CSVWriter;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.List;

public class DataRecorder {
    private static final DataRecorder instance = new DataRecorder();
    private final String fileName = "post_data_" + new Date().getTime() + ".csv";
    private DataRecorder(){}

    public static DataRecorder getInstance(){
        return instance;
    }

    public synchronized void writeRecord(String[] dataArr) {
        try {
            CSVWriter writer = new CSVWriter(new FileWriter(fileName, true),',', CSVWriter.NO_QUOTE_CHARACTER);
            writer.writeNext(dataArr);
            writer.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public synchronized void writeRecords(List<String[]> records) {
        try {
            CSVWriter writer = new CSVWriter(new FileWriter(fileName, true),',', CSVWriter.NO_QUOTE_CHARACTER);
            writer.writeAll(records, false);
            writer.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public String getFileName(){return this.fileName;}

}
