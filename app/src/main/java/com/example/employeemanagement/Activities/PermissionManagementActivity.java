package com.example.employeemanagement.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.employeemanagement.PermissionAdapter;
import com.example.employeemanagement.R;

import java.util.ArrayList;
import java.util.List;

public class PermissionManagementActivity extends AppCompatActivity {



    private RecyclerView recyclerView;

    private PermissionAdapter permissionAdapter;

    private SharedPreferences sharedPreferences;

    private Button granted;

    private static final int PERMISSION_REQUEST_CODE = 100;

    private int currentPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_permission_management);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });



        recyclerView =
                findViewById(R.id.rv_permission);

        // SharedPreferences
        sharedPreferences =
                getSharedPreferences(
                        "PermissionPrefs",
                        MODE_PRIVATE
                );

        // Permission list
        List<String> permissionList =
                new ArrayList<>();

        permissionList.add("Camera");
        permissionList.add("Location");
        permissionList.add("Notification");
        permissionList.add("Microphone");
        permissionList.add("Contacts");

        // RecyclerView LayoutManager
        recyclerView.setLayoutManager(
                new LinearLayoutManager(this)
        );

        // Adapter
        permissionAdapter =
                new PermissionAdapter(
                        permissionList,
                        this
                );

        // Set Adapter
        recyclerView.setAdapter(
                permissionAdapter
        );
    }


    public void requestPermission(
            String androidPermission,
            int position
    ) {

        // Save the position of the clicked card
        currentPosition = position;

        // Check whether permission is already granted
        boolean isGranted =
                ContextCompat.checkSelfPermission(
                        this,
                        androidPermission
                ) == PackageManager.PERMISSION_GRANTED;

        if (isGranted) {

            // Permission already granted
            refreshPermissionUI();

            return;
        }

        // Check if this permission was requested before
        boolean alreadyRequested =
                sharedPreferences.getBoolean(
                        androidPermission,
                        false
                );

        if (!alreadyRequested) {

            /*
             * FIRST REQUEST
             *
             * Show Android permission dialog.
             */

            sharedPreferences
                    .edit()
                    .putBoolean(
                            androidPermission,
                            true
                    )
                    .apply();

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            androidPermission
                    },
                    PERMISSION_REQUEST_CODE
            );

        } else {

            /*
             * Permission was already requested.
             *
             * Open Android App Settings.
             */

            openAppSettings();
        }
    }

    /**
     * Called by Android after
     * the permission dialog.
     */
    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults
    ) {

        super.onRequestPermissionsResult(
                requestCode,
                permissions,
                grantResults
        );

        if (requestCode ==
                PERMISSION_REQUEST_CODE) {

            refreshPermissionUI();
        }
    }

    /**
     * Refresh only the card whose permission
     * was requested.
     */
    private void refreshPermissionUI() {

        if (permissionAdapter != null &&
                currentPosition != -1) {

            permissionAdapter.notifyItemChanged(
                    currentPosition
            );
        }
    }

    /**
     * Open the application's settings page.
     */
    private void openAppSettings() {

        Intent intent =
                new Intent(
                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                );

        Uri uri =
                Uri.fromParts(
                        "package",
                        getPackageName(),
                        null
                );

        intent.setData(uri);

        startActivity(intent);
    }

    /**
     * Called when the Activity becomes visible again.
     *
     * This is important when the user comes back
     * from Android Settings.
     */
    @Override
    protected void onResume() {

        super.onResume();

        if (permissionAdapter != null) {

            /*
             * Re-check all permissions.
             *
             * This also handles "Allow once"
             * permissions being revoked by Android.
             */
            permissionAdapter.notifyDataSetChanged();
        }
    }
}