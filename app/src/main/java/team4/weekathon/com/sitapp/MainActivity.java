package team4.weekathon.com.sitapp;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
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
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import team4.weekathon.com.workoutchair.R;


public class MainActivity extends ActionBarActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private OutputStream outStream = null;
    private InputStream inStream = null;
    private Handler handler = new Handler();
    private static final byte delimiter =  (byte) '\n';
    private boolean stopWorker = false;
    private int readBufferPosition = 0;
    private byte[] readBuffer = new byte[1024];


    private TextView blueToothMessage;

    private View mUpperBack;
    private View mLowerBack;
    private View mSittingBone;
    private View mArm;
    private ImageView mGeneralPostureState;
    private Button mWorkoutExercise;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mUpperBack = findViewById(R.id.upper_back);
        mLowerBack = findViewById(R.id.lower_back);
        mSittingBone = findViewById(R.id.sitting_bone);
        mArm = findViewById(R.id.arm);
        blueToothMessage = (TextView)findViewById(R.id.blue_text);
        mGeneralPostureState = (ImageView) findViewById(R.id.traffic_light);
        mWorkoutExercise = (Button) findViewById(R.id.workout_button);
        BluetoothHandler.getInstance().initContext(getApplicationContext());
        boolean isConnected = BluetoothHandler.getInstance().CheckBluetoothConnection();

        if(isConnected)
        {
            beginListenForData();
            mWorkoutExercise.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    Intent intent = new Intent(MainActivity.this, WorkoutActivity.class);
                    startActivity(intent);
                }
            });
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopWorker = true;

    }


    @Override
    protected void onResume() {
        super.onResume();
        stopWorker = false;
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
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void beginListenForData()
    {
        try
        {
            inStream = BluetoothHandler.getInstance().getInputStream();
        }
        catch (IOException e)
        {
            Log.e(TAG, "Error to get input stream from bt" + e);
        }

        Thread workerThread = new Thread(new Runnable()
        {
            public void run()
            {
                while(!Thread.currentThread().isInterrupted() && !stopWorker)
                {
                    try
                    {
                        int bytesAvailable = inStream.available();
                        if(bytesAvailable > 0)
                        {
                            byte[] packetBytes = new byte[bytesAvailable];
                            inStream.read(packetBytes);
                            for(int i = 0 ;i < bytesAvailable; i++)
                            {
                                byte b = packetBytes[i];
                                if(b == delimiter)
                                {
                                    byte[] encodedBytes = new byte[readBufferPosition];
                                    System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                                    final String data = new String(encodedBytes, "US-ASCII");
                                    readBufferPosition = 0;
                                    handler.post(new Runnable()
                                    {
                                        public void run()
                                        {
                                            Log.i("SENSOR_DATA", "OUR BT Result - " + data);
                                            detectData(data);
                                        }
                                    });
                                }
                                else
                                {
                                    readBuffer[readBufferPosition++] = b;
                                }
                            }
                        }
                    }
                    catch (IOException ex)
                    {
                        stopWorker = true;
                    }
                }
            }
        });

        workerThread.start();
    }

    private void writeData(String data)
    {
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

    public void detectData(String data)
    {
        String parsedData[] = data.split(":");
        int sensorName = Integer.parseInt(parsedData[0]);
        String value = parsedData[1].replace("\r", "");
        if(value != null || !value.equals(""))
        {
            SensorsChair.SENSORS_CODE_LIST  code = SensorsChair.SENSORS_CODE_LIST.values()[sensorName];
            switch(code)
            {
                case UPPER_BACK_SENSOR_NAME:
                case LOWER_BACK_SENSOR_NAME:
                case FEET_SENSOR_NAME:
                case SITTING_BONE_SENSOR_NAME:
                    SensorsChair.getInstance().updateSensor(code,  value);
                    updateUIState(code);
                    break;
                default:
                    Log.i(TAG, "Couldn't detect data by sensor " + data);
                    blueToothMessage.setText(data);
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void updateUIState(SensorsChair.SENSORS_CODE_LIST code)
    {
        Sensor currentSensor = SensorsChair.getInstance().getSensor(code);

        if(currentSensor != null) {
            int color = (currentSensor.isState()) ? R.drawable.circle_green : R.drawable.circle_red;

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
                    blueToothMessage.setText("Something found");
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
        }
        else
        {
            mGeneralPostureState.setImageResource(R.drawable.traffic_light_green);
        }
    }


}
