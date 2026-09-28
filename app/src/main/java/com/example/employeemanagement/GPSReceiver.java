package com.example.employeemanagement;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.util.Log;

public class GPSReceiver extends BroadcastReceiver {

    private static final String TAG = "GPSReceiver";
    private static final int GPS_NOTIFICATION_ID = 2001;

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d(TAG, "GPS Receiver triggered");

        if (!LocationManager.PROVIDERS_CHANGED_ACTION.equals(
                intent.getAction()
        )) {
            Log.d(TAG, "Unknown action: " + intent.getAction());
            return;
        }

        LocationManager locationManager =
                (LocationManager) context.getSystemService(
                        Context.LOCATION_SERVICE
                );

        if (locationManager == null) {
            Log.d(TAG, "LocationManager is null");
            return;
        }

        boolean isGpsEnabled =
                locationManager.isProviderEnabled(
                        LocationManager.GPS_PROVIDER
                );

        Log.d(TAG, "GPS Enabled: " + isGpsEnabled);

        if (isGpsEnabled) {

            NotificationHelper.showNotification(
                    context,
                    GPS_NOTIFICATION_ID,
                    "GPS Status",
                    "GPS is ON"
            );

        } else {

            NotificationHelper.showNotification(
                    context,
                    GPS_NOTIFICATION_ID,
                    "GPS Status",
                    "GPS is Turned Off"
            );
        }
    }
}