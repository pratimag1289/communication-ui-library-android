package com.azure.android.communication.ui.calling.features

interface AcsFeature {
    // Automatically assume, if it ends with "Impl" and is in the classpath, it's enabled
    val isEnabled get() = this::class.java.simpleName.endsWith("Impl")
}