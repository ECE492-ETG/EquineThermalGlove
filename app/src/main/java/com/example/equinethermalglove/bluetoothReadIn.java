package com.example.equinethermalglove;

import android.bluetooth.BluetoothGattCharacteristic;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class bluetoothReadIn extends AppCompatActivity {

    private final static String TAG = bluetoothReadIn.class.getSimpleName();

    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";

    private bluetooth btService;
    private String deviceName;
    private String deviceAddress;
    private boolean connected = false;
    private BluetoothGattCharacteristic notifyCharacteristic;

    private TextView tempVal;
    private TextView connectionState;

    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            btService = ((bluetooth.LocalBinder) service).getService();
            if (!btService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            // Automatically connects to the device upon successful start-up initialization.
            final boolean result = btService.connect(deviceAddress);
            Log.d(TAG, "Connect request result=" + result);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            btService = null;
        }
    };

    // TODO: use bluetooth class to read in data and send it to saveNewData activity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_read_in);

        final Intent intent = getIntent();
        deviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
        deviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);

        ((TextView) findViewById(R.id.device_name_readin)).setText(deviceName);
        ((TextView) findViewById(R.id.device_address_readin)).setText(deviceAddress);
        tempVal = findViewById(R.id.temperature_value);
        connectionState = findViewById(R.id.connection_state);

        Intent gattServiceIntent = new Intent(this, bluetooth.class);
        if (!bindService(gattServiceIntent, serviceConnection, Context.BIND_AUTO_CREATE)) {
            Log.e(TAG, "Bluetooth service failed to bind.");
        }
    }

    // Handles various events fired by the Service.
    // ACTION_GATT_CONNECTED: connected to a GATT server.
    // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
    // ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
    // ACTION_DATA_AVAILABLE: received data from the device. This can be a
    // result of read or notification operations.
    private final BroadcastReceiver gattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (bluetooth.ACTION_GATT_CONNECTED.equals(action)) {
                connected = true;
                updateConnectionState("Connected");
            } else if (bluetooth.ACTION_GATT_DISCONNECTED.equals(action)) {
                connected = false;
                updateConnectionState("Disconnected");
            } else if (bluetooth.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // Show all the supported services and characteristics on the
                // user interface.
//                displayGattServices(bluetooth.getSupportedGattServices());
            } else if (bluetooth.ACTION_DATA_AVAILABLE.equals(action)) {
                displayData(intent.getStringExtra(bluetooth.EXTRA_DATA));
            }
        }
    };

    private void updateConnectionState(String newConnState) {
        connectionState.setText(newConnState);
        Toast.makeText(getApplicationContext(), newConnState, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(gattUpdateReceiver, makeGattUpdateIntentFilter());
        if (btService!= null) {
            final boolean result = btService.connect(deviceAddress);
            Log.d(TAG, "Connect request result=" + result);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(gattUpdateReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(serviceConnection);
        btService = null;
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(com.example.equinethermalglove.bluetooth.ACTION_GATT_CONNECTED);
        intentFilter.addAction(com.example.equinethermalglove.bluetooth.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(com.example.equinethermalglove.bluetooth.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(com.example.equinethermalglove.bluetooth.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

    private void displayData(String data) {
//        Intent intent = new Intent(this, displayNewHorse.class);
//        // TODO: get data from bluetooth and send to new class
//        // intent.putExtra();
//        startActivity(intent);
        if (data != null) {
            tempVal.setText(data);
        }
    }
}
