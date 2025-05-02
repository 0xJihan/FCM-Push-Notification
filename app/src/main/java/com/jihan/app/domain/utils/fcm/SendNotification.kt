package com.jihan.app.domain.utils.fcm

import com.jihan.app.domain.utils.Constants.API_KEY
import com.jihan.app.domain.utils.Constants.APP_ID
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException


fun sendCustomNotificationToTopic(
    topic: String, title: String, message: String, customizer: (JSONObject) -> JSONObject
) {
    val client = OkHttpClient()

    val baseJson = JSONObject().apply {
        put("app_id", APP_ID)
        put("filters", JSONArray().apply {
            put(JSONObject().apply {
                put("field", "tag")
                put("key", "topic_$topic")
                put("relation", "exists")
            })
        })
        put("headings", JSONObject().put("en", title))
        put("contents", JSONObject().put("en", message))
    }

    // Apply customizations to the base notification
    val json = customizer(baseJson)

    val body = RequestBody.create(
        "application/json; charset=utf-8".toMediaTypeOrNull(), json.toString()
    )

    val request = Request.Builder().url("https://onesignal.com/api/v1/notifications")
        .addHeader("Authorization", "Basic $API_KEY").addHeader("Content-Type", "application/json")
        .post(body).build()

    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            println("Failed to send notification: ${e.message}")
        }

        override fun onResponse(call: Call, response: Response) {
            val responseString = response.body?.string()
            println("Notification response: $responseString")
        }
    })
}

/**
 * Send a notification with a large image (Big Picture Style)
 */
fun sendImageNotification(

    topic: String, title: String, message: String, imageUrl: String
) {
    sendCustomNotificationToTopic(topic, title, message) { json ->
        json.apply {
            put("big_picture", imageUrl)
            put("adm_big_picture", imageUrl) // For Amazon devices
            put("chrome_big_picture", imageUrl) // For Chrome
        }
    }
}

/**
 * Send a notification with action buttons
 */
fun sendNotificationWithButtons(

    topic: String,
    title: String,
    message: String,
    buttons: List<Pair<String, String>> // List of (buttonText, buttonId) pairs
) {
    sendCustomNotificationToTopic(topic, title, message) { json ->
        json.apply {
            put("buttons", JSONArray().apply {
                buttons.forEach { (text, id) ->
                    put(JSONObject().apply {
                        put("id", id)
                        put("text", text)
                    })
                }
            })
        }
    }
}

/**
 * Send a notification with a deep link
 */
fun sendDeepLinkNotification(

    topic: String, title: String, message: String, url: String, // App deep link or web URL
    webUrl: String? = null // Optional separate web URL
) {
    sendCustomNotificationToTopic(topic, title, message) { json ->
        json.apply {
            put("url", url) // Deep link or URL to open
            webUrl?.let { put("web_url", it) } // Optional separate URL for web push
        }
    }
}

/**
 * Send a notification scheduled for future delivery
 */
fun sendScheduledNotification(

    topic: String, title: String, message: String, deliverAt: Long // Timestamp in seconds
) {
    sendCustomNotificationToTopic(topic, title, message) { json ->
        json.apply {
            put("send_after", deliverAt)
        }
    }
}

/**
 * Send a notification with custom sounds
 */
fun sendNotificationWithSound(

    topic: String,
    title: String,
    message: String,
    androidSound: String? = null,
    iosSound: String? = null
) {
    sendCustomNotificationToTopic(topic, title, message) { json ->
        json.apply {
            androidSound?.let { put("android_sound", it) }
            iosSound?.let { put("ios_sound", it) }
        }
    }
}

/**
 * Send a notification with icon and accent color customization
 */
fun sendCustomStyledNotification(

    topic: String,
    title: String,
    message: String,
    iconUrl: String? = null,
    accentColor: String? = null, // Hex color code
    badgeCount: Int? = null // iOS badge count
) {
    sendCustomNotificationToTopic(topic, title, message) { json ->
        json.apply {
            iconUrl?.let { put("large_icon", it) }
            accentColor?.let { put("android_accent_color", it) }
            badgeCount?.let { put("ios_badgeCount", it) }
        }
    }
}

/**
 * Send a carousel notification with multiple images
 * Note: This is an advanced feature that may not be available on all plans
 */
fun sendCarouselNotification(

    topic: String,
    title: String,
    message: String,
    carouselImages: List<Pair<String, String>>
) {
    val client = OkHttpClient()

    val json = JSONObject().apply {
        // Basic notification info
        put("app_id", APP_ID)
        put("filters", JSONArray().apply {
            put(JSONObject().apply {
                put("field", "tag")
                put("key", "topic_$topic")
                put("relation", "exists")
            })
        })
        put("headings", JSONObject().put("en", title))
        put("contents", JSONObject().put("en", message))

        // IMPORTANT: Must match exactly the channel ID created in your app
        put("android_channel_id", "carousel_channel")

        // Carousel content
        put("carousel_content", JSONArray().apply {
            carouselImages.forEach { (imageUrl, linkUrl) ->
                put(JSONObject().apply {
                    put("media", imageUrl)
                    if (linkUrl.isNotEmpty()) {
                        put("url", linkUrl)
                    }
                })
            }
        })
    }

    // Rest of your code to send the request...
}

/**
 * Send a localized notification with multiple language support
 */
fun sendLocalizedNotification(
    appId: String,

    topic: String, titles: Map<String, String>, // Map of language code to title
    messages: Map<String, String> // Map of language code to message
) {
    val client = OkHttpClient()

    val headings = JSONObject()
    titles.forEach { (lang, title) ->
        headings.put(lang, title)
    }

    val contents = JSONObject()
    messages.forEach { (lang, message) ->
        contents.put(lang, message)
    }

    val json = JSONObject().apply {
        put("app_id", appId)
        put("filters", JSONArray().apply {
            put(JSONObject().apply {
                put("field", "tag")
                put("key", "topic_$topic")
                put("relation", "exists")
            })
        })
        put("headings", headings)
        put("contents", contents)
    }

    val body = RequestBody.create(
        "application/json; charset=utf-8".toMediaTypeOrNull(), json.toString()
    )

    val request = Request.Builder().url("https://onesignal.com/api/v1/notifications")
        .addHeader("Authorization", "Basic $API_KEY").addHeader("Content-Type", "application/json")
        .post(body).build()

    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            println("Failed to send notification: ${e.message}")
        }

        override fun onResponse(call: Call, response: Response) {
            val responseString = response.body?.string()
            println("Notification response: $responseString")
        }
    })
}

/**
 * Send a rich media notification with HTML content
 */
fun sendRichHtmlNotification(
    topic: String, title: String, message: String, htmlContent: String
) {
    sendCustomNotificationToTopic(topic, title, message) { json ->
        json.apply {
            put("web_push_additional_data_objects", JSONObject().apply {
                put("html", htmlContent)
            })
            put("chrome_web_image", "https://example.com/placeholder.jpg") // Optional thumbnail
            put("data", JSONObject().apply {
                put("custom_html", htmlContent)
            })
        }
    }
}

/**
 * Send a notification with priority and TTL settings
 */
fun sendPriorityNotification(

    topic: String,
    title: String,
    message: String,
    priority: Int = 10, // Android priority: 10 (high) or 5 (normal)
    ttl: Int = 259200 // Time to live in seconds (3 days default)
) {
    sendCustomNotificationToTopic(topic, title, message) { json ->
        json.apply {
            put("priority", priority)
            put("ttl", ttl)
        }
    }
}

/**
 * Example usage showing how to combine multiple customizations:
 */
fun sendFullyCustomizedNotification(

    topic: String,
    title: String,
    message: String,
    imageUrl: String,
    actionButtons: List<Pair<String, String>>,
    deepLink: String,
    iconUrl: String,
    accentColor: String
) {
    val client = OkHttpClient()

    val json = JSONObject().apply {
        // Target audience
        put("app_id", APP_ID)
        put("filters", JSONArray().apply {
            put(JSONObject().apply {
                put("field", "tag")
                put("key", "topic_$topic")
                put("relation", "exists")
            })
        })

        // Basic content
        put("headings", JSONObject().put("en", title))
        put("contents", JSONObject().put("en", message))

        // Rich media
        put("big_picture", imageUrl)
        put("large_icon", iconUrl)
        put("android_accent_color", accentColor)

        // Interactive elements
        put("url", deepLink)
        put("buttons", JSONArray().apply {
            actionButtons.forEach { (text, id) ->
                put(JSONObject().apply {
                    put("id", id)
                    put("text", text)
                })
            }
        })

        // Other settings
        put("android_channel_id", "default_channel") // Ensure this exists in your app
        put("android_group", "custom_notifications")
        put("collapse_id", "custom_notification_${System.currentTimeMillis()}")
        put("priority", 10) // High priority
    }

    val body = RequestBody.create(
        "application/json; charset=utf-8".toMediaTypeOrNull(), json.toString()
    )

    val request = Request.Builder().url("https://onesignal.com/api/v1/notifications")
        .addHeader("Authorization", "Basic $API_KEY").addHeader("Content-Type", "application/json")
        .post(body).build()

    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            println("Failed to send notification: ${e.message}")
        }

        override fun onResponse(call: Call, response: Response) {
            val responseString = response.body?.string()
            println("Notification response: $responseString")
        }
    })
}

//fun sendNotificationToMultipleTopics(
//    appId: String,
//    restApiKey: String,
//    topics: List<String>,
//    title: String,
//    message: String
//) {
//    val client = OkHttpClient()
//
//    val filters = JSONArray()
//    topics.forEachIndexed { index, topic ->
//        // Add the topic filter
//        filters.put(JSONObject().apply {
//            put("field", "tag")
//            put("key", "topic_$topic")
//            put("relation", "exists")
//        })
//
//        // Add "AND" operator between filters (except after the last one)
//        if (index < topics.size - 1) {
//            filters.put(JSONObject().apply {
//                put("operator", "AND")
//            })
//        }
//    }
//
//    val json = JSONObject().apply {
//        put("app_id", appId)
//        put("filters", filters)
//        put("headings", JSONObject().put("en", title))
//        put("contents", JSONObject().put("en", message))
//    }
//
//    val body = RequestBody.create(
//        "application/json; charset=utf-8".toMediaTypeOrNull(),
//        json.toString()
//    )
//
//    val request = Request.Builder()
//        .url("https://onesignal.com/api/v1/notifications")
//        .addHeader("Authorization", "Basic $restApiKey")
//        .addHeader("Content-Type", "application/json")
//        .post(body)
//        .build()
//
//    client.newCall(request).enqueue(object : Callback {
//        override fun onFailure(call: Call, e: IOException) {
//            println("Failed to send notification: ${e.message}")
//        }
//
//        override fun onResponse(call: Call, response: Response) {
//            val responseString = response.body?.string()
//            println("Notification response: $responseString")
//        }
//    })
//}