package com.example.employeemanagement.Services;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.IBinder;
import android.os.Looper;

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

    private static final int NOTIFICATION_ID = 4001;

    private FusedLocationProviderClient fusedLocationClient;

    private LocationCallback locationCallback;

    private int updateCount = 0;

    @Override
    public void onCreate() {

        super.onCreate();

        fusedLocationClient =
                LocationServices.getFusedLocationProviderClient(this);

        locationCallback =
                new LocationCallback() {

                    @Override
                    public void onLocationResult(
                            LocationResult locationResult
                    ) {

                        for (Location location :
                                locationResult.getLocations()) {

                            updateCount++;

                            updateNotification(location);
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

        if (intent != null &&
                ACTION_STOP.equals(intent.getAction())) {

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

        startLocationUpdates(interval);

        return START_STICKY;
    }

    private void startLocationUpdates(long interval) {

        if (
                ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
                        &&
                        ActivityCompat.checkSelfPermission(
                                this,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED
        ) {

            stopSelf();

            return;
        }

        LocationRequest locationRequest =
                new LocationRequest.Builder(
                        Priority.PRIORITY_HIGH_ACCURACY,
                        interval
                )
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

    private void stopLocationUpdates() {

        if (fusedLocationClient != null &&
                locationCallback != null) {

            fusedLocationClient.removeLocationUpdates(
                    locationCallback
            );
        }
    }

    @Override
    public void onDestroy() {

        stopLocationUpdates();

        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }
}
