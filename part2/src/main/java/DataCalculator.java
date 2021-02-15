import org.apache.commons.math.stat.descriptive.rank.Median;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DataCalculator {
    private List<DataRow> data;
    private double meanLatency;
    private double medianLatency;
    // Inspiration for how to calculate this https://stackoverflow.com/questions/41413544/calculate-percentile-from-a-long-array
    private double latencyPercentile;
    private int maxLatency;

    public DataCalculator(String filename){
        data = new ArrayList<>();
        this.generateDate(filename);
        this.calculateStats();
    }

    public String printData(){
        String output = "Mean response time: " + this.meanLatency + "\n";
        output+= "Median response time: " + this.medianLatency + "\n";
        output+= "99th Percentile: " + this.latencyPercentile + "\n";
        output+= "Max latency: " + this.maxLatency + "\n";
        return output;
    }

    private void generateDate(String filename){
        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));
            String line;
            while((line=br.readLine()) !=null){
                data.add(new DataRow(line.split(",")));
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    private void calculateStats(){
        int[] latencies = new int[this.data.size()];
        int latenciesSum = 0;
        for (int i=0; i<this.data.size(); i++){
            int latency = this.data.get(i).getLatency();
            latencies[i] = latency;
            latenciesSum += latency;
        }
        Arrays.sort(latencies);
        // Calculate median latency
        if (latencies.length % 2 == 0){
            this.medianLatency = ((double)latencies[latencies.length/2] + (double)latencies[latencies.length/2-1])/2;
        } else{
            this.medianLatency = (double)latencies[latencies.length/2];
        }
        // Calculate mean latency
        this.meanLatency = (double)latenciesSum/latencies.length;
        // Calculate 99th percentile
        int index = (int)Math.ceil(99.0/100.0*latencies.length);
        this.latencyPercentile = latencies[index-1];

        this.maxLatency = latencies[latencies.length-1];
    }


    public List<DataRow> getData(){return this.data;}

    public static void main(String[] args){
        DataCalculator calc = new DataCalculator("post_data_1613401702661.csv");
        System.out.println(calc.getData().size());
    }



}
