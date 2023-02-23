package com.zhuinden.simplestackftuecomposesample.features.registration

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.zhuinden.simplestack.ScopeKey
import com.zhuinden.simplestackftuecomposesample.app.ComposeKey
import kotlinx.parcelize.Parcelize

@Parcelize
data object CreateLoginCredentialsKey : ComposeKey(), ScopeKey.Child {
    operator fun invoke(): CreateLoginCredentialsKey = CreateLoginCredentialsKey

    override fun getParentScopes(): List<String> = listOf(RegistrationViewModel::class.java.name)

    @Composable
    override fun ScreenComposable(modifier: Modifier) {
        CreateLoginCredentialsScreen(modifier = modifier)
    }
}