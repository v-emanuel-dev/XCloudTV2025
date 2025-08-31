// app/src/main/java/com/ivip/xcloud2025/di/PreferencesModule.kt
package com.ivip.xcloudtv2025.di

import com.ivip.xcloudtv2025.data.local.preferences.PreferencesManager
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val preferencesModule = module {
    single { PreferencesManager(androidContext()) }
}
