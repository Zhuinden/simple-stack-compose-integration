package com.zhuinden.simplestackftuecomposesample.features.registration

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rxjava2.subscribeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.zhuinden.simplestackcomposeintegration.services.rememberService
import com.zhuinden.simplestackftuecomposesample.utils.set
import com.zhuinden.simplestackftuecomposesample.utils.subscribeAsState

@Composable
fun EnterProfileDataScreen(
    modifier: Modifier = Modifier,
) {
    val registrationViewModel = rememberService<RegistrationViewModel>()

    val fullName = registrationViewModel.fullName.subscribeAsState()
    val bio = registrationViewModel.bio.subscribeAsState()

    val isEnabled = registrationViewModel.isEnterProfileNextEnabled.subscribeAsState(initial = false)

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        TextField(
            value = fullName.value ?: "",
            singleLine = true,
            placeholder = { Text("Full name") },
            onValueChange = registrationViewModel.fullName::set,
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = bio.value ?: "",
            singleLine = true,
            placeholder = { Text("Bio") },
            onValueChange = registrationViewModel.bio::set,
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = registrationViewModel::onEnterProfileNextClicked,
            enabled = isEnabled.value,
        ) {
            Text(text = "Next")
        }
    }
}