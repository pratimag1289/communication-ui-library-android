// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

/**
 * Provides participant list view options to Call Composite.
 *
 * Create an instance of {@link CallCompositeParticipantListOptions} and pass it to
 * {@link CallCompositeCallScreenOptions}.
 *
 */
public class CallCompositeParticipantListOptions {
    private boolean isLobbyParticipantsVisible = true;

    /**
     * Constructs {@link  CallCompositeParticipantListOptions}.
     */
    public CallCompositeParticipantListOptions() {
    }

    /**
     * get the visibility of lobby participants.
     */
    public boolean isLobbyParticipantsVisible() {
        return isLobbyParticipantsVisible;
    }

    /**
     * Set the visibility of lobby participants.
     * @param isLobbyParticipantsVisible visibility of lobby participants.
     * @return The current {@link CallCompositeParticipantListOptions}.
     */
    public CallCompositeParticipantListOptions setLobbyParticipantsVisible(final boolean isLobbyParticipantsVisible) {
        this.isLobbyParticipantsVisible = isLobbyParticipantsVisible;
        return this;
    }
}
