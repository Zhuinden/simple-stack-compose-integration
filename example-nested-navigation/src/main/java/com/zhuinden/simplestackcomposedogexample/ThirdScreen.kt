package com.zhuinden.simplestackcomposesimpleexample

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.zhuinden.simplestack.History
import com.zhuinden.simplestack.navigator.ComposeNavigator
import com.zhuinden.simplestackcomposeintegration.core.LocalBackstack
import com.zhuinden.simplestackextensions.services.DefaultServiceProvider
import kotlinx.parcelize.Parcelize

@Immutable
@Parcelize
data class ThirdKey(private val noArgsPlaceholder: String = "") : ComposeKey() {
    @Composable
    override fun ScreenComposable(modifier: Modifier) {
        ThirdScreen()
    }
}

@Composable
fun ThirdScreen() {
    val backstack = LocalBackstack.current

    Column(
        Modifier.fillMaxSize()
    ) {
        Text("Nested navigation twice:", Modifier.padding(bottom = 32.dp))

        Box(
            Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            propagateMinConstraints = true
        ) {
            ComposeNavigator(id = "TOP") {
                createBackstack(
                    History.of(FirstNestedKey()),
                    scopedServices = DefaultServiceProvider()
                )
            }
        }

        Box(
            Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            propagateMinConstraints = true
        ) {
            ComposeNavigator(id = "BOTTOM") {
                createBackstack(
                    History.of(FirstNestedKey()),
                    scopedServices = DefaultServiceProvider()
                )
            }
        }

        Button(
            onClick = {
                backstack.goBack()
            }, content = {
                Text("Go back (main)!")
            }, modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}
