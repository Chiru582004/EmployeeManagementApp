package com.example.employeemanagement;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.LocationManager;
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
    private TextView fcmTokenLabel;

    private Button permissionManagerButton;

    private ChargingReceiver chargingReceiver;
    private Button clockIn;

    private Button clockOut;

    private GPSReceiver gpsReceiver;
    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_main);

        chargingReceiver = new ChargingReceiver();

        IntentFilter gpsFilter = new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION);
        gpsFilter.addAction(Intent.ACTION_PROVIDER_CHANGED);
        gpsReceiver = new GPSReceiver();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(gpsReceiver, gpsFilter, Context.RECEIVER_NOT_EXPORTED);
        } else {
            registerReceiver(gpsReceiver, gpsFilter);
        }

        fcmTokenTextView = findViewById(R.id.tv_fcm_token);
        fcmTokenLabel = findViewById(R.id.tv_fcm_label);

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {

                    if (!task.isSuccessful()) {

                        Log.e(
                                "FCM_TOKEN",
                                "Fetching FCM token failed",
                                task.getException()
                        );

                        fcmTokenLabel.setText(
                                "Failed to generate FCM token"
                        );

                        return;
                    }

                    String token = task.getResult();

                    Log.d("FCM_TOKEN", token);

                    fcmTokenLabel.setText("FCM Token Generated");
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
            NotificationHelper.showNotification(this, 1001, "Duty Clocked In", "Your duty has been clocked in successfully.");
        });

        clockOut.setOnClickListener(v -> {

            clockOut.setVisibility(View.GONE);
            clockIn.setVisibility(View.VISIBLE);

            NotificationHelper.showNotification(this, 1001, "Duty Clocked Out", "Your duty has been clocked out successfully.");
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

    @Override
    protected void onStart() {
        super.onStart();

        IntentFilter filter = new IntentFilter();

        filter.addAction(Intent.ACTION_POWER_CONNECTED);
        filter.addAction(Intent.ACTION_POWER_DISCONNECTED);

        registerReceiver(chargingReceiver,filter);
    }

    @Override
    protected void onStop() {
        super.onStop();

        unregisterReceiver(chargingReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (gpsReceiver != null) {
            unregisterReceiver(gpsReceiver);
        }
    }
}