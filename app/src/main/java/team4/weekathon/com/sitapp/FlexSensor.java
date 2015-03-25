package team4.weekathon.com.sitapp;

/**
 * Created by Ori on 3/25/2015.
 */
public class FlexSensor extends Sensor
{
    public FlexSensor(String sensorDesc, int thresholdMin, int thresholdMax) {
        super(sensorDesc, thresholdMin, thresholdMax);
    }

    @Override
    public boolean setState(String state)
    {
        boolean isChangedState = false;

        int value = Integer.parseInt(state);
        if(this.state == false)
        {
            if(value < thresholdMin)
            {
                this.state = true;
                isChangedState = true;
            }
        }
        else
        {
            if(value > thresholdMax)
            {
                this.state = false;
                isChangedState = true;
            }
        }

        return isChangedState;
    }
}
