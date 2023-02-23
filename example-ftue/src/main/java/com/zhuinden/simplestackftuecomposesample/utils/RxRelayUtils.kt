package com.zhuinden.simplestackftuecomposesample.utils

import androidx.compose.runtime.*
import com.jakewharton.rxrelay2.BehaviorRelay

fun <T : Any> BehaviorRelay<T>.get(): T = value!!

fun <T : Any> BehaviorRelay<T>.getOrNull(): T? = value

fun <T : Any> BehaviorRelay<T>.set(value: T) {
    this.accept(value)
}

@Composable
fun <T : Any> BehaviorRelay<T>.subscribeAsState(): State<T?> {
    val state = remember { mutableStateOf(value) }
    DisposableEffect(this) {
        val disposable = subscribe {
            state.value = it
        }
        onDispose { disposable.dispose() }
    }
    return state
}