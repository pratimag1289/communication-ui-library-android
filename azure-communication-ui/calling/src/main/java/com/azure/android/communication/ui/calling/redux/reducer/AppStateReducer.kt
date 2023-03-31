// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.redux.reducer

import com.azure.android.communication.ui.calling.redux.action.Action
import com.azure.android.communication.ui.calling.redux.state.AppReduxState

internal class AppStateReducer(
    private val callStateReducer: CallStateReducer,
    private val participantStateReducer: ParticipantStateReducer,
    private val deviceStateReducer: LocalParticipantStateReducer,
    private val permissionStateReducer: PermissionStateReducer,
    private val lifecycleReducer: LifecycleReducer,
    private val errorReducer: ErrorReducer,
    private val navigationReducer: NavigationReducer,
    private val audioSessionReducer: AudioSessionReducer,
    private val cameraOn: Boolean = false,
    private val microphoneOn: Boolean = false,
) :
    Reducer<AppReduxState> {
    override fun reduce(state: AppReduxState, action: Action): AppReduxState {

        val appState = AppReduxState(
            state.localParticipantState.displayName,
            cameraOn,
            microphoneOn
        )

        appState.callState = callStateReducer.reduce(
            state.callState,
            action
        )

        appState.remoteParticipantState = participantStateReducer.reduce(
            state.remoteParticipantState,
            action
        )

        appState.localParticipantState = deviceStateReducer.reduce(
            state.localParticipantState,
            action
        )

        appState.permissionState = permissionStateReducer.reduce(state.permissionState, action)
        appState.lifecycleState = lifecycleReducer.reduce(state.lifecycleState, action)
        appState.errorState = errorReducer.reduce(state.errorState, action)
        appState.navigationState = navigationReducer.reduce(state.navigationState, action)
        appState.audioSessionState = audioSessionReducer.reduce(state.audioSessionState, action)
        return appState
    }
}
