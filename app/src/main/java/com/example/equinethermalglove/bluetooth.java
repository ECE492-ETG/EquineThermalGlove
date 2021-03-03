package com.example.equinethermalglove;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.IBinder;

import androidx.annotation.Nullable;

public class bluetooth extends Service {

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

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
