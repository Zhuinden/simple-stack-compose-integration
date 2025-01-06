package com.zhuinden.simplestackcomposeintegration.core

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.LocalSaveableStateRegistry
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zhuinden.simplestack.AheadOfTimeWillHandleBackChangedListener
import com.zhuinden.simplestack.BackHandlingModel
import com.zhuinden.simplestack.Backstack
import com.zhuinden.simplestack.Backstack.StateClearStrategy
import com.zhuinden.simplestack.DefaultKeyFilter
import com.zhuinden.simplestack.DefaultKeyParceler
import com.zhuinden.simplestack.DefaultStateClearStrategy
import com.zhuinden.simplestack.GlobalServices
import com.zhuinden.simplestack.KeyFilter
import com.zhuinden.simplestack.KeyParceler
import com.zhuinden.simplestack.ScopedServices
import com.zhuinden.simplestack.StateChanger
import com.zhuinden.statebundle.StateBundle

/**
 * Create a [Backstack] for navigation and remember it across state changes and process kills.
 *
 * [stateChanger] wil not be remembered and will be re-initialized every time this composable
 * exits scope. It MUST be remembered by the caller.
 *
 * [init] argument will only be called once (or after process kill). In that lambda, you have to
 * call [ComposeNavigatorInitializer.createBackstack] and return provided value.
 * Backstack will not perform any navigation until you return from that lambda,
 * so you can initialize your own services that require a [Backstack] instance, before you return.
 *
 * optional [id] argument allows you to have multiple backstacks inside single screen. To do that,
 * you have to provide unique ID to every distinct [rememberBackstack] call.
 *
 * Created backstack will automatically intercept all back button presses when necessary, if
 * [interceptBackButton] flag is enabled. Otherwise it is up to the caller to manually call
 * [Backstack.goBack].
 *
 * Note that backstack created with this method
 * uses [BackHandlingModel.AHEAD_OF_TIME] back handling model.
 */
@Composable
fun rememberBackstack(
    stateChanger: StateChanger,
    id: String = "DEFAULT_SINGLE_COMPOSE_STACK_IDENTIFIER",
    interceptBackButton: Boolean = true,
    init: ComposeNavigatorInitializer.() -> Backstack,
): Backstack {
    val viewModel = viewModel<BackstackHolderViewModel>()
    val backstack = viewModel.getBackstack(id) ?: init(viewModel.createInitializer(id))

    SaveBackstackState(backstack, id)
    ListenToLifecycleEvents(backstack)

    if (interceptBackButton) {
        BackHandler(backstack)
    }

    remember(backstack, stateChanger, id) {
        // Attach state changer after init call to defer first navigation. That way,
        // caller can use backstack to init their own things with Backstack instance
        // before navigation is performed.
        backstack.setStateChanger(stateChanger)
        true
    }

    return backstack
}

@Composable
private fun BackHandler(backstack: Backstack) {
    var backButtonEnabled by remember { mutableStateOf(backstack.willHandleAheadOfTimeBack()) }

    DisposableEffect(backstack) {
        val listener = AheadOfTimeWillHandleBackChangedListener {
            backButtonEnabled = it
        }


        backButtonEnabled = backstack.willHandleAheadOfTimeBack()
        backstack.addAheadOfTimeWillHandleBackChangedListener(listener)

        onDispose {
            backstack.removeAheadOfTimeWillHandleBackChangedListener(listener)
        }
    }

    BackHandler(enabled = backButtonEnabled) {
        backstack.goBack()
    }
}

@Composable
private fun SaveBackstackState(backstack: Backstack, id: String) {
    val stateSavingRegistry = LocalSaveableStateRegistry.current

    remember(backstack, id, stateSavingRegistry) {
        if (stateSavingRegistry == null) {
            return@remember true
        }

        val stateId = "$STATE_SAVING_KEY[$id]"
        val oldState = stateSavingRegistry.consumeRestored(stateId) as StateBundle?
        oldState?.let {
            backstack.fromBundle(it)
        }

        stateSavingRegistry.registerProvider(stateId) {
            backstack.toBundle()
        }
    }
}

@Composable
private fun ListenToLifecycleEvents(backstack: Backstack) {
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    DisposableEffect(lifecycle) {
        val lifecycleListener = LifecycleEventObserver { _, event ->
            val isResumed = event.targetState.isAtLeast(Lifecycle.State.RESUMED)
            val isStateChangerAlreadyAttached = backstack.hasStateChanger()
            if (isResumed != isStateChangerAlreadyAttached) {
                if (isResumed) {
                    backstack.reattachStateChanger()
                } else {
                    backstack.detachStateChanger()
                }
            }
        }

        lifecycle.addObserver(lifecycleListener)

        onDispose {
            lifecycle.removeObserver(lifecycleListener)
            backstack.executePendingStateChange()
            backstack.setStateChanger(null)
        }
    }
}

interface ComposeNavigatorInitializer {
    fun createBackstack(
        initialKeys: List<*>,
        keyFilter: KeyFilter = DefaultKeyFilter(),
        keyParceler: KeyParceler = DefaultKeyParceler(),
        stateClearStrategy: StateClearStrategy = DefaultStateClearStrategy(),
        scopedServices: ScopedServices? = null,
        globalServices: GlobalServices? = null,
        globalServicesFactory: GlobalServices.Factory? = null,
        parentServices: Backstack? = null,
        parentScopeTag: String? = null,
    ): Backstack
}

private const val STATE_SAVING_KEY = "BackstackState"
