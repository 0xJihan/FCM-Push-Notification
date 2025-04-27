package com.jihan.app.presentation.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jihan.app.R
import com.jihan.app.Routes

@Composable
fun HomeScreen(
   navigate:(Routes)-> Unit,
) {


    Column {

        var state by remember { mutableStateOf(false) }


        Text(
            stringResource(R.string.hello_android), fontSize = 35.sp
        )

        Spacer(Modifier.height(20.dp))

        Button(
            onClick = { navigate(Routes.LoginRoute) }
        ) {
            Text(stringResource(R.string.goToLoginScreen))
        }

    }

}
