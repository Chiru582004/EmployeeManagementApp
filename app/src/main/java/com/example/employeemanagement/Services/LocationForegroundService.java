package com.example.employeemanagement.Services;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.example.employeemanagement.NotificationHelper;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

public class LocationForegroundService extends Service {

    public static final String ACTION_START = "ACTION_START_LOCATION_SERVICE";
    public static final String ACTION_STOP = "ACTION_STOP_LOCATION_SERVICE";
    public static final String EXTRA_INTERVAL = "EXTRA_INTERVAL";
    public static final String ACTION_LOCATION_UPDATE =
            "com.example.employeemanagement.ACTION_LOCATION_UPDATE";
    public static final String ACTION_CHECK_STATUS =
            "ACTION_CHECK_LOCATION_STATUS";
    public static boolean isTracking = false;
    public static final String EXTRA_LATITUDE = "latitude";
    public static final String EXTRA_LONGITUDE = "longitude";
    public static final String EXTRA_ACCURACY = "accuracy";
    private static final int NOTIFICATION_ID = 4001;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private int updateCount = 0;
    private Location previousLocation;
    private float totalDistance = 0f;
    private float totalSpeed = 0f;
    private float averageSpeed = 0f;
    private double latitude = 0.0;
    private double longitude = 0.0;
    private float accuracy = 0f;
    private float speedKmh = 0f;
    private float bearing = 0f;

    @Override
    public void onCreate() {

        super.onCreate();

        fusedLocationClient =
                LocationServices.getFusedLocationProviderClient(this);


        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }

                for (Location location : locationResult.getLocations()) {

                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    accuracy = location.getAccuracy();

                    speedKmh = location.hasSpeed()
                            ? location.getSpeed() * 3.6f
                            : 0f;

                    bearing = location.hasBearing()
                            ? location.getBearing()
                            : 0f;

                    // Calculate distance between consecutive locations
                    if (previousLocation != null) {
                        totalDistance += previousLocation.distanceTo(location);
                    }

                    previousLocation = new Location(location);

                    // Update statistics in the service
                    updateCount++;
                    totalSpeed += speedKmh;
                    averageSpeed = totalSpeed / updateCount;

                    Log.d("LocationService",
                            "Lat: " + latitude
                                    + ", Lng: " + longitude
                                    + ", Updates: " + updateCount
                                    + ", Distance: " + totalDistance
                                    + ", Average speed: " + averageSpeed);

                    updateNotification(location);
                    broadcastLocation(location);
                }
            }
        };

    }

    @Override
    public int onStartCommand(
            Intent intent,
            int flags,
            int startId
    ) {

        if (intent != null && ACTION_STOP.equals(intent.getAction())) {
            isTracking = false;

            stopLocationUpdates();
            stopForeground(true);
            stopSelf();

            return START_NOT_STICKY;
        }

        long interval = 5000;

        if (intent != null) {

            interval =
                    intent.getLongExtra(
                            EXTRA_INTERVAL,
                            5000
                    );
        }

        startForeground(
                NOTIFICATION_ID,
                NotificationHelper.buildForegroundNotification(
                        this,
                        "Location Tracking Active",
                        "Location Update Service started"
                )
        );

        boolean alreadyTracking = isTracking;

        if (!alreadyTracking) {
            resetTrackingStats();
        }

        startLocationUpdates(interval);

        return START_STICKY;
    }

    private void startLocationUpdates(long interval) {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            isTracking = false;
            stopSelf();
            return;
        }


        isTracking = true;

        LocationRequest locationRequest = new LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY, interval)
                .setMinUpdateIntervalMillis(interval)
                .build();

        fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
        );
    }

    private void updateNotification(Location location) {

        String notificationBody =
                "Updates: " + updateCount +
                        " | Lat: " + location.getLatitude() +
                        " | Lng: " + location.getLongitude();

        NotificationHelper.showNotification(
                this,
                NOTIFICATION_ID,
                "Location Tracking Active",
                notificationBody
        );
    }


    private void broadcastLocation(Location location) {

        Intent intent = new Intent(ACTION_LOCATION_UPDATE);
        intent.setPackage(getPackageName());

        // Current location details
        intent.putExtra(EXTRA_LATITUDE, latitude);
        intent.putExtra(EXTRA_LONGITUDE, longitude);
        intent.putExtra(EXTRA_ACCURACY, accuracy);
        intent.putExtra("speed", speedKmh);
        intent.putExtra("bearing", bearing);

        // Tracking statistics
        intent.putExtra("updateCount", updateCount);
        intent.putExtra("totalDistance", totalDistance);
        intent.putExtra("averageSpeed", averageSpeed);

        sendBroadcast(intent);
    }


    private void stopLocationUpdates() {

        if (fusedLocationClient != null &&
                locationCallback != null) {

            fusedLocationClient.removeLocationUpdates(
                    locationCallback
            );
        }
    }


    private void resetTrackingStats() {
        previousLocation = null;
        updateCount = 0;
        totalDistance = 0f;
        totalSpeed = 0f;
        averageSpeed = 0f;

        latitude = 0.0;
        longitude = 0.0;
        accuracy = 0f;
        speedKmh = 0f;
        bearing = 0f;
    }



    @Override
    public void onDestroy() {
        isTracking = false;
        stopLocationUpdates();
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }
}
