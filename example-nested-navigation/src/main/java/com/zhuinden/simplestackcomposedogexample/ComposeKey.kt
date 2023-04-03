package com.zhuinden.simplestackcomposesimpleexample

import android.os.Parcelable
import com.zhuinden.simplestack.ServiceBinder
import com.zhuinden.simplestackcomposeintegration.core.DefaultComposeKey
import com.zhuinden.simplestackextensions.services.DefaultServiceProvider

abstract class ComposeKey: DefaultComposeKey(), Parcelable, DefaultServiceProvider.HasServices {
    override val saveableStateProviderKey: Any = this

    override fun getScopeTag(): String = javaClass.name

    override fun bindServices(serviceBinder: ServiceBinder) {
    }
}