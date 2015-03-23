package team4.weekathon.com.sitapp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Ori on 3/22/2015.
 */
public class SensorsChair
{
    private POSTURE_STATUS mPostureStatus;
    private int numberOfBadState;
    private Map<SENSORS_CODE_LIST, Sensor> sensorsList;
    private static SensorsChair _instance = new SensorsChair();

    public enum POSTURE_STATUS
    {
        OK,
        BAD
    }

    public enum SENSORS_CODE_LIST
    {
        DUMMY_SENSOR,
        LOWER_BACK_SENSOR_NAME,
        UPPER_BACK_SENSOR_NAME,
        SITTING_BONE_SENSOR_NAME,
        FEET_SENSOR_NAME,
        ARM,
        MAX,

    }



    public static SensorsChair getInstance()
    {
        if (_instance == null)
        {
            _instance = new SensorsChair();
        }
        return _instance;
    }

    public SensorsChair()
    {
        sensorsList =  new HashMap<SENSORS_CODE_LIST, Sensor>();

        sensorsList.put(SENSORS_CODE_LIST.LOWER_BACK_SENSOR_NAME, new Sensor(SENSORS_CODE_LIST.LOWER_BACK_SENSOR_NAME.name(),150 ));
        sensorsList.put(SENSORS_CODE_LIST.UPPER_BACK_SENSOR_NAME, new Sensor(SENSORS_CODE_LIST.UPPER_BACK_SENSOR_NAME.name(),120 ));
        sensorsList.put(SENSORS_CODE_LIST.ARM, new Sensor(SENSORS_CODE_LIST.ARM.name(),100 ));
        sensorsList.put(SENSORS_CODE_LIST.FEET_SENSOR_NAME, new Sensor(SENSORS_CODE_LIST.FEET_SENSOR_NAME.name(),60 ));
        sensorsList.put(SENSORS_CODE_LIST.SITTING_BONE_SENSOR_NAME, new Sensor(SENSORS_CODE_LIST.FEET_SENSOR_NAME.name(), 98));

        mPostureStatus = POSTURE_STATUS.OK;
        numberOfBadState = 0;
    }

    public void updateSensor(SENSORS_CODE_LIST sensorCode, String state)
    {
        Sensor currentSensor = sensorsList.get(sensorCode);
        if(currentSensor != null) {
            currentSensor.setState(state);

            if(currentSensor.isState())
            {
                if(numberOfBadState > 0)
                {
                    numberOfBadState--;

                    if(numberOfBadState == 0)
                    {
                        mPostureStatus = POSTURE_STATUS.OK;
                    }
                }
            }
            else
            {
                numberOfBadState++;
                mPostureStatus = POSTURE_STATUS.BAD;
            }
        }


    }


    public POSTURE_STATUS getmPostureStatus()
    {
        return mPostureStatus;
    }

    public Sensor getSensor(SENSORS_CODE_LIST sensorCode)
    {
        return sensorsList.get(sensorCode);
    }
}
