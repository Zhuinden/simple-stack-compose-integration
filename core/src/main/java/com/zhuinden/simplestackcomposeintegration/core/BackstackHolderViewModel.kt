package com.zhuinden.simplestackcomposeintegration.core

import androidx.lifecycle.ViewModel
import com.zhuinden.simplestack.*

internal class BackstackHolderViewModel : ViewModel() {
    private val backstacks = HashMap<String, Backstack>()

    fun getBackstack(id: String): Backstack? {
        return backstacks[id]
    }

    fun createInitializer(id: String) = object : ComposeNavigatorInitializer {
        override fun createBackstack(
            initialKeys: List<*>,
            keyFilter: KeyFilter,
            keyParceler: KeyParceler,
            stateClearStrategy: Backstack.StateClearStrategy,
            scopedServices: ScopedServices?,
            globalServices: GlobalServices?,
            globalServicesFactory: GlobalServices.Factory?,
        ): Backstack {
            val backstack = Backstack()

            backstack.setBackHandlingModel(BackHandlingModel.AHEAD_OF_TIME)
            backstack.setKeyFilter(keyFilter)
            backstack.setKeyParceler(keyParceler)
            backstack.setStateClearStrategy(stateClearStrategy)
            scopedServices?.let { backstack.setScopedServices(it) }
            globalServices?.let { backstack.setGlobalServices(it) }
            globalServicesFactory?.let { backstack.setGlobalServices(it) }

            backstack.setup(initialKeys)
            backstacks[id] = backstack

            return backstack
        }
    }

    override fun onCleared() {
        for (backstack in backstacks.values)
            backstack.finalizeScopes()
    }
}
