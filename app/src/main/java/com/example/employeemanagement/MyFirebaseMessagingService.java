package com.example.employeemanagement;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "FCM_SERVICE";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage){
        Log.d(TAG, "Message Recieved");

        if(remoteMessage.getNotification() != null){
            Log.d(TAG,"Message title: " + remoteMessage.getNotification().getTitle() );
            Log.d(TAG,"Message Body: " + remoteMessage.getNotification().getBody());
        }

        if(!remoteMessage.getData().isEmpty()){
            Log.d(TAG, "Data: " + remoteMessage.getData());
        }


    }
}

