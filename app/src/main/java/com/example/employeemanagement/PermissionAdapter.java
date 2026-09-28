package com.example.employeemanagement;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PermissionAdapter
        extends RecyclerView.Adapter<PermissionAdapter.PermissionViewHolder> {

    private final List<String> permissionList;

    private final Map<String, String> permissionMap = new HashMap<>();

    private PermissionManagementActivity activity;

    public PermissionAdapter(List<String> permissionList, PermissionManagementActivity instance) {

        this.permissionList = permissionList;

        this.activity = instance;

        // Permission mapping
        permissionMap.put(
                "Camera",
                Manifest.permission.CAMERA
        );

        permissionMap.put(
                "Location",
                Manifest.permission.ACCESS_FINE_LOCATION
        );

        permissionMap.put(
                "Notification",
                Manifest.permission.POST_NOTIFICATIONS
        );

        permissionMap.put(
                "Microphone",
                Manifest.permission.RECORD_AUDIO
        );

        permissionMap.put(
                "Contacts",
                Manifest.permission.READ_CONTACTS
        );
    }

    @NonNull
    @Override
    public PermissionViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(
                        R.layout.item_permission,
                        parent,
                        false
                );

        return new PermissionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull PermissionViewHolder holder,
            int position
    ) {

        // Get the permission name
        String permissionName = permissionList.get(position);

        // Display permission name
        holder.tvPermissionsName.setText(permissionName);

        // Get Android permission from the map
        String androidPermission =
                permissionMap.get(permissionName);

        // Check current permission status
        updatePermissionUI(
                holder,
                androidPermission
        );

        // Request Access button
        holder.btnRequestAccess.setOnClickListener(v -> {

            if (androidPermission != null) {

                activity.requestPermission(
                        androidPermission,
                        position
                );
            }
        });

        // Denied button
        holder.btnDenied.setOnClickListener(v -> {

            if (androidPermission != null) {

                activity.requestPermission(
                        androidPermission,
                        position
                );
            }
        });

        // Granted button

        holder.btnGranted.setOnClickListener(v -> {

            Toast.makeText(holder.itemView.getContext(), "Permission Already Granted", Toast.LENGTH_SHORT).show();

        });
    }

    /**
     * Updates the buttons according to
     * the current permission state.
     */
    private void updatePermissionUI(
            PermissionViewHolder holder,
            String androidPermission
    ) {

        if (androidPermission == null) {

            holder.btnDenied.setVisibility(View.VISIBLE);

            holder.btnGranted.setVisibility(View.GONE);

            holder.btnRequestAccess.setVisibility(View.VISIBLE);

            return;
        }

        Context context =
                holder.itemView.getContext();

        boolean isGranted =
                ContextCompat.checkSelfPermission(
                        context,
                        androidPermission
                ) == PackageManager.PERMISSION_GRANTED;

        if (isGranted) {

            // Permission is granted

            holder.btnDenied.setVisibility(
                    View.GONE
            );

            holder.btnGranted.setVisibility(
                    View.VISIBLE
            );

            holder.btnRequestAccess.setVisibility(
                    View.GONE
            );

        } else {

            // Permission is denied

            holder.btnDenied.setVisibility(
                    View.VISIBLE
            );

            holder.btnGranted.setVisibility(
                    View.GONE
            );

            holder.btnRequestAccess.setVisibility(
                    View.VISIBLE
            );
        }
    }

    @Override
    public int getItemCount() {

        return permissionList.size();
    }

    public static class PermissionViewHolder
            extends RecyclerView.ViewHolder {

        TextView tvPermissionsName;

        Button btnDenied;
        Button btnGranted;
        Button btnRequestAccess;

        public PermissionViewHolder(
                @NonNull View itemView
        ) {

            super(itemView);

            tvPermissionsName =
                    itemView.findViewById(
                            R.id.tv_permissionsName
                    );

            btnDenied =
                    itemView.findViewById(
                            R.id.btn_denied
                    );

            btnGranted =
                    itemView.findViewById(
                            R.id.btn_granted
                    );

            btnRequestAccess =
                    itemView.findViewById(
                            R.id.btn_requestAccess
                    );
        }
    }
}