package com.jihan.app.presentation.screens

import android.os.Build
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.jihan.app.R
import com.jihan.app.domain.utils.Datastore
import com.jihan.app.domain.utils.asMutableState
import com.jihan.app.domain.utils.collectAsStateWithLifecycleNotNull
import com.jihan.composeutils.CxSegmentedButton
import com.michaelflisar.composethemer.ComposeTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen() {


    Scaffold(topBar = {
       TopAppBar(title = {
           Text("Setting")
       })
    }) {
        LazyColumn(
            Modifier
                .fillMaxSize()
                .padding(it)
                .padding(16.dp)
        ) {
            item {


                val scope = rememberCoroutineScope()

                val currentThemeKey = Datastore.themeKey.collectAsStateWithLifecycleNotNull()

                val themesList = remember { ComposeTheme.getRegisteredThemes() }
                val showThemeLabel by Datastore.showThemeLabel.collectAsStateWithLifecycleNotNull()
                var baseTheme by Datastore.baseTheme.asMutableState()


                //?===================================================================

                ThemeChooser(
                    showThemeLabel,
                    scope,
                    themesList,
                    currentThemeKey,
                    themesTitle = stringResource(R.string.themes),
                    showLabelText = stringResource(R.string.show_label)
                )


                val currentItem by remember(baseTheme) {
                    derivedStateOf {
                        when (baseTheme) {
                            ComposeTheme.BaseTheme.System -> 0
                            ComposeTheme.BaseTheme.Light -> 1
                            ComposeTheme.BaseTheme.Dark -> 2
                        }
                    }
                }


                CxSegmentedButton(
                    items = listOf(
                        stringResource(R.string.system),
                        stringResource(R.string.light),
                        stringResource(R.string.dark)
                    ),
                    currentItem = currentItem,
                    title = stringResource(R.string.base_theme),
                ) { index ->
                    when (index) {
                        0 -> baseTheme = ComposeTheme.BaseTheme.System


                        1 -> baseTheme = ComposeTheme.BaseTheme.Light


                        2 -> baseTheme = ComposeTheme.BaseTheme.Dark

                    }
                }


                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    var dynamicTheme by Datastore.dynamic.asMutableState()
                    CxSegmentedButton(
                        items = listOf(
                            stringResource(R.string.yes), stringResource(R.string.no)
                        ),
                        currentItem = if (dynamicTheme) 0 else 1,
                        title = stringResource(R.string.enable_dynamic_theme)
                    ) { pos ->
                        dynamicTheme = pos == 0
                    }
                }


            }
        }
    }

}


@Composable
private fun ThemeItem(
    theme: ComposeTheme.Theme, selected: Boolean, label: Boolean, onClick: (String) -> Unit
) {


    Card(
        onClick = { onClick(theme.key) },
        modifier = Modifier
            .padding(4.dp)
            .defaultMinSize(50.dp),

        ) {

        Row(
            Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            AnimatedVisibility(selected) {
                Icon(
                    Icons.Default.Done,
                    contentDescription = theme.key,
                    tint = theme.colorSchemeLight.primary
                )
            }
            Spacer(Modifier.width(8.dp))
            AnimatedVisibility(label) {
                Text(theme.key)
            }

            Spacer(Modifier.width(8.dp))
            Card(
                Modifier.size(30.dp), shape = CircleShape, colors = CardDefaults.cardColors(
                    containerColor = theme.colorSchemeLight.primary
                )
            ) {

            }
        }


    }
}


@Composable
fun ThemeChooser(
    showThemeLabel: Boolean,
    scope: CoroutineScope,
    themesList: List<ComposeTheme.Theme>,
    currentThemeKey: androidx.compose.runtime.State<String>,
    themesTitle: String,
    showLabelText: String
) {
    Row(
        Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(themesTitle, style = MaterialTheme.typography.titleMedium)


        Row(verticalAlignment = Alignment.CenterVertically) {

            Text(showLabelText, Modifier.padding(end = 8.dp))
            Checkbox(
                checked = showThemeLabel,
                onCheckedChange = {
                    scope.launch {
                        Datastore.showThemeLabel.update(it)
                    }
                },
            )
        }

    }


    LazyRow(Modifier.fillMaxWidth()) {
        items(themesList) { theme ->
            ThemeItem(theme, currentThemeKey.value == theme.key, showThemeLabel) {
                scope.launch {
                    Datastore.themeKey.update(theme.key)
                }
            }
        }
    }
}
