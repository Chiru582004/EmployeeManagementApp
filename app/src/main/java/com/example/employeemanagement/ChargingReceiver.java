package com.example.employeemanagement;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class ChargingReceiver extends BroadcastReceiver {

    private static final String TAG = "ChargingReceiver";
    private static final int CHARGER_NOTIFICATION_ID = 3001;

    @Override
    public void onReceive(Context context, Intent intent){

        String action = intent.getAction();

        if(Intent.ACTION_POWER_CONNECTED.equals(action)){
            Log.d(TAG, "Device is Charging");
//            Toast.makeText(context, "Device is Charging", Toast.LENGTH_SHORT).show();
            NotificationHelper.showNotification(context,CHARGER_NOTIFICATION_ID,"Device is Charging","Charger connected");
        } else if (Intent.ACTION_POWER_DISCONNECTED.equals(action)) {
            Log.d(TAG, "Charger is Disconnected");

//            Toast.makeText(context,"Charger Disconnected",Toast.LENGTH_SHORT).show();
            NotificationHelper.showNotification(context,CHARGER_NOTIFICATION_ID,"Device is not Charging","Charger disconnected");

        }
    }
}
