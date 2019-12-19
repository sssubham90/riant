package com.riantservices.riant.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.riantservices.riant.R;
import com.riantservices.riant.helpers.SessionManager;
import com.riantservices.riant.helpers.SingleShotLocationProvider;

public class SplashActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 1;
    SessionManager session;
    private Handler mHandler = new Handler();
    private Bundle bundle = new Bundle();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        session = new SessionManager(getApplicationContext());
        if(getSupportActionBar()!=null)
            getSupportActionBar().hide();
        final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );
        if ((Build.VERSION.SDK_INT < Build.VERSION_CODES.M ) || (ContextCompat.checkSelfPermission(SplashActivity.this,Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(SplashActivity.this,Manifest.permission.ACCESS_COARSE_LOCATION)==PackageManager.PERMISSION_GRANTED)){
            if (manager != null) {
                if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
                    buildAlertMessageNoGps();
                }
                else {
                    requestLocation();
                    mHandler.postDelayed(new Runnable() {
                         public void run() {
                             start();
                         }
                     }, 2000);
                }
            }
        }
        else
            ActivityCompat.requestPermissions(SplashActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
    }

    public void start(){
        if (session.isLoggedIn()) {
            Intent goMain = new Intent(this, MainActivity.class);
            startActivity(goMain.putExtras(bundle));
        }
        else {
            Intent goLogin = new Intent(this, LoginActivity.class);
            startActivity(goLogin);
        }
    }

    protected void requestLocation(){
        SingleShotLocationProvider.requestSingleUpdate(SplashActivity.this,
                new SingleShotLocationProvider.LocationCallback() {
                    @Override public void onNewLocationAvailable(SingleShotLocationProvider.GPSCoordinates location) {
                        Log.d("loc","My cordinates:"+ location.getLatitude()+","+ location.getLongitude());
                        bundle.putDouble("lat",location.getLatitude());
                        bundle.putDouble("lng",location.getLongitude());
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[]grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                requestLocation();
                mHandler.postDelayed(new Runnable() {
                    public void run() {
                        start();
                    }
                }, 2000);
            } else {
                SplashActivity.this.finish();
                System.exit(0);
            }
        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                        SplashActivity.this.finish();
                        System.exit(0);
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }
}
