package dev.countryfair.player.playlazlo.com.countryfair;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import dev.countryfair.player.playlazlo.com.countryfair.helper.Constants;
import dev.countryfair.player.playlazlo.com.countryfair.helper.SetInitialVarsOnLocal;
import dev.countryfair.player.playlazlo.com.countryfair.service.ConfigService;

/**
 * Created by mymac on 3/28/17.
 */

public class PermissionActivity extends AppCompatActivity {

    private Button cameraStateImg;
    private Button locationStateImg;
    private Button storageStateImg;
    private boolean camStateFlg = false;
    private boolean locStateFlg = false;
    private boolean stgStateFlg = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.permission_layout);

        Button cameraPermissionBtn = (Button) findViewById(R.id.permission_camera_btn);
        cameraPermissionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setCameraPermission();
            }
        });

        Button locationPermissionBtn = (Button) findViewById(R.id.permission_location_btn);
        locationPermissionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setLocationPermission();
            }
        });

        Button storagePermissionBtn = (Button) findViewById(R.id.permission_notification_btn);
        storagePermissionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setStoragePermission();
            }
        });


        cameraStateImg = (Button) findViewById(R.id.permission_camera_state_img);
        locationStateImg = (Button) findViewById(R.id.permission_location_state_img);
        storageStateImg = (Button) findViewById(R.id.permission_notificatioin_state_img);

        updatePermissionStates();

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if (!sharedPref.getBoolean("startAppState", false)) {
            new SetInitialVarsOnLocal(PermissionActivity.this);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean("startAppState", true);
            editor.apply();
        }
        new Constants(PermissionActivity.this);
        getConfiguration();
        gotoMainPage();
    }

    private void updatePermissionStates(){

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sharedPref.edit();
        boolean cameraState = (ContextCompat.checkSelfPermission(PermissionActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)?true:false;
        boolean locationState = (ContextCompat.checkSelfPermission(PermissionActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)?true:false;
        boolean storageState = (ContextCompat.checkSelfPermission(PermissionActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)?true:false;
        editor.putBoolean("cameraState", cameraState);
        editor.putBoolean("locationState", locationState);
        editor.putBoolean("storageState", storageState);
        editor.commit();
    }


    private void setCameraPermission() {
        ActivityCompat.requestPermissions(PermissionActivity.this,
                new String[]{Manifest.permission.CAMERA},
                1);
    }

    private void setLocationPermission() {
        ActivityCompat.requestPermissions(PermissionActivity.this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                1);
    }

    private void setStoragePermission() {
        ActivityCompat.requestPermissions(PermissionActivity.this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                1);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        if (permissions.length > 0) {
            if (permissions[0].equals(Manifest.permission.CAMERA)) {
                switch (requestCode) {
                    case 1: {

                        // If request is cancelled, the result arrays are empty.
                        if (grantResults.length > 0
                                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putBoolean("cameraState", true);
                            editor.apply();
                            gotoMainPage();
                        } else {
                            gotoMainPage();
                            Toast.makeText(PermissionActivity.this, "Permission denied to use camera", Toast.LENGTH_SHORT).show();
                        }
                        return;
                    }

                }
            }
            if (permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION)) {
                switch (requestCode) {
                    case 1: {

                        // If request is cancelled, the result arrays are empty.
                        if (grantResults.length > 0
                                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putBoolean("locationState", true);
                            editor.apply();
                            gotoMainPage();
                        } else {
                            gotoMainPage();
                            Toast.makeText(PermissionActivity.this, "Permission denied to get location", Toast.LENGTH_SHORT).show();
                        }
                        return;
                    }

                }
            }
            if (permissions[0].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                switch (requestCode) {
                    case 1: {

                        // If request is cancelled, the result arrays are empty.
                        if (grantResults.length > 0
                                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putBoolean("storageState", true);
                            editor.apply();
                            gotoMainPage();
                        } else {
                            gotoMainPage();
                            Toast.makeText(PermissionActivity.this, "Permission denied to external", Toast.LENGTH_SHORT).show();
                        }
                        return;
                    }

                    // other 'case' lines to check for other
                    // permissions this app might request
                }
            }
        }

    }

    private void gotoMainPage() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        camStateFlg = sharedPref.getBoolean("cameraState", false);
        locStateFlg = sharedPref.getBoolean("locationState", false);
        stgStateFlg = sharedPref.getBoolean("storageState", false);

        if (camStateFlg) {
            cameraStateImg.setVisibility(View.VISIBLE);
        } else {
            cameraStateImg.setVisibility(View.GONE);
        }

        if (locStateFlg) {
            locationStateImg.setVisibility(View.VISIBLE);
        } else {
            locationStateImg.setVisibility(View.GONE);
        }

        if (stgStateFlg) {
            storageStateImg.setVisibility(View.VISIBLE);
        } else {
            storageStateImg.setVisibility(View.GONE);
        }

        if (camStateFlg && locStateFlg && stgStateFlg) {
            Intent i = new Intent(PermissionActivity.this, MainActivity.class);
            startActivity(i);
            finish();
        }
    }

    private void getConfiguration(){
        startService(new Intent(PermissionActivity.this, ConfigService.class));
    }
}
