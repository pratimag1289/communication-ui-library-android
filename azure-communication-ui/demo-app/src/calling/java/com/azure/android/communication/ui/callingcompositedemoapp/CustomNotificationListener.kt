package com.azure.android.communication.ui.callingcompositedemoapp

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi

//import com.google.firebase.messaging.RemoteMessage
//import com.microsoft.windowsazure.messaging.notificationhubs.NotificationListener

class CustomNotificationListener /*: NotificationListener*/ {
    /*@RequiresApi(Build.VERSION_CODES.N)
    override fun onPushNotificationReceived(context: Context?, message: RemoteMessage?) {
        val notification: RemoteMessage.Notification? = message!!.notification
        val title: String? = notification!!.title
        val data = message.data
        if (message != null) {
            Log.d("CustomNotification ", "Message Notification Title: $title")
            Log.d("CustomNotification ", "Message Notification Body: $message")

        }
        if (data != null) {
            data.forEach { (key, value) ->
                Log.d("CustomNotification ", "Message Notification key: $key value: $value")
            }
        }
    }*/
}