package com.example.employeemanagement.Activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
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
    private Spinner updateIntervalSpinner;
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
    private Location previousLocation;
    private float totalDistance = 0f;
    private int locationUpdateCount = 0;
    private float totalSpeed = 0f;
    private long selectedInterval = 5000;

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

        updateIntervalSpinner = findViewById(R.id.spinner_updateInterval);

        distanceText = findViewById(R.id.tv_distance);

        updatesText = findViewById(R.id.tv_updates);

        averageSpeedText = findViewById(R.id.tv_averageSpeed);

        startTrackingButton = findViewById(R.id.btn_startTracking);

        stopTrackingButton = findViewById(R.id.btn_stopTracking);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        String[] intervals = {
                "1 Second",
                "5 Seconds",
                "10 Seconds",
                "30 Seconds"
        };

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        intervals
                );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        updateIntervalSpinner.setAdapter(adapter);

        updateIntervalSpinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(
                            AdapterView<?> parent,
                            View view,
                            int position,
                            long id) {

                        switch (position) {

                            case 0:
                                selectedInterval = 1000;
                                break;

                            case 1:
                                selectedInterval = 5000;
                                break;

                            case 2:
                                selectedInterval = 10000;
                                break;

                            case 3:
                                selectedInterval = 30000;
                                break;
                        }
                        createLocationRequest();
                    }

                    @Override
                    public void onNothingSelected(
                            AdapterView<?> parent) {
                    }
                }
        );

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        createLocationRequest();
        createLocationCallback();

        startTrackingButton.setOnClickListener(v -> {

            startTracking();

            updateIntervalSpinner.setVisibility(View.GONE);

        });

        stopTrackingButton.setOnClickListener(v -> {
            stopTracking();
            updateIntervalSpinner.setVisibility(View.VISIBLE);
        });
    }



    private void createLocationRequest() {

        locationRequest =
                new LocationRequest.Builder(
                        Priority.PRIORITY_HIGH_ACCURACY,
                        selectedInterval
                )
                        .setMinUpdateIntervalMillis(
                                selectedInterval
                        )
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

        float speed = 0f;

        if (location.hasSpeed()) {
            speed = location.getSpeed();
        }

        float bearing = 0f;

        if (location.hasBearing()) {
            bearing = location.getBearing();
        }

        float speedKmh = speed * 3.6f;


        locationUpdateCount++;

        if (previousLocation != null) {

            float distance = previousLocation.distanceTo(location);

            totalDistance += distance;
        }

        previousLocation = new Location(location);

        totalSpeed += speedKmh;

        float averageSpeed = totalSpeed / locationUpdateCount;

        latitudeText.setText("Latitude: " + latitude);

        longitudeText.setText("Longitude: " + longitude);

        accuracyText.setText(
                "Accuracy: " + accuracy + " m"
        );

        speedText.setText(
                "Speed: " + speedKmh + " km/h"
        );

        bearingText.setText(
                "Bearing: " + bearing + "°"
        );

        distanceText.setText(
                "Distance: " + totalDistance + " m"
        );

        updatesText.setText(
                "Updates: " + locationUpdateCount
        );

        averageSpeedText.setText(
                "Average Speed: " + averageSpeed + " km/h"
        );

        Log.d(
                "LocationTracking",
                "Latitude: " + latitude +
                        ", Longitude: " + longitude +
                        ", Accuracy: " + accuracy +
                        ", Speed: " + speedKmh +
                        " km/h, Bearing: " + bearing +
                        ", Distance: " + totalDistance +
                        ", Updates: " + locationUpdateCount +
                        ", Average Speed: " + averageSpeed
        );
    }


    private void startTracking() {

        previousLocation = null;

        totalDistance = 0f;

        locationUpdateCount = 0;

        totalSpeed = 0f;

        distanceText.setText("Distance: 0 m");
        updatesText.setText("Updates: 0");
        averageSpeedText.setText("Average Speed: 0 km/h");

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