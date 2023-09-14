package com.azure.android.communication.ui.calling.presentation.fragment.calling.timer

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.azure.android.communication.ui.R
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class CallDurationView : LinearLayout {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    private lateinit var durationView: LinearLayout
    private lateinit var durationTextView: TextView
    private lateinit var callDurationViewModel: CallDurationViewModel

    override fun onFinishInflate() {
        super.onFinishInflate()

        durationView = findViewById(R.id.azure_communication_ui_call_duration_view)
        durationTextView =
            findViewById(R.id.azure_communication_ui_call_duration_view_text)
    }

    fun start(
        viewLifecycleOwner: LifecycleOwner,
        callDurationViewModel: CallDurationViewModel,
        accessibilityEnabled: Boolean
    ) {
        this.callDurationViewModel = callDurationViewModel
        this.callDurationViewModel.start()
        durationTextView.text = callDurationViewModel.getCallDurationTextFlow().value

        viewLifecycleOwner.lifecycleScope.launch {
            callDurationViewModel.getCallDurationTextFlow().collect {
                durationTextView.text = it
            }
            if (accessibilityEnabled) {
                durationTextView.contentDescription = "Call duration ${durationTextView.text}"
            }
        }
    }

    fun stop() {
        callDurationViewModel.stop()
    }
}
