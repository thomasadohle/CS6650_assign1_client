public class DataRow {
    private final long start;
    private final String requestType;
    private final int latency;
    private final int responseCode;

    public DataRow(String[] values){
        this.start = Long.valueOf(values[0]);
        this.requestType = values[1];
        this.latency = Integer.valueOf(values[2]);
        this.responseCode = Integer.valueOf(values[3]);
    }

    public long getStart(){return this.start;}

    public String getRequestType(){return this.requestType;}

    public int getLatency(){return this.latency;}

    public int getResponseCode(){return this.responseCode;}
}
