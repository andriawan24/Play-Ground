package com.example.playground.ui.bluetoothPrint;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.playground.BaseActivity;
import com.example.playground.R;
import com.zj.btsdk.BluetoothService;
import com.zj.btsdk.PrintPic;

import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

@SuppressLint("SetTextI18n")
public class PrintActivity extends BaseActivity implements EasyPermissions.PermissionCallbacks, BluetoothHandler.HandleInterface {

    private EditText etText;
    private TextView tvStatus;
    private Button btnPrintText, btnPrintImage;
    private ImageView ivQr;

    private final String TAG = PrintActivity.class.getSimpleName();
    public static final int RC_BLUETOOTH = 0;
    public static final int RC_CONNECT_DEVICE = 1;
    public static final int RC_ENABLE_BLUETOOTH = 2;

    private BluetoothService mService = null;
    private boolean isPrintReady = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print);

        etText = findViewById(R.id.etText);
        tvStatus = findViewById(R.id.tvStatus);
        btnPrintImage = findViewById(R.id.btnPrintImage);
        btnPrintText = findViewById(R.id.btnPrintText);
        ivQr = findViewById(R.id.ivQr);

        setupBluetooth();
    }

    @AfterPermissionGranted(RC_BLUETOOTH)
    private void setupBluetooth() {
        String[] params = {Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN};
        if (!EasyPermissions.hasPermissions(this, params)) {
            EasyPermissions.requestPermissions(this, "You need bluetooth permission", RC_BLUETOOTH, params);
            return;
        }

        mService = new BluetoothService(this, new BluetoothHandler(this));
    }

    @Override
    public void onDeviceConnected() {
        isPrintReady = true;
        tvStatus.setText("Terhubung dengan perangkat");
    }

    @Override
    public void onDeviceConnecting() {
        tvStatus.setText("Sedang menghubungkan...");
    }

    @Override
    public void onDeviceConnectionLost() {
        isPrintReady = false;
        tvStatus.setText("Koneksi perangkat terputus");
    }

    @Override
    public void onDeviceUnableToConnect() {
        tvStatus.setText("Tidak dapat terhubung ke perangkat");
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        // TODO: 02/08/2020 do something
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        // TODO: 02/08/2020 do something
    }

    private void requestBluetooth() {
        if (mService != null) {
            if (!mService.isBTopen()) {
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(intent, RC_ENABLE_BLUETOOTH);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RC_ENABLE_BLUETOOTH:
                if (resultCode == RESULT_OK) {
                    Log.i(TAG, "onActivityResult: bluetooth aktif");
                } else {
                    Log.i(TAG, "onActivityResult: bluetoot harus aktif untuk menggunakan fitur ini");
                }
                break;
            case RC_CONNECT_DEVICE:
                if (resultCode == RESULT_OK) {
                    String address = data.getExtras().getString(DeviceActivity.EXTRA_DEVICE_ADDRESS);
                    BluetoothDevice mDevice = mService.getDevByMac(address);
                    mService.connect(mDevice);
                }
        }
    }

    public void printText(View view) {
        if (!mService.isAvailable()) {
            Log.i(TAG, "printText: perangkat tidak support bluetooth");
            return;
        }

        if (isPrintReady) {
            if (etText.getText().toString().isEmpty()) {
                Toast.makeText(this, "Can't print null text", Toast.LENGTH_SHORT).show();
                return;
            }

            mService.write(PrinterCommands.ESC_ALIGN_CENTER);
            mService.sendMessage(etText.getText().toString(), "");
            mService.write(PrinterCommands.ESC_ENTER);
        } else {
            if (mService.isBTopen())
                startActivityForResult(new Intent(this, DeviceActivity.class), RC_CONNECT_DEVICE);
            else
                requestBluetooth();
        }
    }

    public void printImage(View view) {
        if (isPrintReady) {
            PrintPic pg = new PrintPic();
            pg.initCanvas(400);
            pg.initPaint();
            pg.drawImage(0, 0, Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download/ember1.jpg");
            byte[] sendData = pg.printDraw();
            mService.write(PrinterCommands.ESC_ALIGN_CENTER);
            mService.write(sendData);
        }
    }
}