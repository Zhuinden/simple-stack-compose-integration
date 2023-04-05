package com.zhuinden.simplestackcomposenestedexample

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
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
data class SecondKey(private val noArgsPlaceholder: String = "") : ComposeKey() {
    @Composable
    override fun ScreenComposable(modifier: Modifier) {
        SecondScreen()
    }
}

@Composable
fun SecondScreen() {
    val backstack = LocalBackstack.current

    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(fontWeight = FontWeight.Bold, text = "Single nested stack:")

            Spacer(modifier = Modifier.height(16.dp))
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            propagateMinConstraints = true,
        ) {
            ComposeNavigator {
                createBackstack(
                    History.of(FirstNestedKey()),
                    scopedServices = DefaultServiceProvider()
                )
            }
        }

        Button(
            modifier = Modifier.align(CenterHorizontally),
            onClick = {
                backstack.goTo(ThirdKey())
            },
            content = {
                Text("Open third Screen")
            },
        )
    }
}

@Preview
@Composable
fun SecondScreenPreview() {
    BackstackProvider(backstack = Backstack()) {
        SecondScreen()
    }
}
