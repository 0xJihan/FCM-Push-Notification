package com.jihan.app

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.util.Log
import com.jihan.app.data.di.appModule
import com.michaelflisar.composethemer.ComposeTheme
import com.michaelflisar.composethemer.themes.ComposeThemes
import com.onesignal.OneSignal
import com.onesignal.debug.LogLevel
import com.onesignal.notifications.INotificationClickEvent
import com.onesignal.notifications.INotificationClickListener
import com.onesignal.notifications.IPermissionObserver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin


class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MyApplication)
            modules(appModule)
        }

        createNotificationChannels()
        ComposeTheme.register(*ComposeThemes.ALL.toTypedArray())

        OneSignal.initWithContext(this, Constants.API_KEY)

        OneSignal.Debug.logLevel = LogLevel.VERBOSE

        subscribeToTopic("general")

        CoroutineScope(Dispatchers.Main).launch {

            if (OneSignal.Notifications.canRequestPermission) {
                OneSignal.Notifications.requestPermission(true)
            }


        }

        setupNotificationHandlers()


    }

    private fun setupNotificationHandlers() {

        if (!OneSignal.User.pushSubscription.optedIn) {
            // Ask for permission first, then:
            OneSignal.User.pushSubscription.optIn()
        }


        // Handle notification clicks
        OneSignal.Notifications.addClickListener(object : INotificationClickListener {
            override fun onClick(event: INotificationClickEvent) {
                Log.d("OneSignal", "Notification clicked: ${event.notification.title}")
            }
        })

        // Monitor permission changes
        OneSignal.Notifications.addPermissionObserver(object : IPermissionObserver {
            override fun onNotificationPermissionChange(permission: Boolean) {
                Log.d("OneSignal", "Permission changed: $permission")
            }
        })
    }


    fun subscribeToTopic(topic: String) {
        // Add a tag with the topic name
        OneSignal.User.addTag("topic_$topic", "true")
        Log.d("OneSignal", "Subscribed to topic: $topic")
    }


    // 1. Create notification channels (Android 8.0+)
    // This should be called VERY early in your app's lifecycle - in Application.onCreate()
    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val carouselChannel = NotificationChannel(
                "carousel_channel", // ← This ID must match exactly what you use in API calls
                "Carousel Notifications", NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifications with multiple images"
            }

            // Register channels
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(carouselChannel)

            // Log confirmation
            Log.d("Notifications", "Created carousel_channel notification channel")
        }
    }

}


//// 2. Handle notification clicks and actions in your app
//class MainActivity : AppCompatActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//
//        // Create notification channels
//        createNotificationChannels(this)
//
//        // Initialize OneSignal with notification opened handler
//        OneSignal.initialize(this, "YOUR_ONESIGNAL_APP_ID")
//
//        // Handle notification clicks
//        OneSignal.Notifications.addClickListener { event ->
//            val notificationData = event.notification.additionalData
//
//            // Handle general click
//            val deepLink = notificationData?.optString("url")
//            deepLink?.let { handleDeepLink(it) }
//
//            // Handle button actions
//            val actionId = event.action?.actionId
//            actionId?.let { handleButtonAction(it, notificationData) }
//        }
//    }
//
//    private fun handleDeepLink(deepLink: String) {
//        // Process the deep link
//        if (deepLink.startsWith("yourapp://")) {
//            // Parse and handle internal deep link
//            val path = deepLink.removePrefix("yourapp://")
//
//            when {
//                path.startsWith("products/") -> {
//                    val productId = path.removePrefix("products/")
//                    openProductDetails(productId)
//                }
//                path == "special_offers/holiday" -> {
//                    openSpecialOffers()
//                }
//                // Handle other deep links
//            }
//        } else {
//            // Open external URL
//            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(deepLink))
//            startActivity(intent)
//        }
//    }
//
//    private fun handleButtonAction(actionId: String, data: JSONObject?) {
//        when (actionId) {
//            "shop_action" -> {
//                // Open shop
//                startActivity(Intent(this, ShopActivity::class.java))
//            }
//            "remind_action" -> {
//                // Schedule a reminder
//                scheduleLocalReminder()
//            }
//            "details_action" -> {
//                // Show details
//                val productId = data?.optString("productId")
//                if (productId != null) {
//                    openProductDetails(productId)
//                }
//            }
//            // Handle other button actions
//        }
//    }
//
//    private fun openProductDetails(productId: String) {
//        val intent = Intent(this, ProductDetailActivity::class.java).apply {
//            putExtra("PRODUCT_ID", productId)
//        }
//        startActivity(intent)
//    }
//
//    private fun openSpecialOffers() {
//        startActivity(Intent(this, SpecialOffersActivity::class.java))
//    }
//
//    private fun scheduleLocalReminder() {
//        // Schedule a local reminder using WorkManager or AlarmManager
//    }
//}
