// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.chat.models;

import com.azure.android.communication.common.CommunicationTokenCredential;

public class ChatCompositeRemoteOptions {

    private final CommunicationTokenCredential credential;
    private final ChatCompositeJoinLocator locator;
    private final String displayName;

    /**
     * Create {@link ChatCompositeRemoteOptions}.
     *
     * @param locator    {@link ChatCompositeJoinLocator}
     * @param credential {@link CommunicationTokenCredential}.
     */
    public ChatCompositeRemoteOptions(
            final ChatCompositeJoinLocator locator,
            final CommunicationTokenCredential credential) {
        this(locator, credential, "");
    }

    /**
     * Create {@link ChatCompositeRemoteOptions}.
     *
     * @param locator     {@link ChatCompositeJoinLocator}
     * @param credential  {@link CommunicationTokenCredential}
     * @param displayName User display name other participants will see.
     */
    public ChatCompositeRemoteOptions(
            final ChatCompositeJoinLocator locator,
            final CommunicationTokenCredential credential,
            final String displayName) {

        this.credential = credential;
        this.displayName = displayName;
        this.locator = locator;
    }

    /**
     * Get {@link CommunicationTokenCredential}.
     *
     * @return {@link String}.
     */
    public CommunicationTokenCredential getCredential() {
        return credential;
    }

    /**
     * Get chat locator.
     *
     * @return {@link ChatCompositeJoinLocator}.
     */
    public ChatCompositeJoinLocator getLocator() {
        return locator;
    }

    /**
     * Get {@link CommunicationTokenCredential}.
     *
     * @return {@link String}.
     */
    public String getDisplayName() {
        return displayName;
    }
}