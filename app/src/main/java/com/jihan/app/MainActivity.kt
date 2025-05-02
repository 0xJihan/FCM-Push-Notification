package com.jihan.app

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalConfiguration
import androidx.core.content.ContextCompat
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


                LaunchedEffect(true) {
                    askNotificationPermission()
                }
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

    // Declare the launcher at the top of your Activity/Fragment:
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (isGranted) {
            // FCM SDK (and your app) can post notifications.
        } else {
            // TODO: Inform user that that your app will not show notifications.
        }
    }

    private fun askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                // FCM SDK (and your app) can post notifications.
            } else if (shouldShowRequestPermissionRationale(android.Manifest.permission.POST_NOTIFICATIONS)) {
                // TODO: display an educational UI explaining to the user the features that will be enabled
                //       by them granting the POST_NOTIFICATION permission. This UI should provide the user
                //       "OK" and "No thanks" buttons. If the user selects "OK," directly request the permission.
                //       If the user selects "No thanks," allow the user to continue without notifications.
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}



