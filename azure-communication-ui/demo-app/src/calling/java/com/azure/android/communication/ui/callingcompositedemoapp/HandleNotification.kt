package com.azure.android.communication.ui.callingcompositedemoapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationManagerCompat

class HandleNotification: BroadcastReceiver()
{
    private val TAG = "FirebaseTest "

    override fun onReceive(context: Context, intent: Intent) {
        NotificationManagerCompat.from(context).cancelAll()
        Log.i(TAG, "HandleNotification.onReceive()")

        if (intent != null && intent.extras != null) {
            val action = intent.getStringExtra("action")
            Log.i(TAG, String.format("action:%s", action))
            assert(action != null)
            //waitForIncomingCall()
            if (action == "answer") {
                //answerCall(context)
            } else if (action == "decline") {
                //declineCall(context)
            }
            //context.sendBroadcast(Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS))
            //context.stopService(Intent(context, HandleNotification::class.java))
        }
    }

}