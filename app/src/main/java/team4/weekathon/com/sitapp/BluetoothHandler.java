package team4.weekathon.com.sitapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
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
        return true;
    }
}
