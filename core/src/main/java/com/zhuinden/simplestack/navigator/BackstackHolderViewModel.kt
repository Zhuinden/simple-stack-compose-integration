package com.zhuinden.simplestack.navigator

import androidx.lifecycle.ViewModel
import com.zhuinden.simplestack.Backstack
import com.zhuinden.simplestack.GlobalServices
import com.zhuinden.simplestack.KeyFilter
import com.zhuinden.simplestack.KeyParceler
import com.zhuinden.simplestack.ScopedServices

internal class BackstackHolderViewModel: ViewModel(), ComposeNavigatorInitializer {
    lateinit var backstack: Backstack

    var isInitialized: Boolean = false

    override fun createBackstack(
        initialKeys: List<*>,
        keyFilter: KeyFilter,
        keyParceler: KeyParceler,
        stateClearStrategy: Backstack.StateClearStrategy,
        scopedServices: ScopedServices?,
        globalServices: GlobalServices?,
        globalServicesFactory: GlobalServices.Factory?,
    ): Backstack {
        backstack = Backstack()

        backstack.setKeyFilter(keyFilter)
        backstack.setKeyParceler(keyParceler)
        backstack.setStateClearStrategy(stateClearStrategy)
        scopedServices?.let { backstack.setScopedServices(it) }
        globalServices?.let { backstack.setGlobalServices(it) }
        globalServicesFactory?.let { backstack.setGlobalServices(it) }

        backstack.setup(initialKeys)

        isInitialized = true
        return backstack
    }

    override fun onCleared() {
        if (isInitialized) {
            backstack.finalizeScopes()
        }
    }
}
