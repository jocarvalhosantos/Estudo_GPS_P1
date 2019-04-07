package com.jonas.estudo_gps_p1;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {

    private Button discoverButton;
    private Button grantGPSButton;
    private Button enableGPSButton;
    private Button disableGPSButton;
    private Button startRouteButton;
    private Button stopRouteButton;
    private EditText discoverEditText;
    private TextView travelledDistanceValueText;
    private Chronometer chronometer;

    private LocationManager locationManager;
    private LocationListener locationListener;

    private double latAtual, longAtual, latAntigo, longAntigo;
    private float distance=0;

    private static final int REQUEST_PERMISSION_GPS = 1001;

    Location locAntigo = new Location(LocationManager.GPS_PROVIDER);
    Location locAtual = new Location(LocationManager.GPS_PROVIDER);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        discoverButton = findViewById(R.id.searchButton);
        grantGPSButton = findViewById(R.id.grantgpsButton);
        enableGPSButton = findViewById(R.id.enablegpsButton);
        disableGPSButton = findViewById(R.id.disablegpsButton);
        startRouteButton = findViewById(R.id.startrouteButton);
        stopRouteButton = findViewById(R.id.stoprouteButton);
        discoverEditText = findViewById(R.id.searchPlainText);
        travelledDistanceValueText = findViewById(R.id.travelledDistanceValueTextView);
        chronometer = findViewById(R.id.chronometer);
        chronometer.setFormat("%s");


        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                latAtual = location.getLatitude();
                longAtual = location.getLongitude();
                locAtual.setLatitude(latAtual);
                locAtual.setLongitude(longAtual);
                if (locAtual==null){
                    showMessage(getResources().
                            getString(R.string.location_not_found));
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        chronometer.setOnChronometerTickListener(
                new Chronometer.OnChronometerTickListener() {
                    @Override
                    public void onChronometerTick(Chronometer chronometer) {
                        distance = distance + (locAtual.distanceTo(locAntigo)
                        /10000000);
                        DecimalFormat df = new DecimalFormat("0.0");
                        String show =
                                String.format(
                                        String.valueOf(
                                                df.format(distance)
                                        )
                                        + " " + getResources().getString(R.string.units)
                                );
                    }
                }
        );

        grantGPSButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(
                        MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED){
                    showMessage(getResources().getString(R.string.gps_already_granted));
                }
                else{
                    ActivityCompat.requestPermissions(
                            MainActivity.this,
                            new String[]{
                                    Manifest.permission.ACCESS_FINE_LOCATION
                            },
                            REQUEST_PERMISSION_GPS
                    );
                }
            }
        });

        enableGPSButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(
                        MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED){
                    try {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                2000,
                                10,
                                locationListener
                        );
                        showMessage(getResources().getString(R.string.gps_enabled));
                    }
                    catch (Exception ex){
                        showMessage(getResources().getString(R.string.error_enable_gps));
                    }
                }
                else {
                    showMessage(getResources().getString(R.string.grant_gps_message));
                }

            }
        });
    }

    private void showMessage (String msg){
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_toast,
                (ViewGroup) findViewById(R.id.custom_toast_container));
        TextView text = (TextView) layout.findViewById(R.id.text);
        text.setText(msg);
        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.BOTTOM, 10, 10);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }
}
