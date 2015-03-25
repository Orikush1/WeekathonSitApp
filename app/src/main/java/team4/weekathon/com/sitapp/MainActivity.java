package team4.weekathon.com.sitapp;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import rx.Subscriber;
import rx.android.observables.AndroidObservable;


public class MainActivity extends ActionBarActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private int badPostureNotificationNumber;

    private View mUpperBack;
    private View mLowerBack;
    private View mSittingBone;
    private View mArm;
    private ImageView mGeneralPostureState;
    private Button mWorkoutExercise;
    private Button mStaticWorkoutExercise;

    private byte[] readBuffer = new byte[1024];
    private static final byte delimiter =  (byte) '\n';
    private OutputStream outStream;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mUpperBack = findViewById(R.id.upper_back);
        mLowerBack = findViewById(R.id.lower_back);
        mSittingBone = findViewById(R.id.sitting_bone);
        mArm = findViewById(R.id.arm);
        mGeneralPostureState = (ImageView) findViewById(R.id.traffic_light);
        mWorkoutExercise = (Button) findViewById(R.id.workout_button);
        mStaticWorkoutExercise = (Button) findViewById(R.id.static_work);
        BluetoothHandler.getInstance().initContext(getApplicationContext());
        boolean isConnected = BluetoothHandler.getInstance().CheckBluetoothConnection();

        mWorkoutExercise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, HandsExercise.class);
                startActivity(intent);
            }
        });

        mStaticWorkoutExercise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, StaticExerciseActivity.class);
                startActivity(intent);
            }
        });

        rx.Observable<SensorsChair.SENSORS_CODE_LIST> updateSensorUI = SensorsChair.getInstance().getUpdateNotifier();
        updateSensorUI =  AndroidObservable.bindActivity(this,updateSensorUI);
        updateSensorUI.subscribe(new Subscriber<SensorsChair.SENSORS_CODE_LIST>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(SensorsChair.SENSORS_CODE_LIST sensorCode) {
                updateUIState(sensorCode);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
            case R.id.history:
                Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private static int i = 0;

    private void writeData(String data)
    {
        i++;
        Log.i(TAG, " i = " + i);
        try
        {
            outStream = BluetoothHandler.getInstance().getOutputStream();
        } catch (IOException e)
        {
            Log.d(TAG, "Bug BEFORE Sending stuff", e);
        }

        String message = data;
        /* In my example, I put a button that invoke this method and send a string to it */
        byte[] msgBuffer = message.getBytes();

        try
        {
            outStream.write(msgBuffer);
        }
        catch (IOException e)
        {
            Log.e(TAG, "Bug while sending stuff", e);
        }
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void updateUIState(SensorsChair.SENSORS_CODE_LIST code)
    {
        Sensor currentSensor = SensorsChair.getInstance().getSensor(code);

        if(currentSensor != null) {
            int color = (currentSensor.isSitting()) ? R.drawable.circle_green : R.drawable.circle_red;

            switch (code) {
                case UPPER_BACK_SENSOR_NAME:
                    mUpperBack.setBackground(getResources().getDrawable(color));
                    break;
                case LOWER_BACK_SENSOR_NAME:
                    mLowerBack.setBackground(getResources().getDrawable(color));
                    break;
                case ARM:
                    mArm.setBackground(getResources().getDrawable(color));
                    break;
                case SITTING_BONE_SENSOR_NAME:
                    mSittingBone.setBackground(getResources().getDrawable(color));
                    break;
                default:
                    Log.i(TAG, "Couldn't detect data by sensor ");
            }

            updateGeneralPostureState();
        }
    }

    private void updateGeneralPostureState()
    {
        SensorsChair.POSTURE_STATUS state = SensorsChair.getInstance().getmPostureStatus();

        if(state == SensorsChair.POSTURE_STATUS.BAD)
        {
            mGeneralPostureState.setImageResource(R.drawable.traffic_light_red);
            if(badPostureNotificationNumber % 5 == 0)
            {
                writeData("a");
            }
            badPostureNotificationNumber++;
        }
        else
        {
            mGeneralPostureState.setImageResource(R.drawable.traffic_light_green);
        }
    }


}
