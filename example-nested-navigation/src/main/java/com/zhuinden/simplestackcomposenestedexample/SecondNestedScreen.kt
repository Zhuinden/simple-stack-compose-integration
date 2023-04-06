package com.zhuinden.simplestackcomposenestedexample

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.zhuinden.simplestack.Backstack
import com.zhuinden.simplestackcomposeintegration.core.BackstackProvider
import com.zhuinden.simplestackcomposeintegration.core.LocalBackstack
import kotlinx.parcelize.Parcelize


@Immutable
@Parcelize
data object SecondNestedKey: ComposeKey() {
    operator fun invoke() = this

    @Composable
    override fun ScreenComposable(modifier: Modifier) {
        SecondNestedScreen(modifier)
    }
}

@Composable
fun SecondNestedScreen(modifier: Modifier = Modifier) {
    val backstack = LocalBackstack.current

    Column(
        modifier = modifier
            .background(Color(0xFF, 0x80, 0x80))
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = {
            backstack.goBack()
        }, content = {
            Text("Welcome to Second Nested Screen!")
        })
    }
}

@Preview
@Composable
fun SecondNestedScreenPreview() {
    MaterialTheme {
        BackstackProvider(backstack = Backstack()) {
            SecondNestedScreen()
        }
    }
}
