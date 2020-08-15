package com.example.playground.ui.bluetoothPrint;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.playground.BaseActivity;
import com.example.playground.R;
import com.zj.btsdk.BluetoothService;

import java.util.Set;

public class DeviceActivity extends AppCompatActivity {

    TextView tvPerangkatTerpasang, tvTitleNewDevice;
    ListView listPairedDevice, listNewDevice;
    Button btnScan;

    public static final String EXTRA_DEVICE_ADDRESS = "device_address";
    private BluetoothService mService = null;
    private ArrayAdapter<String> newDeviceAdapter;

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    newDeviceAdapter.add(device.getName() + "\n" + device.getAddress());
                } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                    setTitle("Pilih Perangkat");
                    if (newDeviceAdapter.getCount() == 0) {
                        newDeviceAdapter.add("Perangkat tidak ditemukan");
                    }
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);
        setTitle("Perangkat Bluetooth");

        tvPerangkatTerpasang = findViewById(R.id.tvPerangkatTerpasang);
        tvTitleNewDevice = findViewById(R.id.tvTitleNewDevice);
        listPairedDevice = findViewById(R.id.listPairedDevice);
        listNewDevice = findViewById(R.id.listNewDevice);
        btnScan = findViewById(R.id.btnScan);

        ArrayAdapter<String> pairDeviceAdapter = new ArrayAdapter<>(this, R.layout.device_name);
        listPairedDevice.setAdapter(pairDeviceAdapter);
        listPairedDevice.setOnItemClickListener(mDeviceClickListener);

        IntentFilter intentFilter =  new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, intentFilter);

        newDeviceAdapter = new ArrayAdapter<>(this, R.layout.device_name);
        listNewDevice.setAdapter(newDeviceAdapter);
        listNewDevice.setOnItemClickListener(mDeviceClickListener);

        intentFilter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(mReceiver, intentFilter);

        mService = new BluetoothService(this, null);

        Set<BluetoothDevice> pairedDevice = mService.getPairedDev();

        if (pairedDevice.size() > 0) {
            tvPerangkatTerpasang.setVisibility(View.VISIBLE);
            for (BluetoothDevice device : pairedDevice) {
                pairDeviceAdapter.add(device.getName() + "\n" + device.getAddress());
            }
        } else {
            String noDevice = "Tidak ada perangkat terhubung!";
            pairDeviceAdapter.add(noDevice);
        }
    }

    private AdapterView.OnItemClickListener mDeviceClickListener = (adapterView, view, i, l) -> {
        mService.cancelDiscovery();

        String info = ((TextView) view).getText().toString();
        String address = info.substring(info.length() - 17);

        Intent intent = new Intent();
        intent.putExtra(EXTRA_DEVICE_ADDRESS, address);

        setResult(RESULT_OK, intent);
        finish();
    };

    private void doDiscovery() {
        setTitle("Sedang mencari perangkat");
        tvTitleNewDevice.setVisibility(View.VISIBLE);

        if (mService.isDiscovering()) {
            mService.cancelDiscovery();
        }

        mService.startDiscovery();
    }

    public void scan(View view) {
        doDiscovery();
        view.setVisibility(View.GONE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mService != null) {
            mService.cancelDiscovery();
        }

        mService = null;
        unregisterReceiver(mReceiver);
    }
}