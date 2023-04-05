package com.zhuinden.simplestackcomposesimpleexample

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.zhuinden.simplestack.AsyncStateChanger
import com.zhuinden.simplestack.History
import com.zhuinden.simplestack.navigator.rememberBackstack
import com.zhuinden.simplestackcomposeintegration.core.BackstackProvider
import com.zhuinden.simplestackcomposeintegration.core.ComposeStateChanger
import com.zhuinden.simplestackextensions.services.DefaultServiceProvider

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            // This is an example of a longer, more customizable way to display a backstack
            // You can just use ComposeNavigator() in most cases, see SecondScreen().
            val composeStateChanger = remember { ComposeStateChanger() }
            val asyncStateChanger = remember(composeStateChanger) { AsyncStateChanger(composeStateChanger)}

            val backstack = rememberBackstack(asyncStateChanger) {
                createBackstack(
                    scopedServices = DefaultServiceProvider(),
                    initialKeys = History.of(FirstKey())
                )
            }

            BackstackProvider(backstack) {
                MaterialTheme {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        composeStateChanger.RenderScreen()
                    }
                }
            }
        }
    }
}
