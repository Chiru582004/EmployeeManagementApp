package com.example.employeemanagement.Activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.employeemanagement.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

public class LocationServiceActivity extends AppCompatActivity {



    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private boolean isTracking = false;
    private TextView latitudeText;
    private TextView longitudeText;
    private TextView accuracyText;
    private TextView speedText;
    private TextView bearingText;
    private TextView distanceText;
    private TextView updatesText;
    private TextView averageSpeedText;
    private Button startTrackingButton;
    private Button stopTrackingButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_location_service);

        latitudeText = findViewById(R.id.tv_latitude);
        longitudeText = findViewById(R.id.tv_longitude);
        accuracyText = findViewById(R.id.tv_accuracy);
        speedText = findViewById(R.id.tv_speed);
        bearingText = findViewById(R.id.tv_bearing);

        distanceText = findViewById(R.id.tv_distance);
        updatesText = findViewById(R.id.tv_updates);
        averageSpeedText = findViewById(R.id.tv_averageSpeed);

        startTrackingButton = findViewById(R.id.btn_startTracking);
        stopTrackingButton = findViewById(R.id.btn_stopTracking);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        createLocationRequest();
        createLocationCallback();

        startTrackingButton.setOnClickListener(v -> {

            startTracking();
        });

        stopTrackingButton.setOnClickListener(v -> {
            stopTracking();
        });
    }



    private void createLocationRequest(){

        locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY,2000)
                .setMinUpdateIntervalMillis(1000)
                .build();

    }

    private void createLocationCallback() {

        locationCallback = new LocationCallback() {

            @Override
            public void onLocationResult(
                    @NonNull LocationResult locationResult) {

                for (Location location : locationResult.getLocations()) {

                    updateLocationUI(location);

                }
            }
        };
    }

    private void updateLocationUI(Location location) {

        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        float accuracy = location.getAccuracy();

        float speed = location.getSpeed();

        float bearing = location.getBearing();

        float speedKmh = speed * 3.6f;

        latitudeText.setText(
                "Latitude: " + latitude
        );

        longitudeText.setText(
                "Longitude: " + longitude
        );

        accuracyText.setText(
                "Accuracy: " + accuracy + " m"
        );

        speedText.setText(
                "Speed: " + speedKmh + " km/h"
        );

        bearingText.setText(
                "Bearing: " + bearing + "°"
        );

        Log.d(
                "LocationTracking",
                "Latitude: " + latitude +
                        ", Longitude: " + longitude +
                        ", Accuracy: " + accuracy +
                        ", Speed: " + speedKmh +
                        " km/h, Bearing: " + bearing
        );
    }


    private void startTracking() {

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
                &&
                ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED) {

            Toast.makeText(
                    this,
                    "Location permission is required",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
        );

        isTracking = true;
        int light_blue = ContextCompat.getColor(this, R.color.jeebly_light_blue);

        int red = ContextCompat.getColor(this, R.color.jeebly_red);

        startTrackingButton.setEnabled(false);
        startTrackingButton.setBackgroundColor(light_blue);
        stopTrackingButton.setEnabled(true);
        stopTrackingButton.setBackgroundColor(red);

        Toast.makeText(
                this,
                "Tracking started",
                Toast.LENGTH_SHORT
        ).show();
    }

    private void stopTracking() {

        fusedLocationClient.removeLocationUpdates(
                locationCallback
        );

        isTracking = false;


        int light_blue = ContextCompat.getColor(this, R.color.jeebly_light_blue);

        int red = ContextCompat.getColor(this, R.color.jeebly_red);

        startTrackingButton.setEnabled(true);
        startTrackingButton.setBackgroundColor(red);
        stopTrackingButton.setEnabled(false);
        stopTrackingButton.setBackgroundColor(light_blue);

        Toast.makeText(
                this,
                "Tracking stopped",
                Toast.LENGTH_SHORT
        ).show();
    }
}