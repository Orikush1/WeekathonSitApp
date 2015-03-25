package team4.weekathon.com.sitapp;

/**
 * Created by Ori on 3/23/2015.
 */
public class Sensor
{
    protected final int thresholdMin;
    protected final int thresholdMax;
    protected String sensorDesc;
    protected boolean state;

    public Sensor(String sensorDesc, int thresholdMin, int thresholdMax)
    {
        this.sensorDesc = sensorDesc;
        this.state = false;
        this.thresholdMin = thresholdMin;
        this.thresholdMax = thresholdMax;
    }

    public String getSensorDesc() {
        return sensorDesc;
    }

    public void setSensorDesc(String sensorDesc) {
        this.sensorDesc = sensorDesc;
    }

    public boolean isSitting() {
        return state;
    }

    public boolean setState(String state)
    {
        boolean isChangedState = false;

        int value = Integer.parseInt(state);
        if(this.state == true)
        {
            if(value < thresholdMin)
            {
                this.state = false;
                isChangedState = true;
            }
        }
        else
        {
            if(value > thresholdMax)
            {
                this.state = true;
                isChangedState = true;
            }
        }

        return isChangedState;
    }
}
