package com.anvorgueso.dexium

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.anvorgueso.dexium.ui.components.ProvideAppLocale
import com.anvorgueso.dexium.ui.navigation.DexiumNavHost
import com.anvorgueso.dexium.ui.theme.DexiumTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val language by viewModel.language.collectAsState()

            // Wraps everything, so changing the language in settings re-resolves every string
            // in place without restarting the activity.
            ProvideAppLocale(languageTag = language.androidTag) {
                DexiumTheme {
                    Surface(modifier = Modifier.fillMaxSize()) {
                        DexiumNavHost()
                    }
                }
            }
        }
    }
}
