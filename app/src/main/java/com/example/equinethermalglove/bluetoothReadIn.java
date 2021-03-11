package com.example.equinethermalglove;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class bluetoothReadIn extends AppCompatActivity {

    private bluetooth bluetooth;
    private boolean connected = false;

    // TODO: use bluetooth class to read in data and send it to saveNewData activity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_read_in);
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
                updateConnectionState(R.string.connected);
                invalidateOptionsMenu();
            } else if (bluetooth.ACTION_GATT_DISCONNECTED.equals(action)) {
                connected = false;
                updateConnectionState(R.string.disconnected);
                invalidateOptionsMenu();
                clearUI();
            } else if (bluetooth.
                    ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // Show all the supported services and characteristics on the
                // user interface.
//                displayGattServices(bluetooth.getSupportedGattServices());
            } else if (bluetooth.ACTION_DATA_AVAILABLE.equals(action)) {
                displayData(intent.getStringExtra(bluetooth.EXTRA_DATA));
            }
        }
    };

    private void updateConnectionState(final int resourceId) {

    }

    private void clearUI() {

    }

    private void displayData(String data) {
        Intent intent = new Intent(this, displayNewHorse.class);
        // TODO: get data from bluetooth and send to new class
        // intent.putExtra();
        startActivity(intent);
    }

    // we may not need this
//    private void displayGattServices(List<BluetoothGattService> gattServices) {
//
//    }


}
