package com.zhuinden.simplestackcomposenestedexample

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zhuinden.simplestack.Backstack
import com.zhuinden.simplestack.History
import com.zhuinden.simplestackcomposeintegration.core.BackstackProvider
import com.zhuinden.simplestackcomposeintegration.core.ComposeNavigator
import com.zhuinden.simplestackcomposeintegration.core.LocalBackstack
import com.zhuinden.simplestackextensions.services.DefaultServiceProvider
import kotlinx.parcelize.Parcelize

@Immutable
@Parcelize
data object ThirdKey: ComposeKey() {
    operator fun invoke() = this

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
        Column(modifier = Modifier.padding(16.dp)) {
            Text(fontWeight = FontWeight.Bold, text = "Multiple nested stacks:")

            Spacer(modifier = Modifier.height(16.dp))
        }

        Box(
            Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            propagateMinConstraints = true
        ) {
            ComposeNavigator(id = "TOP", interceptBackButton = false) {
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
            ComposeNavigator(id = "BOTTOM", interceptBackButton = false) {
                createBackstack(
                    History.of(FirstNestedKey()),
                    scopedServices = DefaultServiceProvider()
                )
            }
        }

        Button(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            onClick = {
                backstack.goBack()
            },
            content = {
                Text("Go back (main)!")
            },
        )
    }
}

@Preview
@Composable
fun ThirdScreenPreview() {
    BackstackProvider(backstack = Backstack()) {
        ThirdScreen()
    }
}