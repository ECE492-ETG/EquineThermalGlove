package com.example.equinethermalglove.ble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import androidx.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class BleManager {
    // Log
    private final static String TAG = BleManager.class.getSimpleName();

    // Singleton
    private static BleManager mInstance = null;

    // Data
    private BluetoothManager bluetoothManager;
    private BluetoothAdapter bluetoothAdapter;

    public static BleManager getInstance() {
        if (mInstance == null) {
            mInstance = new BleManager();
        }
        return mInstance;
    }

    private BleManager() {
    }

    public boolean start(Context context) {

        // Init Manager
        bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);

        // Init Adapter
        if (bluetoothManager != null) {
            bluetoothAdapter = bluetoothManager.getAdapter();
        } else {
            bluetoothAdapter = null;
        }

        final boolean isEnabled = bluetoothAdapter != null && bluetoothAdapter.isEnabled();
        if (!isEnabled) {
            Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
        }

        return isEnabled;
    }

    /*
    public @Nullable
    BluetoothAdapter getAdapter() {
        return mAdapter;
    }*/

    public void cancelDiscovery() {
        if (bluetoothAdapter != null) {
            bluetoothAdapter.cancelDiscovery();
        }
    }

    public boolean isAdapterEnabled() {
        return bluetoothAdapter != null && bluetoothAdapter.isEnabled();
    }

    public @NonNull
    List<BluetoothDevice> getConnectedDevices() {
        List<BluetoothDevice> connectedDevices = new ArrayList<>();

        // Check if already initialized
        if (bluetoothManager == null) {
            return connectedDevices;
        }

        List<BluetoothDevice> devices = bluetoothManager.getConnectedDevices(BluetoothProfile.GATT);
        for (BluetoothDevice device : devices) {
            final int type = device.getType();
            if (type == BluetoothDevice.DEVICE_TYPE_LE || type == BluetoothDevice.DEVICE_TYPE_DUAL) {
                connectedDevices.add(device);
            }
        }

        return connectedDevices;
    }
}