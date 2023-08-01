package com.azure.android.communication.ui.callingcompositedemoapp

import android.R
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Intent
import android.media.RingtoneManager
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
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
        Log.d(TAG, "Incoming push notification")

        val messageData: Map<String, String> = remoteMessage.data

        if (messageData.isNotEmpty()) {
            try {
                val notification = PushNotificationInfo.fromMap(messageData)
                showNotificationForIncomingCall(notification)
            } catch (e: Exception) {

            }
        }
        super.onMessageReceived(remoteMessage)

    }

    private fun showNotificationForIncomingCall(notification: PushNotificationInfo) {
        Log.i(TAG, "Showing notification for incoming call")
   /*     val resultIntent = Intent(this, CallLauncherActivity::class.java)
        val stackBuilder: TaskStackBuilder = TaskStackBuilder.create(this)
        stackBuilder.addNextIntentWithParentStack(resultIntent)
        val resultPendingIntent: PendingIntent =
            stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)*/
      /*  val answerCallIntent = Intent(applicationContext, CallLauncherActivity::class.java)
        answerCallIntent.putExtra("action", "answer")
        val answerCallPendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            1200,
            answerCallIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )*/
      /*  val declineCallIntent = Intent(applicationContext, CallLauncherActivity::class.java)
        declineCallIntent.putExtra("action", "decline")*/
    /*    val declineCallPendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            1201,
            declineCallIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )*/
        val content = java.lang.String.format(
            "%s: \n%s\n %s",
            last10(Utilities.toMRI(notification.from)),
            last10(notification.fromDisplayName),
            last10(Utilities.toMRI(notification.to))
        )
        val builder: NotificationCompat.Builder = NotificationCompat.Builder(this, "acs")
           // .setContentIntent(resultPendingIntent)
            .setSmallIcon(R.drawable.ic_menu_call)
            //.setCategory(NotificationCompat.CATEGORY_CALL)
            .setContentTitle("sss")
            .setContentText(content)
          //  .addAction(R.drawable.ic_menu_call, "Accept", answerCallPendingIntent)
          //  .addAction(R.drawable.ic_menu_call, "Decline", declineCallPendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
           // .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE))
           // .setOngoing(true)
            .setAutoCancel(true)
        val notificationManager = NotificationManagerCompat.from(this)
        notificationManager.notify(1, builder.build())
    }

    private fun last10(`in`: String?): String? {
        return if (`in` != null) {
            if (`in`.length >= 10) `in`.substring(`in`.length - 10) else `in`
        } else ""
    }
}