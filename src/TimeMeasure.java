/**
 * Created by Kristofer on 19/02/2017.
 */
public class TimeMeasure {

    private long start = 0;
    private long finish = 0;

    public TimeMeasure() {
    }

    public void start(){
        start = System.nanoTime();
    }

    public void stop(){
        finish = System.nanoTime();
    }

    /**
     * @return measured operation duration in nanoseconds
     */
    public long getDuration(){
        return finish-start;
    }

    public String getDurationString(){
        String result = "";
        long duration = getDuration();
        if (duration > 60000000000L){
            result += Long.toString(duration/60000000000L) + "min ";
            duration = duration % 60000000000L;
        }
        if (duration > 1000000000L){
            result += Long.toString(duration/1000000000L) + "s ";
            duration = duration % 1000000000L;
        }
        result += String.format("%.2f",duration/1000000d).replace(",",".") + "ms";
        return result;
    }
}
