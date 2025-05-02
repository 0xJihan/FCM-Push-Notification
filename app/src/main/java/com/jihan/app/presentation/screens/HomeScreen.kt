package com.jihan.app.presentation.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.composables.icons.lucide.Copy
import com.composables.icons.lucide.Lucide
import com.google.firebase.Firebase
import com.google.firebase.messaging.messaging
import com.jihan.app.Routes
import com.jihan.app.domain.utils.fcm.sendImageNotification
import com.jihan.composeutils.CenterBox
import com.jihan.composeutils.CxButton
import com.jihan.composeutils.CxEditText
import com.jihan.composeutils.Gap
import com.jihan.composeutils.copyToClipboard

@Composable
fun HomeScreen(
    navigate: (Routes) -> Unit,
) {

    val context = LocalContext.current
    var token by rememberSaveable { mutableStateOf("") }

    var mToken by remember { mutableStateOf("") }
    var mTitle by remember { mutableStateOf("") }
    var mBody by remember { mutableStateOf("") }

    LaunchedEffect(true) {
        if (token.isNotEmpty()) {
            return@LaunchedEffect
        }
        Firebase.messaging.token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                token = task.result
            }
        }
    }





    Scaffold {
        Column(Modifier
            .fillMaxSize()
            .padding(it)
            .padding(16.dp)) {

            Row {
                TextField(
                    value = token,
                    singleLine = true,
                    onValueChange = {},
                    placeholder = { Text("My Token For This Device") },
                    enabled = false,
                    readOnly = true,
                    label = { Text("My Token For This Device") },
                    modifier = Modifier.weight(1f),
                    trailingIcon = {
                        IconButton(onClick = {
                            token.copyToClipboard(context, token)
                        }) {
                            Icon(Lucide.Copy, null)
                        }
                    })
            }
            Gap(20)

//            Text("Access Token: ${AccessToken().accessToken}")
            Gap(10)

            CxEditText(
                value = mTitle, "Enter Title"
            ) { mTitle = it }

            Gap(10)
            CxEditText(
                value = mBody, "Enter Body"
            ) { mBody = it }
            Gap(10)
            CxEditText(
                value = mToken, "Enter Token"
            ) { mToken = it }



            CenterBox {
                CxButton("Image Notification") {

                    sendImageNotification(
                        topic = "general",
                        title = "Breaking News!",
                        message = "Check out our latest article about technology trends",
                        imageUrl = ""
                    )
                }
            }
        }
    }


}


