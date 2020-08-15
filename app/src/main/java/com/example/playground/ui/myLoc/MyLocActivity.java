package com.example.playground.ui.myLoc;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Button;

import com.example.playground.BaseActivity;
import com.example.playground.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;

public class MyLocActivity extends BaseActivity implements OnMapReadyCallback {

    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQ_CODE = 101;
    Button btnKantor, btnRumah, btnLoc;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_loc);

        btnKantor = findViewById(R.id.btnKantor);
        btnRumah = findViewById(R.id.btnRumah);
        btnLoc = findViewById(R.id.btnLoc);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        fetchLocation();
        statusCheck();
    }

    private void fetchLocation() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQ_CODE);
            return;
        }

        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(location -> {
            if (location != null) {
                currentLocation = location;
                SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.myMaps);
                assert supportMapFragment != null;
                supportMapFragment.getMapAsync(MyLocActivity.this);
            }
        });
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;
        final LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        final MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("I'm Here");
        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
        mMap.addMarker(markerOptions);

        btnLoc.setOnClickListener(view -> {
            mMap.clear();

            final LatLng latLng1 = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            final MarkerOptions markerOptions1 = new MarkerOptions().position(latLng1).title("I'm Here");
            mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng1));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng1, 17));
            mMap.addMarker(markerOptions1);
        });

        btnKantor.setOnClickListener(view -> {
            double lat = -6.971397;
            double longitude = 107.5685863;
            LatLng coorKantor = new LatLng(lat, longitude);

            mMap.clear();

            MarkerOptions options = new MarkerOptions().position(coorKantor);
            mMap.animateCamera(CameraUpdateFactory.newLatLng(coorKantor));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(coorKantor, 17));
            mMap.addMarker(options);
        });

        btnRumah.setOnClickListener(view -> {
            double lat = -6.8545628;
            double longitude = 107.5982555;
            LatLng coorKantor = new LatLng(lat, longitude);

            mMap.clear();

            MarkerOptions options = new MarkerOptions().position(coorKantor);
            mMap.animateCamera(CameraUpdateFactory.newLatLng(coorKantor));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(coorKantor, 17));
            mMap.addMarker(options);
        });
    }

    public void statusCheck() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();

        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }
}