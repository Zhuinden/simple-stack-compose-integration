package com.zhuinden.simplestackcomposedogexample.app

import android.app.Application
import com.zhuinden.simplestack.GlobalServices
import com.zhuinden.simplestackcomposedogexample.data.datasource.DogDataSource
import com.zhuinden.simplestackextensions.servicesktx.add
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlin.random.Random

class CustomApplication : Application() {
    lateinit var globalServices: GlobalServices
        private set

    override fun onCreate() {
        super.onCreate()

        val random = Random.Default

        val mainThreadScheduler = AndroidSchedulers.mainThread()

        globalServices = GlobalServices.builder()
            .add(DogDataSource(random, mainThreadScheduler))
            .build()
    }
}