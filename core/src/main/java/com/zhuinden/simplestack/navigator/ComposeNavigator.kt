package com.zhuinden.simplestack.navigator

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.zhuinden.simplestack.AsyncStateChanger
import com.zhuinden.simplestack.Backstack
import com.zhuinden.simplestackcomposeintegration.core.BackstackProvider
import com.zhuinden.simplestackcomposeintegration.core.ComposeStateChanger

/**
 * Create a Simple Stack navigator that will handle backstack and display screens.
 *
 * @see ComposeStateChanger for documentation on [animationConfiguration] argument.
 * @see rememberBackstack for documentation on [init] argument.
 */
@Composable
fun ComposeNavigator(
    modifier: Modifier = Modifier,
    animationConfiguration: ComposeStateChanger.AnimationConfiguration =
        ComposeStateChanger.AnimationConfiguration(),
    init: ComposeNavigatorInitializer.() -> Backstack,
) {
    val composeStateChanger = remember { ComposeStateChanger(animationConfiguration) }

    val backstack = rememberBackstack(AsyncStateChanger(composeStateChanger), init)

    BackstackProvider(backstack) {
        composeStateChanger.RenderScreen(modifier)
    }
}
