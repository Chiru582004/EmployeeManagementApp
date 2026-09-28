package com.example.employeemanagement;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

public class NotificationHelper {

    private static final String CHANNEL_ID = "app_notification_channel";
    private static final String CHANNEL_NAME = "App Notifications";
    private static final String CHANNEL_DESCRIPTION =
            "Notifications from Employee Management App";


    public static void showNotification(
            Context context,
            int notificationId,
            String notificationTitle,
            String notificationBody
    ) {

        // Create notification channel
        createNotificationChannel(context);

        // Check notification permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED) {

                // Permission not granted
                return;
            }
        }

        // Build notification
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_notification)
                        .setContentTitle(notificationTitle)
                        .setContentText(notificationBody)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setAutoCancel(true);

        // Show notification
        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(context);

        notificationManager.notify(
                notificationId,
                builder.build()
        );
    }


    private static void createNotificationChannel(Context context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel channel =
                    new NotificationChannel(
                            CHANNEL_ID,
                            CHANNEL_NAME,
                            NotificationManager.IMPORTANCE_HIGH
                    );

            channel.setDescription(CHANNEL_DESCRIPTION);

            NotificationManager notificationManager =
                    context.getSystemService(
                            NotificationManager.class
                    );

            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }
}