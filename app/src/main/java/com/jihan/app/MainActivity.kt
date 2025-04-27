package com.jihan.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.jihan.app.domain.utils.Datastore
import com.jihan.app.domain.utils.collectAsStateNotNull
import com.jihan.app.domain.utils.collectAsStateWithLifecycleNotNull
import com.jihan.app.domain.utils.setNetworkListenerContent
import com.michaelflisar.composethemer.ComposeTheme
import java.util.Locale


class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setNetworkListenerContent {

            val baseTheme = Datastore.baseTheme.collectAsStateNotNull()
            val dynamic = Datastore.dynamic.collectAsStateWithLifecycleNotNull()
            val theme =
                Datastore.themeKey.collectAsStateWithLifecycleNotNull()// the key of a registered theme
            val state = ComposeTheme.State(baseTheme, dynamic, theme)
            ComposeTheme(state = state) {
                NavigationController()

            }
        }

    }


    @Composable
    private fun SetLanguage(language: String) {
        val locale = Locale(language)
        val configuration = LocalConfiguration.current
        configuration.setLocale(locale)
        val displayMetrics = this.resources.displayMetrics
        resources.updateConfiguration(configuration, displayMetrics)
    }
}



