package com.example.equinethermalglove;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.pm.PackageManager;


public class bluetooth {

    // TODO: add logic for bluetooth connections
    public void newConn() {

    }

    public void readIn() {

    }

    public void terminateConn() {

    }

    boolean isBleSupported(Context context) {
        return BluetoothAdapter.getDefaultAdapter() != null
                && context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
    }

}
