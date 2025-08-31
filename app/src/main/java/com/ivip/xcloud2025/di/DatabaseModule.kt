package com.ivip.xcloudtv2025.di

import androidx.room.Room
import com.ivip.xcloudtv2025.data.local.database.AppDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

/**
 * Módulo Koin para configurações do banco de dados
 */
val databaseModule = module {

    // Database
    single<AppDatabase> {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    // DAO
    single { get<AppDatabase>().channelDao() }
}