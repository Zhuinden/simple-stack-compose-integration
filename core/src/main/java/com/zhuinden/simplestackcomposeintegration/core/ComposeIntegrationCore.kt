/*
 * Copyright (C) 2021 Gabor Varadi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zhuinden.simplestackcomposeintegration.core

import android.annotation.SuppressLint
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.TweenSpec
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.SaveableStateHolder
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.util.fastForEach
import androidx.compose.ui.util.fastMap
import androidx.compose.ui.util.fastMaxBy
import com.zhuinden.simplestack.AsyncStateChanger
import com.zhuinden.simplestack.Backstack
import com.zhuinden.simplestack.StateChange
import com.zhuinden.simplestack.StateChanger
import com.zhuinden.simplestackcomposeintegration.core.ComposeStateChanger.AnimationConfiguration.ComposableAnimationSpec
import com.zhuinden.simplestackcomposeintegration.core.ComposeStateChanger.AnimationConfiguration.ComposableTransition

/**
 * Composition local to access the key within screens.
 */
val LocalComposeKey =
    staticCompositionLocalOf<DefaultComposeKey> { throw IllegalStateException("Key does not exist in this composable scope") }

/**
 * A key that receives a modifier and provides for the compose key composition local.
 *
 * Please note that it is not Parcelable by default.
 */
abstract class DefaultComposeKey {
    /**
     * The screen rendering call.
     */
    @Composable
    fun RenderComposable(modifier: Modifier = Modifier) {
        val key = this

        CompositionLocalProvider(LocalComposeKey provides (key)) {
            key(key) {
                ScreenComposable(modifier = modifier)
            }
        }
    }

    /**
     * The key used for the SaveableStateProvider.
     *
     * It must be immutable, unique, and saveable into a Bundle.
     *
     * If it is `this`, then `this` must be Parcelable.
     */
    abstract val saveableStateProviderKey: Any

    /**
     * The screen composable.
     */
    @Composable
    protected abstract fun ScreenComposable(modifier: Modifier)
}

/**
 * A state changer that allows switching between composables, animating the transition.
 */
class ComposeStateChanger(
    private val animationConfiguration: AnimationConfiguration = AnimationConfiguration()
) : AsyncStateChanger.NavigationHandler {
    private var currentStateChange by mutableStateOf<StateChangeData?>(null)

    override fun onNavigationEvent(
        stateChange: StateChange,
        completionCallback: StateChanger.Callback
    ) {
        currentStateChange = StateChangeData(stateChange, completionCallback)
    }

    /**
     * Configuration for the screen switching animations.
     */
    class AnimationConfiguration(
        /**
         * The previous transition.
         */
        @Suppress("UNUSED_ANONYMOUS_PARAMETER")
        val previousComposableTransition: ComposableTransition =
            ComposableTransition { modifier, stateChange, fullWidth, fullHeight, animationProgress ->
                modifier.then(
                    when (stateChange.direction) {
                        StateChange.FORWARD -> Modifier.graphicsLayer {
                            translationX = 0 + (-1) * fullWidth * animationProgress.value
                        }

                        StateChange.BACKWARD -> Modifier.graphicsLayer {
                            translationX = 0 + fullWidth * animationProgress.value
                        }

                        else /* REPLACE */ -> Modifier.graphicsLayer {
                            alpha = (1 - animationProgress.value)
                        }
                    }
                )
            },
        /**
         * The new transition.
         */
        @Suppress("UNUSED_ANONYMOUS_PARAMETER")
        val newComposableTransition: ComposableTransition =
            ComposableTransition { modifier, stateChange, fullWidth, fullHeight, animationProgress ->
                modifier.then(
                    when (stateChange.direction) {
                        StateChange.FORWARD -> Modifier.graphicsLayer {
                            translationX = fullWidth + (-1) * fullWidth * animationProgress.value
                        }
                        StateChange.BACKWARD -> Modifier.graphicsLayer {
                            translationX = -1 * fullWidth + fullWidth * animationProgress.value
                        }
                        else /* REPLACE */ -> Modifier.graphicsLayer{
                            alpha = 0 + animationProgress.value
                        }
                    }
                )
            },
        /**
         * The animation spec.
         */
        @Suppress("UNUSED_ANONYMOUS_PARAMETER")
        val animationSpec: ComposableAnimationSpec = ComposableAnimationSpec { stateChange ->
            TweenSpec(250, 0, LinearEasing)
        },
        /**
         * An optional composable content wrapper.
         */
        val contentWrapper: ComposableContentWrapper = object : ComposableContentWrapper {
            @Composable
            override fun ContentWrapper(stateChange: StateChange, block: @Composable() () -> Unit) {
                block()
            }
        }
    ) {
        /**
         * An interface to describe transition of a composables.
         */
        fun interface ComposableTransition {
            @SuppressLint("ModifierFactoryExtensionFunction")
            fun animateComposable(
                modifier: Modifier,
                stateChange: StateChange,
                fullWidth: Int,
                fullHeight: Int,
                animationProgress: State<Float>
            ): Modifier
        }

        /**
         * An interface to describe animation spec of transitions.
         */
        fun interface ComposableAnimationSpec {
            fun defineAnimationSpec(stateChange: StateChange): AnimationSpec<Float>
        }

        /**
         * An interface to describe an optional content wrapper for the animated content.
         */
        interface ComposableContentWrapper {
            @Composable
            fun ContentWrapper(stateChange: StateChange, block: @Composable() () -> Unit)
        }
    }

    private class StateChangeData(
        val stateChange: StateChange,
        val completionCallback: StateChanger.Callback
    )

    private data class DisplayedKey(
        val key: DefaultComposeKey,
        val transition: ComposableTransition?,
        val animationProgress: State<Float>
    )

    @Composable
    fun RenderScreen(modifier: Modifier = Modifier) {
        LocalBackstack.current // force `BackstackProvider` to be set

        val currentStateChange = currentStateChange ?: return

        val displayedKeys = remember { mutableStateOf(emptyList<DisplayedKey>()) }

        DetermineDisplayedScreens(currentStateChange, displayedKeys)

        DisplayScreens(displayedKeys, modifier, currentStateChange)
    }

    @Composable
    private fun DisplayScreens(
        displayedKeys: State<List<DisplayedKey>>,
        modifier: Modifier,
        currentStateChange: StateChangeData
    ) {
        val saveableStateHolder = rememberSaveableStateHolder()
        CleanupStaleSavedStates(saveableStateHolder)

        val measurePolicy = remember { SizeSavingMeasurePolicy() }
        Layout({
            for (displayedKey in displayedKeys.value) {
                val animationModifier = displayedKey.transition?.animateComposable(
                    modifier,
                    currentStateChange.stateChange,
                    measurePolicy.fullWidth,
                    measurePolicy.fullHeight,
                    displayedKey.animationProgress
                ) ?: modifier

                val key = displayedKey.key

                key(key) {
                    saveableStateHolder.SaveableStateProvider(key) {
                        animationConfiguration.contentWrapper.ContentWrapper(
                            currentStateChange.stateChange
                        ) {
                            key.RenderComposable(animationModifier)
                        }
                    }
                }

            }
        }, Modifier, measurePolicy)
    }

    @Composable
    private fun DetermineDisplayedScreens(
        currentStateChange: StateChangeData,
        displayedKeys: MutableState<List<DisplayedKey>>
    ) {
        LaunchedEffect(currentStateChange) {
            val topNewKey = currentStateChange.stateChange.topNewKey<DefaultComposeKey>()
            val topOldKey = currentStateChange.stateChange.topPreviousKey<DefaultComposeKey>()

            if (topOldKey == null) {
                // First state change, do not animate
                displayedKeys.value = listOf(
                    DisplayedKey(topNewKey, null, mutableStateOf(0f))
                )
                currentStateChange.completionCallback.stateChangeComplete()
                return@LaunchedEffect
            }


            val animatable = Animatable(0f)
            val animationProgress = mutableStateOf(0f)

            displayedKeys.value = listOf(
                DisplayedKey(
                    topOldKey,
                    animationConfiguration.previousComposableTransition,
                    animationProgress
                ),
                DisplayedKey(
                    topNewKey,
                    animationConfiguration.newComposableTransition,
                    animationProgress
                )
            )

            val animationSpec = animationConfiguration.animationSpec.defineAnimationSpec(
                currentStateChange.stateChange
            )

            animatable.animateTo(1f, animationSpec) {
                animationProgress.value = value
            }

            displayedKeys.value = listOf(
                DisplayedKey(topNewKey, null, mutableStateOf(0f))
            )
            currentStateChange.completionCallback.stateChangeComplete()
        }
    }

    @Composable
    private fun CleanupStaleSavedStates(saveableStateHolder: SaveableStateHolder) {
        LaunchedEffect(currentStateChange) {
            val stateChange = currentStateChange?.stateChange ?: return@LaunchedEffect
            val previousKeys = stateChange.getPreviousKeys<Any>()
            val newKeys = stateChange.getNewKeys<Any>()
            previousKeys.fastForEach { previousKey ->
                if (!newKeys.contains(previousKey)) {
                    saveableStateHolder.removeState(previousKey)
                }
            }
        }
    }
}

private class SizeSavingMeasurePolicy : MeasurePolicy {
    var fullWidth by mutableStateOf(0)
    var fullHeight by mutableStateOf(0)

    override fun MeasureScope.measure(
        measurables: List<Measurable>,
        constraints: Constraints
    ): MeasureResult {
        val placeables = measurables.fastMap { it.measure(constraints) }
        val maxWidth = placeables.fastMaxBy { it.width }?.width ?: 0
        val maxHeight = placeables.fastMaxBy { it.height }?.height ?: 0

        if (fullWidth == 0 && maxWidth != 0) {
            fullWidth = maxWidth
        }

        if (fullHeight == 0 && maxHeight != 0) {
            fullHeight = maxHeight
        }

        return layout(maxWidth, maxHeight) {
            placeables.fastForEach { placeable ->
                placeable.place(0, 0)
            }
        }
    }

}

/**
 * Composition local to access the Backstack within screens.
 */
val LocalBackstack =
    staticCompositionLocalOf<Backstack> { throw IllegalStateException("You must ensure that the BackstackProvider provides the backstack, but it currently doesn't exist.") }

/**
 * Provider for the backstack composition local.
 */
@Composable
fun BackstackProvider(backstack: Backstack, content: @Composable() () -> Unit) {
    CompositionLocalProvider(LocalBackstack provides (backstack)) {
        content()
    }
}
