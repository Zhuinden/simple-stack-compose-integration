package com.zhuinden.simplestackcomposedogexample.features.doglist

import com.jakewharton.rxrelay2.BehaviorRelay
import com.zhuinden.simplestack.ScopedServices
import com.zhuinden.simplestackcomposedogexample.data.datasource.DogDataSource
import com.zhuinden.simplestackcomposedogexample.data.models.Dog
import com.zhuinden.simplestackcomposedogexample.utils.OptionalWrapper
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy

class DogListViewModel(
    private val dogDataSource: DogDataSource
) : ScopedServices.Registered {
    private val dogListRelay =
        BehaviorRelay.createDefault<OptionalWrapper<List<Dog>>>(OptionalWrapper.absent())
    val dogList: Observable<OptionalWrapper<List<Dog>>> = dogListRelay

    private val compositeDisposable = CompositeDisposable()

    override fun onServiceRegistered() {
        compositeDisposable += dogDataSource.getDogs().subscribeBy { dogs ->
            dogListRelay.accept(OptionalWrapper(dogs))
        }
    }

    override fun onServiceUnregistered() {
        compositeDisposable.clear()
    }
}