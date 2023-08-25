// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

import com.azure.android.communication.ui.calling.CallCompositeBuilder;

/**
 * Provides call screen view options to Call Composite.
 *
 * Create an instance of {@link CallCompositeCallScreenOptions} and pass it to
 * {@link CallCompositeBuilder}.
 *
 */
public class CallCompositeCallScreenOptions {
    private CallCompositeGridViewOptions gridViewOptions = null;
    private CallCompositeParticipantListOptions participantListOptions = null;

    /**
     * Constructs {@link  CallCompositeCallScreenOptions}.
     */
    public CallCompositeCallScreenOptions() {
    }

    /**
     * get the {@link CallCompositeGridViewOptions}.
     */
    public CallCompositeGridViewOptions getGridViewOptions() {
        return gridViewOptions;
    }

    /**
     * Set the {@link CallCompositeGridViewOptions}.
     * @param gridViewOptions {@link CallCompositeGridViewOptions}.
     * @return The current {@link CallCompositeCallScreenOptions}.
     */ 
    public CallCompositeCallScreenOptions setGridViewOptions(final CallCompositeGridViewOptions gridViewOptions) {
        this.gridViewOptions = gridViewOptions;
        return this;
    }

    /**
     * get the {@link CallCompositeParticipantListOptions}.
     */
    public CallCompositeParticipantListOptions getParticipantListOptions() {
        return participantListOptions;
    }

    /**
     * Set the {@link CallCompositeParticipantListOptions}.
     * @param participantListOptions {@link CallCompositeParticipantListOptions}.
     * @return The current {@link CallCompositeCallScreenOptions}.
     */
    public CallCompositeCallScreenOptions setParticipantListOptions(
            final CallCompositeParticipantListOptions participantListOptions) {
        this.participantListOptions = participantListOptions;
        return this;
    }
}
