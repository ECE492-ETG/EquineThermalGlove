package com.example.equinethermalglove;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.UUID;

public class bluetooth extends Service {

    private final static String TAG = bluetooth.class.getSimpleName();

    private BluetoothManager bluetoothManager;
    private BluetoothAdapter bluetoothAdapter;
    private String bluetoothDeviceAddress;
    private BluetoothGatt bluetoothGatt;
    private int connectionState = STATE_DISCONNECTED;

    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;

    public final static String ACTION_GATT_CONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED =
            "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE =
            "com.example.bluetooth.le.ACTION_DATA_AVAILABLE";
    public final static String THUMB_DATA =
            "com.example.bluetooth.le.THUMB_DATA";
    public final static String INDEX_DATA =
            "com.example.bluetooth.le.INDEX_DATA";
    public final static String MIDDLE_DATA =
            "com.example.bluetooth.le.MIDDLE_DATA";
    public final static String RING_DATA =
            "com.example.bluetooth.le.RING_DATA";
    public final static String PINKIE_DATA =
            "com.example.bluetooth.le.PINKIE_DATA";
    public final static String BATT_DATA =
            "com.example.bluetooth.le.BATT_DATA";
    public final static String EXTRA_DATA =
            "com.example.bluetooth.le.EXTRA_DATA";

    // ETG UUID
    public final static UUID UUID_ETG_Service =
            UUID.fromString("089915a8-1528-437a-b378-f731190c0745");
    public final static UUID UUID_ETG_Temperature_Thumb =
            UUID.fromString("08990001-1528-437a-b378-f731190c0745");
    public final static UUID UUID_ETG_Temperature_Index =
            UUID.fromString("08990002-1528-437a-b378-f731190c0745");
    public final static UUID UUID_ETG_Temperature_Middle =
            UUID.fromString("08990003-1528-437a-b378-f731190c0745");
    public final static UUID UUID_ETG_Temperature_Ring =
            UUID.fromString("08990004-1528-437a-b378-f731190c0745");
    public final static UUID UUID_ETG_Temperature_Pinkie =
            UUID.fromString("08990005-1528-437a-b378-f731190c0745");
    public final static UUID UUID_ETG_Battery =
            UUID.fromString("08990006-1528-437a-b378-f731190c0745");

    // Various callback methods defined by the BLE API.
    private final BluetoothGattCallback gattCallback =
            new BluetoothGattCallback() {
                @Override
                public void onConnectionStateChange(BluetoothGatt gatt, int status,
                                                    int newState) {
                    String intentAction;
                    if (newState == BluetoothProfile.STATE_CONNECTED) {
                        intentAction = ACTION_GATT_CONNECTED;
                        connectionState = STATE_CONNECTED;
                        broadcastUpdate(intentAction);
                        Log.i(TAG, "Connected to GATT server.");
                        Log.i(TAG, "Attempting to start service discovery:" +
                                bluetoothGatt.discoverServices());

                    } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                        intentAction = ACTION_GATT_DISCONNECTED;
                        connectionState = STATE_DISCONNECTED;
                        Log.i(TAG, "Disconnected from GATT server.");
                        broadcastUpdate(intentAction);
                    }
                }

                @Override
                // New services discovered
                public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                    if (status == BluetoothGatt.GATT_SUCCESS) {
                        broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
                    } else {
                        Log.w(TAG, "onServicesDiscovered received: " + status);
                    }
                }

                @Override
                // Result of a characteristic read operation
                public void onCharacteristicRead(BluetoothGatt gatt,
                                                 BluetoothGattCharacteristic characteristic,
                                                 int status) {
                    if (status == BluetoothGatt.GATT_SUCCESS) {
                        broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
                    }
                }

                @Override
                public void onCharacteristicChanged(BluetoothGatt gatt,
                                                    BluetoothGattCharacteristic characteristic) {
                    broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
                }
            };

    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        sendBroadcast(intent);
    }

    private void broadcastUpdate(final String action,
                                 final BluetoothGattCharacteristic characteristic) {
        final Intent intent = new Intent(action);

        // This is special handling for the ETG Temperature Measurement
        if (UUID_ETG_Temperature_Thumb.equals(characteristic.getUuid())) {
            final byte[] data = characteristic.getValue();
            int temperature = data[0] & 0xff;
            Log.d(TAG, String.format("Received Thumb Temperature: %d", temperature));
            intent.putExtra(THUMB_DATA, String.valueOf(temperature));

        }
        if (UUID_ETG_Temperature_Index.equals(characteristic.getUuid())) {
            final byte[] data = characteristic.getValue();
            int temperature = data[0] & 0xff;
            Log.d(TAG, String.format("Received Index Temperature: %d", temperature));
            intent.putExtra(INDEX_DATA, String.valueOf(temperature));
        }
        if (UUID_ETG_Temperature_Middle.equals(characteristic.getUuid())) {
            final byte[] data = characteristic.getValue();
            int temperature = data[0] & 0xff;
            Log.d(TAG, String.format("Received Middle Temperature: %d", temperature));
            intent.putExtra(MIDDLE_DATA, String.valueOf(temperature));
        }
        if (UUID_ETG_Temperature_Ring.equals(characteristic.getUuid())) {
            final byte[] data = characteristic.getValue();
            int temperature = data[0] & 0xff;
            Log.d(TAG, String.format("Received Ring Temperature: %d", temperature));
            intent.putExtra(RING_DATA, String.valueOf(temperature));
        }
        if (UUID_ETG_Temperature_Pinkie.equals(characteristic.getUuid())) {
            final byte[] data = characteristic.getValue();
            int temperature = data[0] & 0xff;
            Log.d(TAG, String.format("Received Pinkie Temperature: %d", temperature));
            intent.putExtra(PINKIE_DATA, String.valueOf(temperature));
        }
        if (UUID_ETG_Battery.equals(characteristic.getUuid())) {
            final byte[] data = characteristic.getValue();
            int battery = data[0] & 0xff;
            Log.d(TAG, String.format("Received Battery Life: %d", battery));
            intent.putExtra(BATT_DATA, String.valueOf(battery));
        } else {
            // For all other profiles, writes the data formatted in HEX.
            final byte[] data = characteristic.getValue();
            if (data != null && data.length > 0) {
                final StringBuilder stringBuilder = new StringBuilder(data.length);
                for(byte byteChar : data)
                    stringBuilder.append(String.format("%02X ", byteChar));
                intent.putExtra(EXTRA_DATA, new String(data) + "\n" +
                        stringBuilder.toString());
            }
        }
        sendBroadcast(intent);
    }

    /**
     * Request a read on a given {@code BluetoothGattCharacteristic}. The read result is reported
     * asynchronously through the {@code BluetoothGattCallback#onCharacteristicRead(android.bluetooth.BluetoothGatt, android.bluetooth.BluetoothGattCharacteristic, int)}
     * callback.
     *
     * @param characteristic The characteristic to read from.
     */
    public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (bluetoothAdapter == null || bluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        bluetoothGatt.readCharacteristic(characteristic);
    }

    /**
     * Enables or disables notification on a give characteristic.
     *
     * @param characteristic Characteristic to act on.
     * @param enabled If true, enable notification.  False otherwise.
     */
    public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic,
                                              boolean enabled) {
        if (bluetoothAdapter == null || bluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        bluetoothGatt.setCharacteristicNotification(characteristic, enabled);

        // This is specific to ETG
        if (UUID_ETG_Temperature_Thumb.equals(characteristic.getUuid())) {
            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(
                    UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")); //tbh I'm not sure why the descriptor is this UUID
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            bluetoothGatt.writeDescriptor(descriptor);
        }
    }

    /**
     * Retrieves a list of supported GATT services on the connected device. This should be
     * invoked only after {@code BluetoothGatt#discoverServices()} completes successfully.
     *
     * @return A {@code List} of supported services.
     */
    public List<BluetoothGattService> getSupportedGattServices() {
        if (bluetoothGatt == null) return null;

        return bluetoothGatt.getServices();
    }

    public void close() {
        if (bluetoothGatt == null) {
            return;
        }
        bluetoothGatt.close();
        bluetoothGatt = null;
    }

    // This gets called within the ServiceConnection from bluetoothReadIn
    public boolean initialize() {
        // If bluetoothManager is null, try to set it
        if (bluetoothManager == null) {
            bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (bluetoothManager == null) {
                Log.e(TAG, "Unable to initialize BluetoothManager.");
                return false;
            }
        }
        // For API level 18 and higher, get a reference to BluetoothAdapter through
        // BluetoothManager.
        bluetoothAdapter = bluetoothManager.getAdapter();
        if (bluetoothAdapter == null) {
            Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
            return false;
        }
        return true;
    }

    public boolean connect(final String address) {
        if (bluetoothAdapter == null || address == null) {
            Log.w(TAG, "BluetoothAdapter not initialized or unspecified address.");
            return false;
        }

        // Previously connected device.  Try to reconnect.
        if (bluetoothDeviceAddress != null && address.equals(bluetoothDeviceAddress)
                && bluetoothGatt != null) {
            Log.d(TAG, "Trying to use an existing mBluetoothGatt for connection.");
            if (bluetoothGatt.connect()) {
                connectionState = STATE_CONNECTING;
                return true;
            } else {
                return false;
            }
        }

        final BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);
        if (device == null) {
            Log.w(TAG, "Device not found.  Unable to connect.");
            return false;
        }
        // We want to directly connect to the device, so we are setting the autoConnect
        // parameter to false.
        bluetoothGatt = device.connectGatt(this, false, gattCallback);
        Log.d(TAG, "Trying to create a new connection.");
        bluetoothDeviceAddress = address;
        connectionState = STATE_CONNECTING;
        return true;
    }

    /**
     * Disconnects an existing connection or cancel a pending connection. The disconnection result
     * is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */
    public void disconnect() {
        if (bluetoothAdapter == null || bluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        bluetoothGatt.disconnect();
    }

    boolean isBleSupported(Context context) {
        return BluetoothAdapter.getDefaultAdapter() != null
                && context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        // After using a given device, you should make sure that BluetoothGatt.close() is called
        // such that resources are cleaned up properly.
        close();
        return super.onUnbind(intent);
    }

    class LocalBinder extends Binder {
        bluetooth getService() {
            return bluetooth.this;
        }
    }

    private final IBinder binder = new LocalBinder();
}
