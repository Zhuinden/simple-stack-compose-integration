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
import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.TweenSpec
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.util.fastForEach
import androidx.compose.ui.util.fastMap
import androidx.compose.ui.util.fastMaxBy
import com.zhuinden.simplestack.AsyncStateChanger
import com.zhuinden.simplestack.Backstack
import com.zhuinden.simplestack.StateChange
import com.zhuinden.simplestack.StateChanger
import com.zhuinden.simplestackcomposeintegration.core.ComposeStateChanger.AnimationConfiguration.ComposableAnimationSpec
import com.zhuinden.simplestackcomposeintegration.core.ComposeStateChanger.AnimationConfiguration.ComposableTransition
import kotlinx.coroutines.launch

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
    private var backstackState by mutableStateOf(BackstackState(animationConfiguration = animationConfiguration))

    override fun onNavigationEvent(
        stateChange: StateChange,
        completionCallback: StateChanger.Callback
    ) {
        this.backstackState =
            BackstackState(
                animationConfiguration = animationConfiguration,
                stateChange = stateChange,
                callback = completionCallback,
            )
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
                        StateChange.FORWARD -> Modifier.graphicsLayer(translationX = 0 + (-1) * fullWidth * animationProgress)
                        StateChange.BACKWARD -> Modifier.graphicsLayer(translationX = 0 + fullWidth * animationProgress)
                        else /* REPLACE */ -> Modifier.graphicsLayer(alpha = (1 - animationProgress))
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
                        StateChange.FORWARD -> Modifier.graphicsLayer(translationX = fullWidth + (-1) * fullWidth * animationProgress)
                        StateChange.BACKWARD -> Modifier.graphicsLayer(translationX = -1 * fullWidth + fullWidth * animationProgress)
                        else /* REPLACE */ -> Modifier.graphicsLayer(alpha = 0 + animationProgress)
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
        val contentWrapper: ComposableContentWrapper = object: ComposableContentWrapper {
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
            fun animateComposable(modifier: Modifier, stateChange: StateChange, fullWidth: Int, fullHeight: Int, animationProgress: Float): Modifier
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

    private data class BackstackState(
        private val animationConfiguration: AnimationConfiguration,
        private val stateChange: StateChange? = null,
        private val callback: StateChanger.Callback? = null,
    ) {
        @Composable
        fun RenderScreen(modifier: Modifier = Modifier) {
            val stateChange = stateChange ?: return
            val callback = callback ?: return

            val saveableStateHolder = rememberSaveableStateHolder()

            var completionCallback by remember { mutableStateOf<StateChanger.Callback?>(null) }

            val topNewKey by rememberUpdatedState(newValue = stateChange.topNewKey<DefaultComposeKey>())
            val topPreviousKey by rememberUpdatedState(newValue = stateChange.topPreviousKey<DefaultComposeKey>())

            var isAnimating by remember { mutableStateOf(false) }

            val lerping = remember { Animatable(0.0f) }

            var animationProgress by remember { mutableStateOf(0.0f) }

            var initialization by remember { mutableStateOf(true) }

            if (completionCallback !== callback) {
                completionCallback = callback

                if (topPreviousKey != null) {
                    initialization = false

                    animationProgress = 0.0f
                    isAnimating = true
                } else {
                    initialization = true
                }
            }

            var fullWidth by remember { mutableStateOf(0) }
            var fullHeight by remember { mutableStateOf(0) }

            val measurePolicy = MeasurePolicy { measurables, constraints ->
                val placeables = measurables.fastMap { it.measure(constraints) }
                val maxWidth = placeables.fastMaxBy { it.width }?.width ?: 0
                val maxHeight = placeables.fastMaxBy { it.height }?.height ?: 0

                if (fullWidth == 0 && maxWidth != 0) {
                    fullWidth = maxWidth
                }

                if (fullHeight == 0 && maxHeight != 0) {
                    fullHeight = maxHeight
                }

                layout(maxWidth, maxHeight) {
                    placeables.fastForEach { placeable ->
                        placeable.place(0, 0)
                    }
                }
            }

            val previousTransition = animationConfiguration.previousComposableTransition
            val newTransition = animationConfiguration.newComposableTransition
            
            val contentWrapper = animationConfiguration.contentWrapper

            var initialNewKey by remember { mutableStateOf(topNewKey) }

            val newKeys by rememberUpdatedState(newValue = stateChange.getNewKeys<DefaultComposeKey>())
            val previousKeys by rememberUpdatedState(newValue = stateChange.getPreviousKeys<DefaultComposeKey>())

            val allKeys by rememberUpdatedState(newValue = mutableListOf<DefaultComposeKey>().apply {
                addAll(newKeys)

                previousKeys.fastForEach { previousKey ->
                    if (!newKeys.contains(previousKey)) {
                        add(0, previousKey)
                    }
                }
            }.toList())

            Layout(
                content = {
                    allKeys.fastForEach { key ->
                        key(key) {
                            contentWrapper.ContentWrapper(stateChange = stateChange) {
                                if (key == topNewKey || (isAnimating && key == initialNewKey)) {
                                    saveableStateHolder.SaveableStateProvider(key = key.saveableStateProviderKey) {
                                        Box(
                                            modifier = when {
                                                !isAnimating || initialization -> modifier
                                                else -> when {
                                                    key == topNewKey -> newTransition.animateComposable(modifier, stateChange, fullWidth, fullHeight, animationProgress)
                                                    else -> previousTransition.animateComposable(modifier, stateChange, fullWidth, fullHeight, animationProgress)
                                                }
                                            }
                                        ) {
                                            key.RenderComposable(modifier)
                                        }
                                    }
                                }
                            }
                        }
                    }
                },
                measurePolicy = measurePolicy,
            )

            val coroutineScope = rememberCoroutineScope()

            DisposableEffect(key1 = completionCallback, effect = {
                @Suppress("NAME_SHADOWING")
                val topNewKey = topNewKey // ensure this is kept while the animation is progressing, I guess?

                @Suppress("NAME_SHADOWING")
                val completionCallback = completionCallback  // ensure this is kept while the animation is progressing, I guess?

                val job = coroutineScope.launch {
                    if (isAnimating) {
                        lerping.animateTo(1.0f, animationConfiguration.animationSpec.defineAnimationSpec(stateChange = stateChange)) {
                            animationProgress = this.value
                        }
                        isAnimating = false
                        lerping.snapTo(0f)
                    }
                    initialNewKey = topNewKey

                    previousKeys.fastForEach { previousKey ->
                        if (!newKeys.contains(previousKey)) {
                            saveableStateHolder.removeState(previousKey.saveableStateProviderKey)
                        }
                    }

                    try {
                        completionCallback!!.stateChangeComplete()
                    } catch(e: IllegalStateException) {
                        Log.i("ComposeStateChanger", "Unexpected double call to completion callback", e)
                    }
                }

                onDispose {
                    try {
                        if (!job.isCompleted) {
                            job.cancel()
                        }
                    } catch (e: Throwable) {
                        // I don't think this can happen, but even if it did, it wouldn't be useful here.
                        // Having to cancel the job would only happen if the animation is in progress while the composable is removed.
                    }
                }
            })
        }
    }

    @Composable
    fun RenderScreen(modifier: Modifier = Modifier) {
        LocalBackstack.current // force `BackstackProvider` to be set

        backstackState.RenderScreen(modifier)
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