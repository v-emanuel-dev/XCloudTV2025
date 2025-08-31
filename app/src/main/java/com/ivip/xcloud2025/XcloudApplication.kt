package com.ivip.xcloudtv2025

import android.app.Application
import com.ivip.xcloudtv2025.di.appModule
import com.ivip.xcloudtv2025.di.databaseModule
import com.ivip.xcloudtv2025.di.networkModule
import com.ivip.xcloudtv2025.di.preferencesModule
import com.ivip.xcloudtv2025.di.repositoryModule
import com.ivip.xcloudtv2025.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

/**
 * Classe principal da aplicação configurada para usar Koin
 */
class XcloudApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Inicializar Koin
        startKoin {
            androidContext(this@XcloudApplication)
            modules(
                // Remova o appModule antigo (ver passo 3)
                databaseModule,
                networkModule,
                repositoryModule,
                preferencesModule,   // <-- novo
                viewModelModule      // <-- garantir registro do MainViewModel correto
            )
        }

        android.util.Log.d("XcloudApp", "Aplicação inicializada com Koin")
    }
}