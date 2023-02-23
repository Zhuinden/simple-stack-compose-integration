package com.zhuinden.simplestackftuecomposesample.features.profile

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.zhuinden.simplestack.ServiceBinder
import com.zhuinden.simplestackextensions.servicesktx.add
import com.zhuinden.simplestackextensions.servicesktx.lookup
import com.zhuinden.simplestackftuecomposesample.app.AuthenticationManager
import com.zhuinden.simplestackftuecomposesample.app.ComposeKey
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProfileKey(
    val username: String
) : ComposeKey() {
    @Suppress("RemoveExplicitTypeArguments")
    override fun bindServices(serviceBinder: ServiceBinder) {
        with(serviceBinder) {
            add(
                ProfileViewModel(
                    authenticationManager = lookup<AuthenticationManager>(),
                    backstack = backstack,
                )
            )
        }
    }

    override fun getScopeTag(): String = toString()

    @Composable
    override fun ScreenComposable(modifier: Modifier) {
        ProfileScreen(
            username = username,
            modifier = modifier,
        )
    }
}
