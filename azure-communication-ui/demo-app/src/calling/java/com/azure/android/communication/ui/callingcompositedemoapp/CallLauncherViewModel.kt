// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.callingcompositedemoapp

import android.content.Context
import androidx.lifecycle.ViewModel
import com.azure.android.communication.calling.PushNotificationInfo
import com.azure.android.communication.common.CommunicationTokenCredential
import com.azure.android.communication.common.CommunicationTokenRefreshOptions
import com.azure.android.communication.ui.calling.CallComposite
import com.azure.android.communication.ui.calling.CallCompositeBuilder
import com.azure.android.communication.ui.calling.models.CallCompositeCallHistoryRecord
import com.azure.android.communication.ui.calling.models.CallCompositeGroupCallLocator
import com.azure.android.communication.ui.calling.models.CallCompositeJoinLocator
import com.azure.android.communication.ui.calling.models.CallCompositeLocalOptions
import com.azure.android.communication.ui.calling.models.CallCompositeLocalizationOptions
import com.azure.android.communication.ui.calling.models.CallCompositeRemoteOptions
import com.azure.android.communication.ui.calling.models.CallCompositeSetupScreenViewData
import com.azure.android.communication.ui.calling.models.CallCompositeTeamsMeetingLinkLocator
import com.azure.android.communication.ui.callingcompositedemoapp.features.AdditionalFeatures
import com.azure.android.communication.ui.callingcompositedemoapp.features.SettingsFeatures
import java.util.UUID

class CallLauncherViewModel : ViewModel() {

    var callComposite: CallComposite? = null
    private var remoteOptions: CallCompositeRemoteOptions? = null

    private var communicationTokenCredential :CommunicationTokenCredential? = null

    fun initCallComposite(        context: Context,
                                  acsToken: String,
                                  displayName: String,
                                  groupId: UUID?,
                                  meetingLink: String?,) {

         callComposite = createCallComposite(context)

        val communicationTokenRefreshOptions =
            CommunicationTokenRefreshOptions({ acsToken }, true)
        communicationTokenCredential =
            CommunicationTokenCredential(communicationTokenRefreshOptions)

        val locator: CallCompositeJoinLocator =
            if (groupId != null) CallCompositeGroupCallLocator(groupId)
            else CallCompositeTeamsMeetingLinkLocator(meetingLink)

        remoteOptions =
            CallCompositeRemoteOptions(locator, communicationTokenCredential, "IPS" ,notificationData)

       // callComposite?.getCallClient(context, remoteOptions)
    }

    fun incomingCallAccept(context: Context) {
        callComposite!!.addOnErrorEventHandler(CallLauncherActivityErrorHandler(context, callComposite!!))

        if (SettingsFeatures.getRemoteParticipantPersonaInjectionSelection()) {
            callComposite?.addOnRemoteParticipantJoinedEventHandler(
                RemoteParticipantJoinedHandler(callComposite!!, context)
            )
        }

        val localOptions = CallCompositeLocalOptions()
            .setParticipantViewData(SettingsFeatures.getParticipantViewData(context.applicationContext))
            .setSetupScreenViewData(
                CallCompositeSetupScreenViewData()
                    .setTitle(SettingsFeatures.getTitle())
                    .setSubtitle(SettingsFeatures.getSubtitle())
            )
            .setSkipSetupScreen(SettingsFeatures.getSkipSetupScreenFeatureOption())
            .setCameraOn(SettingsFeatures.getCameraOnByDefaultOption())
            .setMicrophoneOn(SettingsFeatures.getMicOnByDefaultOption())

        val locator: CallCompositeJoinLocator = CallCompositeTeamsMeetingLinkLocator("")

        remoteOptions =
            CallCompositeRemoteOptions(locator, communicationTokenCredential, "IPS" ,notificationData)

        callComposite?.launch(context, remoteOptions, localOptions)
    }

    fun launch(
        context: Context
    ) {
        callComposite!!.addOnErrorEventHandler(CallLauncherActivityErrorHandler(context, callComposite!!))

        if (SettingsFeatures.getRemoteParticipantPersonaInjectionSelection()) {
            callComposite?.addOnRemoteParticipantJoinedEventHandler(
                RemoteParticipantJoinedHandler(callComposite!!, context)
            )
        }


        val localOptions = CallCompositeLocalOptions()
            .setParticipantViewData(SettingsFeatures.getParticipantViewData(context.applicationContext))
            .setSetupScreenViewData(
                CallCompositeSetupScreenViewData()
                    .setTitle(SettingsFeatures.getTitle())
                    .setSubtitle(SettingsFeatures.getSubtitle())
            )
            .setSkipSetupScreen(SettingsFeatures.getSkipSetupScreenFeatureOption())
            .setCameraOn(SettingsFeatures.getCameraOnByDefaultOption())
            .setMicrophoneOn(SettingsFeatures.getMicOnByDefaultOption())

        callComposite?.launch(context, remoteOptions, localOptions)
    }

    fun getCallHistory(context: Context): List<CallCompositeCallHistoryRecord> {
        return (callComposite ?: createCallComposite(context)).getDebugInfo(context).callHistoryRecords
    }

    private fun createCallComposite(context: Context): CallComposite {
        SettingsFeatures.initialize(context.applicationContext)

        val selectedLanguage = SettingsFeatures.language()
        val locale = selectedLanguage?.let { SettingsFeatures.locale(it) }

        val callCompositeBuilder = CallCompositeBuilder()
            .localization(CallCompositeLocalizationOptions(locale!!, SettingsFeatures.getLayoutDirection()))

        if (AdditionalFeatures.secondaryThemeFeature.active)
            callCompositeBuilder.theme(R.style.MyCompany_Theme_Calling)

        val callComposite = callCompositeBuilder.build()

        // For test purposes we will keep a static ref to CallComposite
        CallLauncherViewModel.callComposite = callComposite
        return callComposite
    }

    companion object {
        var callComposite: CallComposite? = null
        var notificationData: PushNotificationInfo? = null

    }
}
