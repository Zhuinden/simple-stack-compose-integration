package com.zhuinden.simplestackftuecomposesample.features.login

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.zhuinden.simplestack.ServiceBinder
import com.zhuinden.simplestackextensions.servicesktx.add
import com.zhuinden.simplestackextensions.servicesktx.lookup
import com.zhuinden.simplestackftuecomposesample.app.AuthenticationManager
import com.zhuinden.simplestackftuecomposesample.app.ComposeKey
import kotlinx.parcelize.Parcelize

@Parcelize
data object LoginKey : ComposeKey() {
    operator fun invoke(): LoginKey = LoginKey

    @Suppress("RemoveExplicitTypeArguments")
    override fun bindServices(serviceBinder: ServiceBinder) {
        with(serviceBinder) {
            add(
                LoginViewModel(
                    authenticationManager = lookup<AuthenticationManager>(),
                    backstack = backstack,
                )
            )
        }
    }

    @Composable
    override fun ScreenComposable(modifier: Modifier) {
        LoginScreen(modifier = modifier)
    }
}