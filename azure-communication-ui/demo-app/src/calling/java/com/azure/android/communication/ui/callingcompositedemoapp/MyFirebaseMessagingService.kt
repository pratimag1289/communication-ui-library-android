package com.azure.android.communication.ui.callingcompositedemoapp

import android.util.Log
import com.azure.android.communication.calling.PushNotificationInfo
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


class MyFirebaseMessagingService: FirebaseMessagingService()
{
    private val TAG = "FirebaseTest "


    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d(TAG, "Incoming push notification")

        val messageData: Map<String, String> = remoteMessage.data

        if (messageData.isNotEmpty()) {
            try {
                val notification = PushNotificationInfo.fromMap(messageData)

            } catch (e: Exception) {

            }
        }
    }
}