package com.example.employeemanagement;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.util.Log;
import com.google.firebase.messaging.FirebaseMessaging;



public class MainActivity extends AppCompatActivity {


    private static final String CHANNEL_ID = "duty_notification_channel";

    private TextView fcmTokenTextView;

    private Button permissionManagerButton;
    private Button clockIn;

    private Button clockOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_main);

        createNotificationChannel();

        fcmTokenTextView = findViewById(R.id.tv_fcm_token);

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {

                    if (!task.isSuccessful()) {

                        Log.e(
                                "FCM_TOKEN",
                                "Fetching FCM token failed",
                                task.getException()
                        );

                        fcmTokenTextView.setText(
                                "Failed to generate FCM token"
                        );

                        return;
                    }

                    String token = task.getResult();

                    Log.d("FCM_TOKEN", token);

                    fcmTokenTextView.setText(token);
                });



        permissionManagerButton = findViewById(R.id.btn_permission_manager);


        permissionManagerButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, PermissionManagementActivity.class);

            startActivity(intent);

        });
        clockOut = findViewById(R.id.btn_clockOut);

        clockIn = findViewById(R.id.btn_clockIn);

        clockIn.setOnClickListener(v -> {

            clockIn.setVisibility(View.GONE);
            clockOut.setVisibility(View.VISIBLE);
            showClockInNotification();
        });

        clockOut.setOnClickListener(v -> {

            clockOut.setVisibility(View.GONE);
            clockIn.setVisibility(View.VISIBLE);

            showClockOutNotification();
        });


        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.main),
                (v, insets) -> {

                    Insets systemBars =
                            insets.getInsets(
                                    WindowInsetsCompat.Type.systemBars()
                            );

                    v.setPadding(
                            systemBars.left,
                            systemBars.top,
                            systemBars.right,
                            systemBars.bottom
                    );

                    return insets;
                }
        );

    }

    private void createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            CharSequence name = "Duty Notifications";

            String description =
                    "Notifications related to employee duty status";

            int importance =
                    NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel channel =
                    new NotificationChannel(
                            CHANNEL_ID,
                            name,
                            importance
                    );

            channel.setDescription(description);

            NotificationManager notificationManager =
                    getSystemService(NotificationManager.class);

            notificationManager.createNotificationChannel(channel);
        }
    }

    private void showClockInNotification() {

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(
                        this,
                        CHANNEL_ID
                )
                        .setSmallIcon(R.drawable.ic_notification)
                        .setContentTitle("Duty Clocked In")
                        .setContentText("Your duty has been clocked in successfully.")
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setAutoCancel(true);

        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(this);

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
        ) != PackageManager.PERMISSION_GRANTED) {

            return;
        }

        notificationManager.notify(
                1001,
                builder.build()
        );
    }
    private void showClockOutNotification() {

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(
                        this,
                        CHANNEL_ID
                )
                        .setSmallIcon(R.drawable.ic_notification)
                        .setContentTitle("Duty Clocked Out")
                        .setContentText("Your duty has been clocked out successfully.")
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setAutoCancel(true);

        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(this);

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
        ) != PackageManager.PERMISSION_GRANTED) {

            return;
        }

        notificationManager.notify(
                1001,
                builder.build()
        );
    }
}