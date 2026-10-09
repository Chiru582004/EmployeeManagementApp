package com.example.employeemanagement.Activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.util.Log;

import com.example.employeemanagement.NotificationHelper;
import com.example.employeemanagement.R;
import com.example.employeemanagement.receivers.ChargingReceiver;
import com.example.employeemanagement.receivers.GPSReceiver;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.messaging.FirebaseMessaging;



public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {


    private static final String CHANNEL_ID = "duty_notification_channel";
    
    private TextView fcmTokenLabel;

    private Button permissionManagerButton;

    private ChargingReceiver chargingReceiver;
    private Button clockIn;

    private Button clockOut;

    private ImageButton profileImageButton;
    private ImageButton locationImageButton;

    private TextView latLongLabel;
    private GPSReceiver gpsReceiver;
    private GoogleMap mMap;
    private Marker mapMarker;
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

        latLongLabel = findViewById(R.id.tv_onClick_latlong_label);
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



        locationImageButton = findViewById(R.id.ibtn_location);

        locationImageButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, LocationServiceActivity.class);

            startActivity(intent);
        });

        profileImageButton = findViewById(R.id.btn_profileImage);

        profileImageButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CameraActivity.class);

            startActivity(intent);
        });



        permissionManagerButton = findViewById(R.id.btn_permission_manager);


        permissionManagerButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, PermissionManagementActivity.class);

            startActivity(intent);

        });
        clockOut = findViewById(R.id.btn_clockOut);

        clockIn = findViewById(R.id.btn_clockIn);

        clockIn.setOnClickListener(v -> {

            clockIn.setEnabled(false);
            clockOut.setEnabled(true);
            NotificationHelper.showNotification(this, 1001, "Duty Clocked In", "Your duty has been clocked in successfully.");
        });

        clockOut.setOnClickListener(v -> {

            clockIn.setEnabled(true);
            clockOut.setEnabled(false);

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
        setUpMap();

    }

    private void setUpMap() {
        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this); // triggers onMapReady
        }
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

    private void moveCameraToLocation() {

        LatLng location = new LatLng(
                28.6139,
                77.2090
        );

        CameraUpdate cameraUpdate =
                CameraUpdateFactory.newLatLngZoom(
                        location,
                        4
                );

        mMap.animateCamera(cameraUpdate);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setBuildingsEnabled(true);

        LatLng initialLocation =
                new LatLng(28.52579, 77.15197);

        CameraUpdate cameraUpdate =
                CameraUpdateFactory.newLatLngZoom(
                        initialLocation,
                        4
                );

        mMap.moveCamera(cameraUpdate);

        moveCameraToLocation();

        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);


        mMap.setOnMapClickListener(latLng -> {

            double latitude = latLng.latitude;
            double longitude = latLng.longitude;

            latLongLabel.setVisibility(View.VISIBLE);
            latLongLabel.setText( latitude + " , " + longitude);

            Log.d(
                    "MapClick",
                    "Latitude: " + latitude +
                            ", Longitude: " + longitude
            );
            mMap.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(
                            latLng,
                            15
                    )
            );

//            "Service updated at 09:45:30 AM"
//            "Service received location update ()"

//            mMap.clear();
            LatLng tapLocation = new LatLng(latitude, longitude);
            if (mapMarker == null) {
                mapMarker = mMap.addMarker(
                        new MarkerOptions()
                                .position(tapLocation)
                                .draggable(true)
                                .title("Map Single Click Marker")
                                .snippet("Testing marker single click")
                                .zIndex(100.9f)
                                .flat(false)
                );
            } else {
                mapMarker.setPosition(tapLocation);
                mapMarker.setTitle("Map Single Click Marker");
                mapMarker.setSnippet("Testing marker single click");
            }
            googleMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                @Override
                public void onMarkerDrag(@NonNull Marker marker) {
                    String position = marker.getPosition().latitude + " , " + marker.getPosition().longitude;
                    latLongLabel.setText(position);
                }

                @Override
                public void onMarkerDragEnd(@NonNull Marker marker) {

                }

                @Override
                public void onMarkerDragStart(@NonNull Marker marker) {

                }
            });
        });

        mMap.setOnMapLongClickListener(latLng -> {
            double latitude = latLng.latitude;
            double longitude = latLng.longitude;

            latLongLabel.setVisibility(View.VISIBLE);
            latLongLabel.setText( latitude + " , " + longitude);

            Log.d(
                    "MapClick",
                    "Latitude: " + latitude +
                            ", Longitude: " + longitude
            );
            mMap.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(
                            latLng,
                            15
                    )
            );
//            mMap.clear();
            LatLng tapLocation = new LatLng(latitude, longitude);
            if (mapMarker == null) {
                mapMarker = mMap.addMarker(
                        new MarkerOptions()
                                .position(tapLocation)
                                .title("Map Long Click Marker")
                                .snippet("Testing marker long click")
                );
            } else {
                mapMarker.setPosition(tapLocation);
                mapMarker.setTitle("Map Long Click Marker");
                mapMarker.setSnippet("Testing marker long click");
            }
//            mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title("Map Long Click Marker").snippet("Testing marker long click"));
        });
    }
}