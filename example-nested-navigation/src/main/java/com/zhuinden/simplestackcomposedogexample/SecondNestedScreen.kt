package com.zhuinden.simplestackcomposesimpleexample

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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.parcelize.Parcelize


@Immutable
@Parcelize
data class SecondNestedKey(private val noArgsPlaceholder: String = "") : ComposeKey() {
    @Composable
    override fun ScreenComposable(modifier: Modifier) {
        SecondNestedScreen(modifier)
    }
}

@Composable
fun SecondNestedScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current

    Column(
        modifier = modifier.background(Color.Red).fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = {
            // onClick is not a composition context, must get ambients above
            context.showToast("Blah")
        }, content = {
            Text("Hello Second Nested Screen!")
        })
    }
}

@Preview
@Composable
fun SecondNestedScreenPreview() {
    MaterialTheme {
        SecondNestedScreen()
    }
}
