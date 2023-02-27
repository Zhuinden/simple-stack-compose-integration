package com.zhuinden.simplestackcomposesimpleexample

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.zhuinden.simplestack.History
import com.zhuinden.simplestack.navigator.ComposeNavigator
import com.zhuinden.simplestackextensions.services.DefaultServiceProvider
import kotlinx.parcelize.Parcelize

@Immutable
@Parcelize
data class SecondKey(private val noArgsPlaceholder: String = "") : ComposeKey() {
    @Composable
    override fun ScreenComposable(modifier: Modifier) {
        SecondScreen()
    }
}

@Composable
fun SecondScreen() {
    Column(
        Modifier.fillMaxSize()
    ) {
        Text("Nested navigation:", Modifier.padding(bottom = 32.dp))

        Box(Modifier
            .weight(1f)
            .fillMaxWidth(),
            propagateMinConstraints = true) {
            ComposeNavigator {
                createBackstack(
                    History.of(FirstNestedKey()),
                    scopedServices = DefaultServiceProvider()
                )
            }
        }
    }
}
