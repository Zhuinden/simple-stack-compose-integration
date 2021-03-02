package com.zhuinden.firstcomposeapp

import androidx.compose.material.Text
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.parcelize.Parcelize


@Immutable
@Parcelize
data class SecondKey(private val noArgsPlaceholder: String = "") : ComposeKey() {
    @Composable
    override fun ScreenComposable(modifier: Modifier) {
        SecondScreen(modifier)
    }
}

@Composable
fun SecondScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = {
            // onClick is not a composition context, must get ambients above
            context.showToast("Blah")
        }, content = {
            Text("Hello Second Screen!")
        })
    }
}

@Preview
@Composable
fun SecondScreenPreview() {
    MaterialTheme {
        SecondScreen()
    }
}