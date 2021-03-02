package com.zhuinden.firstcomposeapp

import android.os.Parcelable
import com.zhuinden.simplestack.ServiceBinder
import com.zhuinden.simplestackcomposeintegration.core.DefaultComposeKey
import com.zhuinden.simplestackextensions.services.DefaultServiceProvider

abstract class ComposeKey: DefaultComposeKey(), Parcelable, DefaultServiceProvider.HasServices {
    override fun getScopeTag(): String = javaClass.name

    override fun bindServices(serviceBinder: ServiceBinder) {
    }
}