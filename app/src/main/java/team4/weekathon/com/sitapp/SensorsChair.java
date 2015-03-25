package team4.weekathon.com.sitapp;

import java.util.HashMap;
import java.util.Map;

import rx.subjects.PublishSubject;

/**
 * Created by Ori on 3/22/2015.
 */
public class SensorsChair
{
    private POSTURE_STATUS mPostureStatus;
    private int numberOfBadState;
    private Map<SENSORS_CODE_LIST, Sensor> sensorsList;
    private static SensorsChair _instance = new SensorsChair();
    private PublishSubject<SENSORS_CODE_LIST> updateNotifier;

    public enum POSTURE_STATUS
    {
        OK,
        BAD
    }

    public enum SENSORS_CODE_LIST
    {
        DUMMY_SENSOR,
        UPPER_BACK_SENSOR_NAME,
        LOWER_BACK_SENSOR_NAME,
        HAND_POWER,
        ARM,
        SITTING_BONE_SENSOR_NAME,
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
        updateNotifier = PublishSubject.create();
        sensorsList =  new HashMap<SENSORS_CODE_LIST, Sensor>();

        sensorsList.put(SENSORS_CODE_LIST.LOWER_BACK_SENSOR_NAME, new Sensor(SENSORS_CODE_LIST.LOWER_BACK_SENSOR_NAME.name(),400 , 600));
        sensorsList.put(SENSORS_CODE_LIST.UPPER_BACK_SENSOR_NAME, new Sensor(SENSORS_CODE_LIST.UPPER_BACK_SENSOR_NAME.name(), 400, 600 ));
        sensorsList.put(SENSORS_CODE_LIST.ARM, new Sensor(SENSORS_CODE_LIST.ARM.name(),400 , 600 ));
        sensorsList.put(SENSORS_CODE_LIST.HAND_POWER, new Sensor(SENSORS_CODE_LIST.HAND_POWER.name(),400, 600 ));
        sensorsList.put(SENSORS_CODE_LIST.SITTING_BONE_SENSOR_NAME, new FlexSensor(SENSORS_CODE_LIST.SITTING_BONE_SENSOR_NAME.name(), 444, 447));

        mPostureStatus = POSTURE_STATUS.OK;
        numberOfBadState = 0;
    }

    public rx.Observable<SENSORS_CODE_LIST> getUpdateNotifier()
    {
        return updateNotifier.asObservable();
    }

    public void updateSensor(SENSORS_CODE_LIST sensorCode, String state)
    {
        Sensor currentSensor = sensorsList.get(sensorCode);
        if(currentSensor != null)
        {

            boolean isChangedState = currentSensor.setState(state);

            if(currentSensor.isSitting())
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

            if(isChangedState)
            {
                updateNotifier.onNext(sensorCode);
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
