package com.zhuinden.simplestack.navigator

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.LocalSaveableStateRegistry
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
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
import com.zhuinden.simplestackcomposeintegration.util.historyAsState
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
 * Created backstack will automatically intercept all back button presses when necessary.
 *
 * Note that backstack created with this method does NOT support [ScopedServices.HandlesBack].
 * Use fragment-based Navigator if you want this functionality.
 * See https://github.com/Zhuinden/simple-stack/issues/259 for more info.
 */
@Composable
fun rememberBackstack(stateChanger: StateChanger, init: ComposeNavigatorInitializer.() -> Backstack): Backstack {
    val viewModel = viewModel<BackstackHolderViewModel>()
    if (!viewModel.isInitialized) {
        init(viewModel)
    }

    SaveBackstackState(viewModel)
    ListenToLifecycleEvents(viewModel)
    BackHandler(viewModel)

    remember(stateChanger) {
        // Attach state changer after init call to defer first navigation. That way,
        // caller can use backstack to init their own things with Backstack instance
        // before navigation is performed.
        viewModel.backstack.setStateChanger(stateChanger)
        true
    }

    return viewModel.backstack
}

@Composable
private fun BackHandler(viewModel: BackstackHolderViewModel) {
    val history by viewModel.backstack.historyAsState()

    BackHandler(enabled = history.size > 1) {
        viewModel.backstack.goBack()
    }
}

@Composable
private fun SaveBackstackState(viewModel: BackstackHolderViewModel) {
    val stateSavingRegistry = LocalSaveableStateRegistry.current

    remember(viewModel) {
        if (stateSavingRegistry == null) {
            return@remember true
        }

        val oldState = stateSavingRegistry.consumeRestored(STATE_SAVING_KEY) as StateBundle?
        oldState?.let {
            viewModel.backstack.fromBundle(it)
        }

        stateSavingRegistry.registerProvider(STATE_SAVING_KEY) {
            viewModel.backstack.toBundle()
        }
    }
}

@Composable
private fun ListenToLifecycleEvents(viewModel: BackstackHolderViewModel) {
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    DisposableEffect(lifecycle) {
        val lifecycleListener = LifecycleEventObserver { _, event ->
            val isResumed = event.targetState.isAtLeast(Lifecycle.State.RESUMED)
            val isStateChangerAlreadyAttached = viewModel.backstack.hasStateChanger()
            if (isResumed != isStateChangerAlreadyAttached) {
                if (isResumed) {
                    viewModel.backstack.reattachStateChanger()
                } else {
                    viewModel.backstack.detachStateChanger()
                }
            }
        }

        lifecycle.addObserver(lifecycleListener)

        onDispose {
            lifecycle.removeObserver(lifecycleListener)
            viewModel.backstack.executePendingStateChange()
            viewModel.backstack.setStateChanger(null)
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
    ): Backstack
}

private const val STATE_SAVING_KEY = "BackstackState"
