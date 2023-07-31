// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.di

import android.content.Context
import com.azure.android.communication.ui.calling.CallComposite
import com.azure.android.communication.ui.calling.getConfig
import com.azure.android.communication.ui.calling.service.sdk.CallingSDKEventHandler
import com.azure.android.communication.ui.calling.service.sdk.CallingSDKWrapper
import com.azure.android.communication.ui.calling.utilities.CoroutineContextProvider

internal class ServiceWrapperDependencyContainer(
    private val parentContext: Context,
    private val callComposite: CallComposite) {

    val configuration by lazy {
        callComposite.getConfig()
    }

   val callingSDKWrapperNative: CallingSDKWrapper by lazy { CallingSDKWrapper(
       parentContext.applicationContext,
        callingSDKEventHandler,
        configuration.callConfig
    ) }


    private val callingSDKEventHandler by lazy {
        CallingSDKEventHandler(
            coroutineContextProvider
        )
    }

    private val coroutineContextProvider by lazy { CoroutineContextProvider() }
}