package com.zhuinden.simplestackcomposeintegration.core.util

import android.annotation.SuppressLint
import androidx.compose.runtime.*
import com.zhuinden.simplestack.Backstack
import com.zhuinden.simplestack.History

/**
 * Get current history as a compose [State] that will automatically be updated on every navigation.
 */
@Composable
fun Backstack.historyAsState(): State<History<Any>> {
    // History is a Mutable class, but we return a copy, so mutating has no effect
    @SuppressLint("MutableCollectionMutableState")
    val state = remember { mutableStateOf<History<Any>>(getHistory()) }

    DisposableEffect(this) {
        val listener = Backstack.CompletionListener {
            state.value = it.getNewKeys()
        }

        addStateChangeCompletionListener(listener)

        onDispose {
            removeStateChangeCompletionListener(listener)
        }
    }

    return state
}
