package com.example.playground;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.playground.ui.bluetoothPrint.PrintActivity;
import com.example.playground.ui.dragndrop.DragDropActivity;
import com.example.playground.ui.myLoc.MyLocActivity;
import com.google.android.material.navigation.NavigationView;

public abstract class BaseActivity extends AppCompatActivity {
    private FrameLayout view_stub; //This is the framelayout to keep your content view
    private NavigationView navigation_view; // The new navigation view from Android Design Library. Can inflate menu resources. Easy
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.app_base);

        view_stub = findViewById(R.id.viewStub);
        navigation_view = findViewById(R.id.navView);
        mDrawerLayout = findViewById(R.id.drawerLayout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, 0, 0);
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        setupNavigationView(navigation_view);
        mDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (Common.item != null) {
            Log.e("Ni apa", "onCreate: " + Common.item.getTitle());
            setTitle(Common.item.getTitle());
        }

        Common.item = null;
    }

    @Override
    public void setContentView(int layoutResID) {
        if (view_stub != null){
            LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
            );
            View stubView = inflater.inflate(layoutResID, view_stub, false);
            view_stub.addView(stubView, lp);
        }
    }

    @Override
    public void setContentView(View view) {
        if (view_stub != null) {
            ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
            );
            view_stub.addView(view, lp);
        }
    }


    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        if (view_stub != null) {
            view_stub.addView(view, params);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void selectedItem(MenuItem item) {
        Intent intent;

        Common.item = item;

        if (item.getItemId() == R.id.db) {
            intent = new Intent(this, MainActivity.class);
            Common.item = null;
        } else if (item.getItemId() == R.id.my_loc) {
            intent = new Intent(this, MyLocActivity.class);
        } else if (item.getItemId() == R.id.bluetoothPrint) {
            intent = new Intent(this, PrintActivity.class);
        } else if (item.getItemId() == R.id.dragndrop) {
            intent = new Intent(this, DragDropActivity.class);
        } else {
            intent = new Intent(this, MainActivity.class);
        }

        mDrawerLayout.closeDrawers();
        startActivity(intent);
        finish();
    }

    private void setupNavigationView(NavigationView navigationView ) {
        navigationView.setNavigationItemSelectedListener(item -> {
            selectedItem(item);
            return true;
        });
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            if (doubleBackToExitPressedOnce) {
                finish();
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Tekan back sekali lagi untuk keluar", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
        }
    }
}
