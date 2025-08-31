package com.ivip.xcloudtv2025

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.OnBackPressedCallback
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.ivip.xcloudtv2025.presentation.theme.XcloudTVTheme
import com.ivip.xcloudtv2025.presentation.screens.main.MainScreen
import com.ivip.xcloudtv2025.presentation.screens.main.MainViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * Activity principal do aplicativo Xcloud TV (com Koin)
 */
class MainActivity : ComponentActivity() {

    private val mainViewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Configuração para Android TV
        setupTVMode()

        setContent {
            XcloudTVTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen(viewModel = mainViewModel)
                }
            }
        }

        // Inicializa canais padrão se necessário
        mainViewModel.initializeApp()

        // Handle back press
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                mainViewModel.handleBackAction()
            }
        })
    }

    private fun setupTVMode() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        requestedOrientation = android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        window.addFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    override fun onResume() {
        super.onResume()
        mainViewModel.refreshData()
    }

    override fun onPause() {
        super.onPause()
        mainViewModel.saveCurrentState()
    }

    override fun onDestroy() {
        super.onDestroy()
        mainViewModel.cleanup()
    }

    override fun onKeyDown(keyCode: Int, event: android.view.KeyEvent?): Boolean {
        return when (keyCode) {
            android.view.KeyEvent.KEYCODE_DPAD_CENTER,
            android.view.KeyEvent.KEYCODE_ENTER -> {
                mainViewModel.handleSelectAction()
                true
            }
            android.view.KeyEvent.KEYCODE_MENU -> {
                mainViewModel.openSettings()
                true
            }
            else -> super.onKeyDown(keyCode, event)
        }
    }
}