package com.zhuinden.simplestackcomposeintegration.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.zhuinden.simplestack.AsyncStateChanger
import com.zhuinden.simplestack.Backstack

/**
 * Create a Simple Stack navigator that will handle backstack and display screens.
 *
 * @see ComposeStateChanger for documentation on [animationConfiguration] argument.
 * @see rememberBackstack for documentation on the rest of the arguments
 */
@Composable
fun ComposeNavigator(
    modifier: Modifier = Modifier,
    animationConfiguration: ComposeStateChanger.AnimationConfiguration =
        ComposeStateChanger.AnimationConfiguration(),
    id: String = "DEFAULT_SINGLE_COMPOSE_STACK_IDENTIFIER",
    interceptBackButton: Boolean = true,
    init: ComposeNavigatorInitializer.() -> Backstack,
) {
    val composeStateChanger = remember { ComposeStateChanger(animationConfiguration) }
    val asyncStateChanger = remember(composeStateChanger) { AsyncStateChanger(composeStateChanger) }

    val backstack = rememberBackstack(asyncStateChanger, id, interceptBackButton, init)

    BackstackProvider(backstack) {
        composeStateChanger.RenderScreen(modifier)
    }
}
