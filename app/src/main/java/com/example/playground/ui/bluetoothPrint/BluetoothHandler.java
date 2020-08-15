package com.example.playground.ui.bluetoothPrint;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import com.zj.btsdk.BluetoothService;

public class BluetoothHandler extends Handler {

    private static final String TAG = BluetoothHandler.class.getSimpleName();
    private HandleInterface handleInterface;

    public BluetoothHandler(HandleInterface handleInterface) {
        this.handleInterface = handleInterface;
    }

    @Override
    public void handleMessage(@NonNull Message msg) {
        switch (msg.what) {
            case BluetoothService.MESSAGE_STATE_CHANGE:
                switch (msg.arg1) {
                    case BluetoothService.STATE_CONNECTED:
                        handleInterface.onDeviceConnected();
                        break;
                    case BluetoothService.STATE_CONNECTING:
                        handleInterface.onDeviceConnecting();
                        break;
                    case BluetoothService.STATE_LISTEN:
                    case BluetoothService.STATE_NONE:
                        Log.i(TAG, "Bluetooth state listen or none");
                        break;
                }
                break;
            case BluetoothService.MESSAGE_CONNECTION_LOST:
                handleInterface.onDeviceConnectionLost();
                break;
            case BluetoothService.MESSAGE_UNABLE_CONNECT:
                handleInterface.onDeviceUnableToConnect();
                break;
        }
    }

    public interface HandleInterface {
        void onDeviceConnected();
        void onDeviceConnecting();
        void onDeviceConnectionLost();
        void onDeviceUnableToConnect();
    }

}
