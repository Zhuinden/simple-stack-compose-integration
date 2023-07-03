package com.zhuinden.simplestackcomposeintegration.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner

internal class StoreHolderViewModel : ViewModel() {
   private val viewModelStores = HashMap<Any, ViewModelStoreOwner>()

   fun removeKey(key: Any) {
      viewModelStores.remove(key)?.viewModelStore?.clear()
   }

   @Composable
   fun WithLocalViewModelStore(key: Any, block: @Composable () -> Unit) {
      val storeOwner = viewModelStores.getOrPut(key) {
         val store = ViewModelStore()

         object: ViewModelStoreOwner {
            override val viewModelStore: ViewModelStore
               get() = store

         }
      }

      CompositionLocalProvider(LocalViewModelStoreOwner provides storeOwner) {
         block()
      }
   }

   override fun onCleared() {
      for (store in viewModelStores.values) {
         store.viewModelStore.clear()
      }
   }
}
