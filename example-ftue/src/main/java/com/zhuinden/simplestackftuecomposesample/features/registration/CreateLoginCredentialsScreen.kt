package com.zhuinden.simplestackftuecomposesample.features.registration

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rxjava2.subscribeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.zhuinden.simplestackcomposeintegration.services.rememberService
import com.zhuinden.simplestackftuecomposesample.utils.set
import com.zhuinden.simplestackftuecomposesample.utils.subscribeAsState

@Composable
fun CreateLoginCredentialsScreen(
    modifier: Modifier = Modifier,
) {
    val registrationViewModel = rememberService<RegistrationViewModel>()

    val username = registrationViewModel.username.subscribeAsState()
    val password = registrationViewModel.password.subscribeAsState()

    val isEnabled = registrationViewModel.isRegisterAndLoginEnabled.subscribeAsState(initial = false)

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        TextField(
            value = username.value ?: "",
            singleLine = true,
            placeholder = { Text("Username") },
            onValueChange = registrationViewModel.username::set,
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = password.value ?: "",
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            placeholder = { Text("Password") },
            onValueChange = registrationViewModel.password::set,
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = registrationViewModel::onRegisterAndLoginClicked,
            enabled = isEnabled.value,
        ) {
            Text(text = "Register and login")
        }
    }
}