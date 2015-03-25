package team4.weekathon.com.sitapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * Created by Ori on 3/23/2015.
 */
public class BluetoothHandler
{
    private static final String TAG = BluetoothHandler.class.getSimpleName();
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothSocket btSocket;
    private static String address = "00:06:66:6A:4D:08";
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static BluetoothHandler _instance = new BluetoothHandler();
    private Context mContext;
    private boolean stopWorker;
    private InputStream inStream;
    private int readBufferPosition;
    private byte[] readBuffer = new byte[1024];
    private static final byte delimiter =  (byte) '\n';
    private Thread workerThread;


    public BluetoothHandler()
    {
    }

    public void initContext(Context context)
    {
        this.mContext = context;
    }

    public static BluetoothHandler getInstance()
    {
        if (_instance == null)
        {
            _instance = new BluetoothHandler();
        }
        return _instance;
    }

    public java.io.InputStream getInputStream() throws IOException {
        return btSocket.getInputStream();
    }

    public OutputStream getOutputStream() throws IOException { return btSocket.getOutputStream();}

    public boolean CheckBluetoothConnection()
    {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (!mBluetoothAdapter.isEnabled())
        {
            Toast.makeText(mContext, "Bluetooth Disabled !",
                    Toast.LENGTH_SHORT).show();
                   /* It tests if the bluetooth is enabled or not, if not the app will show a message. */

            return false;
        }
        else if(mBluetoothAdapter == null)
        {
            Toast.makeText(mContext, "Bluetooth null !", Toast.LENGTH_SHORT).show();
            return false;
        }
        else
        {
            return Connect();
        }

    }

    public boolean Connect() {
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        Log.d("", "Connecting to ... " + device);
        mBluetoothAdapter.cancelDiscovery();
        try {
            btSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
        /* Here is the part the connection is made, by asking the device to create a
        RfcommSocket (Unsecure socket I guess), It map a port for us or something like that */
            btSocket.connect();
            Log.d(TAG, "Connection made.");
        }
        catch (IOException e)
        {
            try
            {
                btSocket.close();
            }
            catch (IOException e2)
            {
                Log.d(TAG, "Unable to end the connection " + e2);
            }
            Log.d(TAG, "Socket creation failed");
            return false;
        }

        beginListenForData();
        return true;
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

        workerThread = new Thread(new Runnable()
        {
            public void run()
            {
                Log.d(TAG, "Thread BT");
                while(!Thread.currentThread().isInterrupted())
                {
                    if(!stopWorker)
                    {
                        try {
                            int bytesAvailable = inStream.available();
                            if (bytesAvailable > 0) {
                                byte[] packetBytes = new byte[bytesAvailable];
                                inStream.read(packetBytes);
                                for (int i = 0; i < bytesAvailable; i++) {
                                    byte b = packetBytes[i];
                                    if (b == delimiter)
                                    {
                                        byte[] encodedBytes = new byte[readBufferPosition];
                                        System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                                        final String data = new String(encodedBytes, "US-ASCII");
                                        readBufferPosition = 0;

                                        Log.i("SENSOR_DATA", "OUR BT Result - " + data);
                                        detectData(data);
                                    }
                                    else
                                    {
                                        readBuffer[readBufferPosition++] = b;
                                    }
                                }
                            }

                        } catch (IOException ex) {
                            stopWorker = true;
                        }
                    }
                }
            }
        });

        workerThread.start();
    }

    public void detectData(String data)
    {
        String parsedData[] = data.split(":");
        if(parsedData.length >= 2)
        {
            int sensorName = Integer.parseInt(parsedData[0]);
            String value = parsedData[1].replace("\r", "");
            if(value != null || !value.equals(""))
            {
                SensorsChair.SENSORS_CODE_LIST  code = SensorsChair.SENSORS_CODE_LIST.values()[sensorName];
                switch(code)
                {
                    case UPPER_BACK_SENSOR_NAME:
                    case LOWER_BACK_SENSOR_NAME:
                    case HAND_POWER:
                    case ARM:
                    case SITTING_BONE_SENSOR_NAME:
                        SensorsChair.getInstance().updateSensor(code,  value);
                        break;
                    default:
                        Log.i(TAG, "Couldn't detect data by sensor " + data);
                }
            }
        }
    }
}
