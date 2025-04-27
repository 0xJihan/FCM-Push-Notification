package com.jihan.app.domain.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.util.Patterns
import androidx.compose.foundation.layout.FlowRowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jihan.composeutils.toast
import com.michaelflisar.kotpreferences.core.interfaces.StorageSetting
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext
import org.json.JSONObject
import retrofit2.Response
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

@Composable
fun <T> StorageSetting<T>.collectAsStateWithLifecycle(
    lifecycle: Lifecycle,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
    context: CoroutineContext = EmptyCoroutineContext,
    initialValue: T? = getCached()
): State<T?> {
    return flow.collectAsStateWithLifecycle(
        initialValue = initialValue, lifecycle, minActiveState, context
    )
}

@Composable
fun <T> StorageSetting<T>.collectAsStateWithLifecycle(
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
    context: CoroutineContext = EmptyCoroutineContext,
    initialValue: T? = getCached()
): State<T?> {
    return flow.collectAsStateWithLifecycle(
        initialValue = initialValue, lifecycleOwner, minActiveState, context
    )
}

@Composable
fun <T> StorageSetting<T>.collectAsStateWithLifecycleNotNull(
    lifecycle: Lifecycle,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
    context: CoroutineContext = EmptyCoroutineContext,
    initialValue: T = getCached() ?: value
): State<T> {
    return flow.collectAsStateWithLifecycle(
        initialValue = initialValue, lifecycle, minActiveState, context
    )
}

@Composable
fun <T> StorageSetting<T>.collectAsStateWithLifecycleNotNull(
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
    context: CoroutineContext = EmptyCoroutineContext,
    initialValue: T = getCached() ?: value
): State<T> {
    return flow.collectAsStateWithLifecycle(
        initialValue = initialValue, lifecycleOwner, minActiveState, context
    )
}

@Composable
fun <T> StorageSetting<T>.collectAsState(initialValue: T? = getCached()): State<T?> {
    return flow.collectAsState(initial = initialValue)
}

@Composable
fun <T> StorageSetting<T>.collectAsStateNotNull(
    initialValue: T = getCached() ?: value
): State<T> {
    return flow.collectAsState(initial = initialValue)
}

@Composable
fun <T> StorageSetting<T>.asMutableState(): MutableState<T> {
    val state = remember { mutableStateOf(value) }
    LaunchedEffect(Unit) {
        snapshotFlow {
            state.value
        }.collect {
            withContext(Dispatchers.IO) {
                update(it)
            }
        }
    }
    LaunchedEffect(Unit) {
        flow.collect {
            state.value = it
        }
    }
    return state
}


@Composable
fun LazyItemScope.Gap(size: Int) {
    Spacer(Modifier.height(size.dp))
}


@Composable
fun Int.painter(): Painter {
    return painterResource(this)
}


fun String?.isEmail(): Boolean {
    if (this.isNullOrEmpty()) return false
    return Patterns.EMAIL_ADDRESS.matcher(this).matches()

}

fun String.copyToClipboard(context: Context) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("Copied Text", this)
    clipboard.setPrimaryClip(clip)
    "Copied to clipboard".toast(context)
}


suspend fun <T> handleResponse(
    response: Response<T>,
    stateFlow: MutableStateFlow<UiState<T&Any>>,
) {
    if (response.isSuccessful) {

        stateFlow.emit(UiState.Success(response.body()!!))

    } else {
        val errorMessage = response.errorBody()?.let {
            JSONObject(it.charStream().readText()).getString("message")
        } ?: response.message()
        stateFlow.emit(UiState.Error(errorMessage))
    }
}

suspend fun <T, R> handleResponse(
    response: Response<T>,
    stateFlow: MutableStateFlow<UiState<R>>,
    transform: (T) -> R // Transformation function
) {
    if (response.isSuccessful) {
        response.body()?.let {
            stateFlow.emit(UiState.Success(transform(it))) // Apply transformation
        } ?: stateFlow.emit(UiState.Error("Empty response body"))
    } else {
        val errorMessage = response.errorBody()?.let {
            JSONObject(it.charStream().readText()).getString("message")
        } ?: response.message()
        stateFlow.emit(UiState.Error(errorMessage))
    }
}


