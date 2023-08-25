// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

/**
 * Provides grid view options to Call Composite.
 *
 * Create an instance of {@link CallCompositeGridViewOptions} and pass it to
 * {@link CallCompositeCallScreenOptions}.
 *
 */
public class CallCompositeGridViewOptions {
    private boolean isLobbyParticipantsVisible = true;

    /**
     * Constructs {@link  CallCompositeGridViewOptions}.
     */
    public CallCompositeGridViewOptions() {
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
     * @return The current {@link CallCompositeGridViewOptions}.
     */
    public CallCompositeGridViewOptions setLobbyParticipantsVisible(final boolean isLobbyParticipantsVisible) {
        this.isLobbyParticipantsVisible = isLobbyParticipantsVisible;
        return this;
    }
}
