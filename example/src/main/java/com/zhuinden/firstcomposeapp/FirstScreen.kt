package com.zhuinden.firstcomposeapp

import android.annotation.SuppressLint
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
import com.zhuinden.simplestack.Backstack
import com.zhuinden.simplestack.ServiceBinder
import com.zhuinden.simplestackextensions.servicesktx.add
import com.zhuinden.simplestackextensions.servicesktx.rebind
import kotlinx.parcelize.Parcelize
import androidx.compose.ui.tooling.preview.Preview
import com.zhuinden.simplestackcomposeintegration.services.rememberService

class FirstModel(
    private val backstack: Backstack
): FirstScreen.ActionHandler {
    override fun navigateToSecond() {
        backstack.goTo(SecondKey())
    }
}

@Immutable
@Parcelize
data class FirstKey(val title: String) : ComposeKey() {
    constructor() : this("Hello First Screen!")

    @Composable
    override fun ScreenComposable(modifier: Modifier) {
        FirstScreen(title, modifier)
    }

    override fun bindServices(serviceBinder: ServiceBinder) {
        with(serviceBinder) {
            val firstModel = FirstModel(backstack)

            add(firstModel)
            rebind<FirstScreen.ActionHandler>(firstModel)
        }
    }
}

class FirstScreen private constructor() {
    fun interface ActionHandler {
        fun navigateToSecond()
    }

    companion object {
        @Composable
        @SuppressLint("ComposableNaming")
        operator fun invoke(title: String, modifier: Modifier = Modifier) {
            val eventHandler = rememberService<ActionHandler>()

            Column(
                modifier = modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(onClick = {
                    // onClick is not a composition context, must get ambients above
                    eventHandler.navigateToSecond()
                }, content = {
                    Text(title)
                })
            }
        }
    }
}

@Preview
@Composable
fun FirstScreenPreview() {
    MaterialTheme {
        FirstScreen("This is a preview")
    }
}