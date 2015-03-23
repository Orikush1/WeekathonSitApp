package team4.weekathon.com.sitapp;

/**
 * Created by Ori on 3/23/2015.
 */
public class Sensor
{
    private final int threshold;
    private String sensorDesc;
    private boolean state;

    public Sensor(String sensorDesc, int threshold)
    {
        this.sensorDesc = sensorDesc;
        this.state = false;
        this.threshold = threshold;
    }

    public String getSensorDesc() {
        return sensorDesc;
    }

    public void setSensorDesc(String sensorDesc) {
        this.sensorDesc = sensorDesc;
    }

    public boolean isState() {
        return state;
    }

    public void setState(String state) {
        int value = Integer.parseInt(state);
        if (value < threshold){
            this.state = true;
        }
        else
        {
            this.state = false;
        }
    }
}
