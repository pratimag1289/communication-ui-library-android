// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling

import androidx.test.platform.app.InstrumentationRegistry
import com.azure.android.communication.BaseUiTest
import com.azure.android.communication.assertViewHasChild
import com.azure.android.communication.assertViewText
import com.azure.android.communication.calling.MediaStreamType
import com.azure.android.communication.calling.ParticipantState
import com.azure.android.communication.common.CommunicationTokenCredential
import com.azure.android.communication.common.CommunicationTokenRefreshOptions
import com.azure.android.communication.tapWhenDisplayed
import com.azure.android.communication.ui.calling.models.CallCompositeCallScreenOptions
import com.azure.android.communication.ui.calling.models.CallCompositeGridViewOptions
import com.azure.android.communication.ui.calling.models.CallCompositeGroupCallLocator
import com.azure.android.communication.ui.calling.models.CallCompositeParticipantListOptions
import com.azure.android.communication.ui.calling.models.CallCompositeRemoteOptions
import com.azure.android.communication.ui.calling.service.sdk.CommunicationIdentifier
import com.azure.android.communication.waitUntilDisplayed
import kotlinx.coroutines.test.runTest
import org.junit.Test
import java.util.UUID

internal class LobbyParticipantsVisibilityTest : BaseUiTest() {

    @Test
    fun testGridViewAndParticipantListShowsLobbyParticipantsWhenHideOptionsNotSetWithViewOptions() = runTest {
        injectDependencies(testScheduler)

        // one lobby participant and two connected participants
        val expectedParticipantCountOnGridView = 3
        val expectedParticipantCountOnParticipantList = 3
        val expectedParticipantCountOnFloatingHeader = 3
        val callScreenOptions = CallCompositeCallScreenOptions()

        lobbyParticipantsVisibilityTests(
            callScreenOptions,
            expectedParticipantCountOnFloatingHeader,
            expectedParticipantCountOnGridView,
            expectedParticipantCountOnParticipantList
        )
    }

    @Test
    fun testGridViewAndParticipantListShowsLobbyParticipantsWhenHideOptionsAreNull() = runTest {
        injectDependencies(testScheduler)

        // one lobby participant and two connected participants
        val expectedParticipantCountOnGridView = 3
        val expectedParticipantCountOnParticipantList = 3
        val expectedParticipantCountOnFloatingHeader = 3
        val callScreenOptions: CallCompositeCallScreenOptions? = null

        lobbyParticipantsVisibilityTests(
            callScreenOptions,
            expectedParticipantCountOnFloatingHeader,
            expectedParticipantCountOnGridView,
            expectedParticipantCountOnParticipantList
        )
    }

    @Test
    fun testGridViewAndParticipantListShowsLobbyParticipantsWhenCallCompositeCallScreenOptionsSetToTrue() = runTest {
        injectDependencies(testScheduler)

        // one lobby participant and two connected participants
        val expectedParticipantCountOnGridView = 3
        val expectedParticipantCountOnParticipantList = 3
        val expectedParticipantCountOnFloatingHeader = 3
        val callScreenOptions = CallCompositeCallScreenOptions().setParticipantListOptions(
            CallCompositeParticipantListOptions().setLobbyParticipantsVisible(true)
        ).setGridViewOptions(CallCompositeGridViewOptions().setLobbyParticipantsVisible(true))

        lobbyParticipantsVisibilityTests(
            callScreenOptions,
            expectedParticipantCountOnFloatingHeader,
            expectedParticipantCountOnGridView,
            expectedParticipantCountOnParticipantList
        )
    }

    @Test
    fun testGridViewAndParticipantListHidesLobbyParticipantsWhenCallCompositeCallScreenOptionsSetToFalse() = runTest {
        injectDependencies(testScheduler)

        // one lobby participant and two connected participants
        val expectedParticipantCountOnGridView = 2
        val expectedParticipantCountOnParticipantList = 2
        val expectedParticipantCountOnFloatingHeader = 2
        val callScreenOptions = CallCompositeCallScreenOptions().setParticipantListOptions(
            CallCompositeParticipantListOptions().setLobbyParticipantsVisible(false)
        ).setGridViewOptions(CallCompositeGridViewOptions().setLobbyParticipantsVisible(false))

        lobbyParticipantsVisibilityTests(
            callScreenOptions,
            expectedParticipantCountOnFloatingHeader,
            expectedParticipantCountOnGridView,
            expectedParticipantCountOnParticipantList
        )
    }

    @Test
    fun testGridViewHidesLobbyParticipantsWhenCallCompositeCallScreenOptionsSetToFalse() = runTest {
        injectDependencies(testScheduler)

        // one lobby participant and two connected participants
        val expectedParticipantCountOnGridView = 2
        val expectedParticipantCountOnParticipantList = 3
        val expectedParticipantCountOnFloatingHeader = 2
        val callScreenOptions = CallCompositeCallScreenOptions()
            .setGridViewOptions(CallCompositeGridViewOptions().setLobbyParticipantsVisible(false))

        lobbyParticipantsVisibilityTests(
            callScreenOptions,
            expectedParticipantCountOnFloatingHeader,
            expectedParticipantCountOnGridView,
            expectedParticipantCountOnParticipantList
        )
    }

    @Test
    fun testParticipantListHidesLobbyParticipantsWhenCallCompositeCallScreenOptionsSetToFalse() = runTest {
        injectDependencies(testScheduler)

        // one lobby participant and two connected participants
        val expectedParticipantCountOnGridView = 3
        val expectedParticipantCountOnParticipantList = 2
        val expectedParticipantCountOnFloatingHeader = 2
        val callScreenOptions = CallCompositeCallScreenOptions().setParticipantListOptions(
            CallCompositeParticipantListOptions().setLobbyParticipantsVisible(false)
        )

        lobbyParticipantsVisibilityTests(
            callScreenOptions,
            expectedParticipantCountOnFloatingHeader,
            expectedParticipantCountOnGridView,
            expectedParticipantCountOnParticipantList
        )
    }

    private suspend fun lobbyParticipantsVisibilityTests(
        callScreenOptions: CallCompositeCallScreenOptions?,
        expectedParticipantCountOnFloatingHeader: Int,
        expectedParticipantCountOnGridView: Int,
        expectedParticipantCountOnParticipantList: Int
    ) {
        // Launch the UI.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val callComposite = CallCompositeBuilder().callScreenOptions(callScreenOptions).build()
        val communicationTokenRefreshOptions =
            CommunicationTokenRefreshOptions({ "token" }, true)
        val communicationTokenCredential =
            CommunicationTokenCredential(communicationTokenRefreshOptions)
        val remoteOptions =
            CallCompositeRemoteOptions(
                CallCompositeGroupCallLocator(UUID.fromString("74fce2c1-520f-11ec-97de-71411a9a8e14")),
                communicationTokenCredential,
                "test"
            )

        callComposite.launchTest(appContext, remoteOptions, null)

        tapWhenDisplayed(joinCallId)
        waitUntilDisplayed(endCallId)

        callingSDK.addRemoteParticipant(
            CommunicationIdentifier.CommunicationUserIdentifier("ACS User 1"),
            displayName = "ACS User 1",
            isMuted = false,
            isSpeaking = true,
            videoStreams = listOf(MediaStreamType.VIDEO)
        )

        callingSDK.addRemoteParticipant(
            CommunicationIdentifier.CommunicationUserIdentifier("ACS User 2"),
            displayName = "ACS User 2",
            isMuted = false,
            isSpeaking = true,
            videoStreams = listOf(MediaStreamType.VIDEO)
        )

        callingSDK.addRemoteParticipant(
            CommunicationIdentifier.CommunicationUserIdentifier("Lobby State"),
            displayName = "Lobby State",
            state = ParticipantState.IN_LOBBY,
            isMuted = false,
            isSpeaking = true,
            videoStreams = listOf(MediaStreamType.VIDEO)
        )

        waitUntilDisplayed(participantContainerId)

        assertViewText(
            participantCountId,
            "Call with $expectedParticipantCountOnFloatingHeader people"
        )

        assertViewHasChild(participantContainerId, expectedParticipantCountOnGridView)

        tapWhenDisplayed(participantListOpenButton)

        // 1 local
        assertViewHasChild(bottomDrawer, expectedParticipantCountOnParticipantList + 1)
    }
}
