package team4.weekathon.com.sitapp;

/**
 * Created by Ori on 3/25/2015.
 */
public class HandPowerSensor extends Sensor {

    private int previousValue;
    private boolean firstTimeOfUse = true;
    public HandPowerSensor(String sensorDesc, int thresholdMin, int thresholdMax) {
        super(sensorDesc, thresholdMin, thresholdMax);
    }


    @Override
    public boolean setState(String state) {
        boolean isChangedState = true;
        int value = Integer.parseInt(state);


        if (previousValue == value)
        {
            isChangedState = false;
        }

        previousValue = value;

        return isChangedState;
    }

    public int getPreviousValue()
    {
        return previousValue;
    }
}
