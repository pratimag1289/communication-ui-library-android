package com.azure.android.communication.ui.calling.presentation.fragment.calling.timer

import android.os.Handler
import android.os.Looper
import com.azure.android.communication.ui.calling.models.CallCompositeDurationTimerData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class CallDurationViewModel(private val durationTimerData: CallCompositeDurationTimerData?) {
    companion object {
        private const val INTERVAL_MILLIS: Long = 1000
    }
    private val handler: Handler = Handler(Looper.getMainLooper())
    private var callDuration: MutableStateFlow<String> = MutableStateFlow("00:00")
    private var startTimeMillis: Long = 0

    fun getCallDurationTextFlow(): StateFlow<String> = callDuration

    fun start() {
        this.startTimeMillis = durationTimerData?.startTimeMillis ?: 0
        handler.post(object : Runnable {
            override fun run() {
                val elapsedTimeMillis = startTimeMillis + INTERVAL_MILLIS
                val seconds = (elapsedTimeMillis / 1000).toInt() % 60
                val minutes = (elapsedTimeMillis / (1000 * 60) % 60).toInt()
                val hours = (elapsedTimeMillis / (1000 * 60 * 60) % 24).toInt()

                val timerText = if (hours > 0) {
                    String.format("%02d:%02d:%02d", hours, minutes, seconds);
                } else {
                    String.format("%02d:%02d", minutes, seconds);
                }

                // Update the TextView
                callDuration.value = timerText
                startTimeMillis = elapsedTimeMillis
                // Schedule the next update
                handler.postDelayed(this, INTERVAL_MILLIS)
            }
        })
    }

}