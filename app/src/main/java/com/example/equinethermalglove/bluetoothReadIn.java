package com.example.equinethermalglove;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class bluetoothReadIn extends AppCompatActivity {

    private final static String TAG = bluetoothReadIn.class.getSimpleName();

    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";

    private bluetooth btService;
    private String deviceName;
    private String deviceAddress;
    private boolean connected = false;
    private BluetoothGattCharacteristic notifyCharacteristic;

    private TextView thumbTemp;
    private TextView indexTemp;
    private TextView middleTemp;
    private TextView ringTemp;
    private TextView pinkieTemp;
    private TextView batteryVal;
    private TextView connectionState;
    private Button saveBtn;

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
        thumbTemp = findViewById(R.id.temperature_value_thumb);
        indexTemp = findViewById(R.id.temperature_value_index);
        middleTemp = findViewById(R.id.temperature_value_middle);
        ringTemp = findViewById(R.id.temperature_value_ring);
        pinkieTemp = findViewById(R.id.temperature_value_pinkie);
        batteryVal = findViewById(R.id.battery_value);
        connectionState = findViewById(R.id.connection_state);
        saveBtn = findViewById(R.id.save_measurement_button);

        Intent gattServiceIntent = new Intent(this, bluetooth.class);
        if (!bindService(gattServiceIntent, serviceConnection, Context.BIND_AUTO_CREATE)) {
            Log.e(TAG, "Bluetooth service failed to bind.");
        }

        saveBtn.setOnClickListener(v -> {
            try {
                Intent i = new Intent(bluetoothReadIn.this, displayNewHorse.class);
                ArrayList<Double> data = new ArrayList<>();
                data.add(Double.valueOf(thumbTemp.getText().toString()));
                data.add(Double.valueOf(indexTemp.getText().toString()));
                data.add(Double.valueOf(middleTemp.getText().toString()));
                data.add(Double.valueOf(ringTemp.getText().toString()));
                data.add(Double.valueOf(pinkieTemp.getText().toString()));
                i.putExtra("data", data);
                startActivity(i);
            } catch(Exception e) {
                Log.e(TAG, "Failed to save data");
                Toast.makeText(bluetoothReadIn.this, "Couldn't save data", Toast.LENGTH_SHORT).show();
            }
        });
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
                try {
                    readTemperature();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else if (bluetooth.ACTION_DATA_AVAILABLE.equals(action)) {
                if (intent.hasExtra(bluetooth.THUMB_DATA)) {
                    displayData("thumb", intent.getStringExtra(bluetooth.THUMB_DATA));
                }
                if (intent.hasExtra(bluetooth.INDEX_DATA)) {
                    displayData("index", intent.getStringExtra(bluetooth.INDEX_DATA));
                }
                if (intent.hasExtra(bluetooth.MIDDLE_DATA)) {
                    displayData("middle", intent.getStringExtra(bluetooth.MIDDLE_DATA));
                }
                if (intent.hasExtra(bluetooth.RING_DATA)) {
                    displayData("ring", intent.getStringExtra(bluetooth.RING_DATA));
                }
                if (intent.hasExtra(bluetooth.PINKIE_DATA)) {
                    displayData("pinkie", intent.getStringExtra(bluetooth.PINKIE_DATA));
                }
                if (intent.hasExtra(bluetooth.BATT_DATA)) {
                    displayData("battery", intent.getStringExtra(bluetooth.BATT_DATA));
                }
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

    private void displayData(String field, String data) {
        if (field.equals("thumb")) {
            thumbTemp.setText(data);
        }
        if (field.equals("index")) {
            indexTemp.setText(data);
        }
        if (field.equals("middle")) {
            middleTemp.setText(data);
        }
        if (field.equals("ring")) {
            ringTemp.setText(data);
        }
        if (field.equals("pinkie")) {
            pinkieTemp.setText(data);
        }
        if (field.equals("battery")) {
            batteryVal.setText(data);
        }
    }

    private void readTemperature() throws InterruptedException {
        BluetoothGattCharacteristic thumb_temp = null;
        BluetoothGattCharacteristic index_temp = null;
        BluetoothGattCharacteristic middle_temp = null;
        BluetoothGattCharacteristic ring_temp = null;
        BluetoothGattCharacteristic pinkie_temp = null;
        BluetoothGattCharacteristic battery_life = null;
        List<BluetoothGattService> services = btService.getSupportedGattServices();
        for (int i = 0; i < services.size(); i++) {
            BluetoothGattService gattService = services.get(i);
            if (gattService.getUuid().toString().equalsIgnoreCase(btService.UUID_ETG_Service.toString())) {
                thumb_temp = gattService.getCharacteristic(btService.UUID_ETG_Temperature_Thumb);
                index_temp = gattService.getCharacteristic(btService.UUID_ETG_Temperature_Index);
                middle_temp = gattService.getCharacteristic(btService.UUID_ETG_Temperature_Middle);
                ring_temp = gattService.getCharacteristic(btService.UUID_ETG_Temperature_Ring);
                pinkie_temp = gattService.getCharacteristic(btService.UUID_ETG_Temperature_Pinkie);
                battery_life = gattService.getCharacteristic(btService.UUID_ETG_Battery);
            }
        }
        if (thumb_temp == null || index_temp == null || middle_temp == null ||
            ring_temp == null || pinkie_temp == null || battery_life == null) {
            Log.e(TAG, "UUID_ETG_Service not found.");
            return;
        }
        btService.setCharacteristicNotification(thumb_temp, true);
        Thread.sleep(200);
        btService.setCharacteristicNotification(index_temp, true);
        Thread.sleep(200);
        btService.setCharacteristicNotification(middle_temp, true);
        Thread.sleep(200);
        btService.setCharacteristicNotification(ring_temp, true);
        Thread.sleep(200);
        btService.setCharacteristicNotification(pinkie_temp, true);
        Thread.sleep(200);
        btService.setCharacteristicNotification(battery_life, true);
    }
}
