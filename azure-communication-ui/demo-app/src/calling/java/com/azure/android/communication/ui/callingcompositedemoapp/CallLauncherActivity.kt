// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.callingcompositedemoapp

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.telecom.DisconnectCause
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.azure.android.communication.common.CommunicationTokenCredential
import com.azure.android.communication.common.CommunicationTokenRefreshOptions
import com.azure.android.communication.ui.calling.CallComposite
import com.azure.android.communication.ui.calling.CallCompositeBuilder
import com.azure.android.communication.ui.calling.models.CallCompositeCallStateCode
import com.azure.android.communication.ui.calling.models.CallCompositeIncomingCallNotificationOptions
import com.azure.android.communication.ui.calling.models.CallCompositeParticipantRole
import com.azure.android.communication.ui.callingcompositedemoapp.databinding.ActivityCallLauncherBinding
import com.azure.android.communication.ui.callingcompositedemoapp.features.AdditionalFeatures
import com.azure.android.communication.ui.callingcompositedemoapp.features.FeatureFlags
import com.azure.android.communication.ui.callingcompositedemoapp.features.SettingsFeatures
import com.azure.android.communication.ui.callingcompositedemoapp.features.conditionallyRegisterDiagnostics
import com.azure.android.communication.ui.callingcompositedemoapp.telecom_utils.CallConnectionService
import com.azure.android.communication.ui.callingcompositedemoapp.telecom_utils.CallHandler
import com.azure.android.communication.ui.callingcompositedemoapp.views.EndCompositeButtonView
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.microsoft.appcenter.AppCenter
import com.microsoft.appcenter.analytics.Analytics
import com.microsoft.appcenter.crashes.Crashes
import com.microsoft.appcenter.distribute.Distribute
import kotlinx.coroutines.launch
import org.threeten.bp.format.DateTimeFormatter
import java.util.UUID

class CallLauncherActivity : AppCompatActivity() {
    
    companion object {
        var callLauncherActivity: CallLauncherActivity? = null
        var deviceToken: String? = null
    }

    private lateinit var binding: ActivityCallLauncherBinding
    private val callLauncherViewModel: CallLauncherViewModel by viewModels()

    fun answerCall() {
        launch()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        createNotificationChannels()

        if (shouldFinish()) {
            finish()
            return
        }
        if (!AppCenter.isConfigured() && !BuildConfig.DEBUG) {
            AppCenter.start(
                application,
                BuildConfig.APP_SECRET,
                Analytics::class.java,
                Crashes::class.java,
                Distribute::class.java
            )
        }
        // Register Memory Viewer with FeatureFlags
        conditionallyRegisterDiagnostics(this)
        FeatureFlags.registerAdditionalFeature(AdditionalFeatures.secondaryThemeFeature)

        binding = ActivityCallLauncherBinding.inflate(layoutInflater)
        setContentView(binding.root)
        callLauncherActivity = this

        val data: Uri? = intent?.data
        val deeplinkAcsToken = data?.getQueryParameter("acstoken")
        val deeplinkName = data?.getQueryParameter("name")
        val deeplinkGroupId = data?.getQueryParameter("groupid")
        val deeplinkTeamsUrl = data?.getQueryParameter("teamsurl")
        val deepLinkRoomsId = data?.getQueryParameter("roomsid")
        var acsToken = ""

        binding.run {
            if (!deeplinkAcsToken.isNullOrEmpty()) {
                acsTokenText.setText(deeplinkAcsToken)
            } else {
                acsTokenText.setText(BuildConfig.ACS_TOKEN)
            }

            acsToken = acsTokenText.text.toString()

            if (!deeplinkName.isNullOrEmpty()) {
                userNameText.setText(deeplinkName)
            } else {
                userNameText.setText(BuildConfig.USER_NAME)
            }

            if (!deeplinkGroupId.isNullOrEmpty()) {
                groupIdOrTeamsMeetingLinkText.setText(deeplinkGroupId)
                groupCallRadioButton.isChecked = true
                teamsMeetingRadioButton.isChecked = false
                roomsMeetingRadioButton.isChecked = false
            } else if (!deeplinkTeamsUrl.isNullOrEmpty()) {
                groupIdOrTeamsMeetingLinkText.setText(deeplinkTeamsUrl)
                groupCallRadioButton.isChecked = false
                teamsMeetingRadioButton.isChecked = true
                roomsMeetingRadioButton.isChecked = false
            } else if (!deepLinkRoomsId.isNullOrEmpty()) {
                groupIdOrTeamsMeetingLinkText.setText(deepLinkRoomsId)
                groupCallRadioButton.isChecked = false
                teamsMeetingRadioButton.isChecked = false
                roomsMeetingRadioButton.isChecked = true
            } else {
                groupIdOrTeamsMeetingLinkText.setText(BuildConfig.GROUP_CALL_ID)
            }

            launchButton.setOnClickListener {
                launch()
                initCallHandler()
            }

            showUIButton.setOnClickListener {
                showUI()
            }
            closeCompositeButton.setOnClickListener { callLauncherViewModel.close() }

            groupCallRadioButton.setOnClickListener {
                if (groupCallRadioButton.isChecked) {
                    groupIdOrTeamsMeetingLinkText.setText(BuildConfig.GROUP_CALL_ID)
                    teamsMeetingRadioButton.isChecked = false
                    roomsMeetingRadioButton.isChecked = false
                    participantDialRadioButton.isChecked = false
                    attendeeRoleRadioButton.visibility = View.GONE
                    presenterRoleRadioButton.visibility = View.GONE
                }
            }
            teamsMeetingRadioButton.setOnClickListener {
                if (teamsMeetingRadioButton.isChecked) {
                    groupIdOrTeamsMeetingLinkText.setText(BuildConfig.TEAMS_MEETING_LINK)
                    groupCallRadioButton.isChecked = false
                    roomsMeetingRadioButton.isChecked = false
                    participantDialRadioButton.isChecked = false
                    attendeeRoleRadioButton.visibility = View.GONE
                    presenterRoleRadioButton.visibility = View.GONE
                }
            }
            participantDialRadioButton.setOnClickListener {
                if (participantDialRadioButton.isChecked) {
                    groupIdOrTeamsMeetingLinkText.setText(BuildConfig.PARTICIPANT_MRI)
                    groupCallRadioButton.isChecked = false
                    roomsMeetingRadioButton.isChecked = false
                    teamsMeetingRadioButton.isChecked = false
                    attendeeRoleRadioButton.visibility = View.GONE
                    presenterRoleRadioButton.visibility = View.GONE
                }
            }
            roomsMeetingRadioButton.setOnClickListener {
                if (roomsMeetingRadioButton.isChecked) {
                    groupIdOrTeamsMeetingLinkText.setText(BuildConfig.ROOMS_ID)
                    presenterRoleRadioButton.visibility = View.VISIBLE
                    attendeeRoleRadioButton.visibility = View.VISIBLE
                    attendeeRoleRadioButton.isChecked = true
                    groupCallRadioButton.isChecked = false
                    teamsMeetingRadioButton.isChecked = false
                    participantDialRadioButton.isChecked = false
                } else {
                    presenterRoleRadioButton.visibility = View.GONE
                    attendeeRoleRadioButton.visibility = View.GONE
                }
            }

            presenterRoleRadioButton.setOnClickListener {
                if (presenterRoleRadioButton.isChecked) {
                    attendeeRoleRadioButton.isChecked = false
                }
            }

            attendeeRoleRadioButton.setOnClickListener {
                if (attendeeRoleRadioButton.isChecked) {
                    presenterRoleRadioButton.isChecked = false
                }
            }

            showCallHistoryButton.setOnClickListener {
                showCallHistory()
            }

            lifecycleScope.launch {
                callLauncherViewModel.callCompositeCallStateStateFlow.collect {
                    runOnUiThread {
                        if (it.isNotEmpty()) {
                            callStateText.text = it
                            EndCompositeButtonView.get(application).updateText(it)
                            when(it) {
                                CallCompositeCallStateCode.CONNECTING.toString() -> {
                                    CallConnectionService.conn?.setDialing()
                                }
                                CallCompositeCallStateCode.CONNECTED.toString() -> {
                                    CallConnectionService.conn?.onAnswer()
                                }
                                CallCompositeCallStateCode.DISCONNECTED.toString() -> {
                                    CallConnectionService.conn?.setDisconnected(DisconnectCause(DisconnectCause.REMOTE, "REJECTED"))
                                }
                                CallCompositeCallStateCode.NONE.toString() -> {
                                    CallConnectionService.conn?.setDisconnected(DisconnectCause(DisconnectCause.REMOTE, "REJECTED"))
                                }
                            }
                        }
                    }
                }
            }

            lifecycleScope.launch {
                callLauncherViewModel.callCompositeExitSuccessStateFlow.collect {
                    runOnUiThread {
                        if (it &&
                            SettingsFeatures.getReLaunchOnExitByDefaultOption()
                        ) {
                            launch()
                        }
                    }
                }
            }

            if (BuildConfig.DEBUG) {
                versionText.text = "${BuildConfig.VERSION_NAME}"
            } else {
                versionText.text = "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})"
            }
        }

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("FirebaseTest ", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result

            // Log and toast
            Log.d("FirebaseToken ", token)

            deviceToken = token

            val callComposite = CallCompositeBuilder().build()
            val communicationTokenRefreshOptions =
                CommunicationTokenRefreshOptions({ acsToken }, true)
            val communicationTokenCredential =
                CommunicationTokenCredential(communicationTokenRefreshOptions)

            var options = CallCompositeIncomingCallNotificationOptions(communicationTokenCredential, token)
            callComposite.registerIncomingCallPushNotification(applicationContext, options)
        })

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CALL_PHONE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermission.launch(Manifest.permission.CALL_PHONE)
        }
    }

    private val requestPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            Toast.makeText(
                this,
                "Permission is ${if (isGranted) "granted" else "denied"}.",
                Toast.LENGTH_SHORT
            ).show()
        }

    override fun onDestroy() {
        super.onDestroy()
        EndCompositeButtonView.get(this).hide()
        EndCompositeButtonView.buttonView = null
        callLauncherViewModel?.unsubscribe()
    }

    // check whether new Activity instance was brought to top of stack,
    // so that finishing this will get us to the last viewed screen
    private fun shouldFinish() = BuildConfig.CHECK_TASK_ROOT && !isTaskRoot

    private fun showAlert(message: String, title: String = "Alert") {
        Log.d("Message N Inder", message)
        Log.d("Message N Inder", title)
        runOnUiThread {
            val builder = AlertDialog.Builder(this).apply {

                setMessage(message)
                setTitle(title)
                setPositiveButton("OK") { _, _ ->
                }
            }
            builder.show()
        }
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: CharSequence = "acs"
            val description = "acs"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel =
                NotificationChannel("acs", name, importance)
            channel.description = description
            val notificationManager = getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(channel)
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun initCallHandler() {
        val callDesc = "ACS Demo Call"
        val userName = "Pratima"
        val handler = CallHandler(application)
        handler.startOutgoingCall(callDesc, userName)
    }

    private fun launch() {
        val userName = binding.userNameText.text.toString()
        val acsToken = binding.acsTokenText.text.toString()

        val roomId = binding.groupIdOrTeamsMeetingLinkText.text.toString()
        val roomRole = if (binding.attendeeRoleRadioButton.isChecked) CallCompositeParticipantRole.ATTENDEE
        else if (binding.presenterRoleRadioButton.isChecked) CallCompositeParticipantRole.PRESENTER
        else null

        var groupId: UUID? = null
        if (binding.groupCallRadioButton.isChecked) {
            try {
                groupId =
                    UUID.fromString(binding.groupIdOrTeamsMeetingLinkText.text.toString().trim())
            } catch (e: IllegalArgumentException) {
                val message = "Group ID is invalid or empty."
                showAlert(message)
                return
            }
        }
        var meetingLink: String? = null
        if (binding.teamsMeetingRadioButton.isChecked) {
            meetingLink = binding.groupIdOrTeamsMeetingLinkText.text.toString()
            if (meetingLink.isBlank()) {
                val message = "Teams meeting link is invalid or empty."
                showAlert(message)
                return
            }
        }
        var participantMri: String? = null
        if (binding.participantDialRadioButton.isChecked) {
            participantMri = binding.groupIdOrTeamsMeetingLinkText.text.toString()
            if (participantMri.isBlank()) {
                val message = "Mri is invalid or empty."
                showAlert(message)
                return
            }
        }

        callLauncherViewModel.launch(
            this@CallLauncherActivity,
            acsToken,
            userName,
            groupId,
            roomId,
            roomRole,
            meetingLink,
            participantMri,
            CallLauncherViewModel.notificationData
        )
    }

    private fun showUI() {
        callLauncherViewModel.callComposite?.displayCallCompositeIfWasHidden(this)
    }

    private fun showCallHistory() {
        val history = callLauncherViewModel
            .getCallHistory(this@CallLauncherActivity)
            .sortedBy { it.callStartedOn }

        val title = "Total calls: ${history.count()}"
        var message = "Last Call: none"
        history.lastOrNull()?.let {
            message = "Last Call: ${it.callStartedOn.format(DateTimeFormatter.ofPattern("MMM dd 'at' hh:mm"))}"
            it.callIds.forEach { callId ->
                message += "\nCallId: $callId"
            }
        }

        showAlert(message, title)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.launcher_activity_action_bar, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {

        R.id.azure_composite_show_settings -> {
            val settingIntent = Intent(this, SettingsActivity::class.java)
            startActivity(settingIntent)
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onStart() {
        super.onStart()
        toggleEndCompositeButton()
    }

    override fun onStop() {
        super.onStop()
        toggleEndCompositeButton()
    }

    override fun onResume() {
        super.onResume()
        toggleEndCompositeButton()
    }

    private fun toggleEndCompositeButton() {
        if (!SettingsFeatures.getEndCallOnByDefaultOption()) {
            EndCompositeButtonView.get(this).hide()
        } else {
            EndCompositeButtonView.get(this).show(callLauncherViewModel)
        }
    }
}
