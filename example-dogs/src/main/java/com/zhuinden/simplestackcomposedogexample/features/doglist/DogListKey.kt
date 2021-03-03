package com.zhuinden.simplestackcomposedogexample.features.doglist

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.rxjava2.subscribeAsState
import androidx.compose.ui.Modifier
import com.zhuinden.simplestack.ServiceBinder
import com.zhuinden.simplestackcomposedogexample.core.navigation.ComposeKey
import com.zhuinden.simplestackcomposedogexample.data.datasource.DogDataSource
import com.zhuinden.simplestackcomposedogexample.utils.OptionalWrapper
import com.zhuinden.simplestackcomposeintegration.services.rememberService
import com.zhuinden.simplestackextensions.servicesktx.add
import com.zhuinden.simplestackextensions.servicesktx.lookup
import kotlinx.parcelize.Parcelize

@Immutable
@Parcelize
class DogListKey : ComposeKey() {
    @Suppress("RemoveExplicitTypeArguments")
    override fun bindServices(serviceBinder: ServiceBinder) {
        super.bindServices(serviceBinder)
        with(serviceBinder) {
            add(DogListViewModel(lookup<DogDataSource>()))
        }
    }

    @Composable
    override fun ScreenComposable(modifier: Modifier) {
        val viewModel = rememberService<DogListViewModel>()

        val dogs = viewModel.dogList.subscribeAsState(OptionalWrapper.absent())
        val lazyListState = viewModel.lazyListState

        DogListScreen(dogs.value.value, lazyListState)
    }
}