package com.zhuinden.simplestackcomposeintegration.services

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.zhuinden.simplestackcomposeintegration.core.LocalBackstack

/**
 * Helper function to remember a service looked up from the backstack.
 */
@Composable
inline fun <reified T> rememberService(serviceTag: String = T::class.java.name): T {
    val backstack = LocalBackstack.current

    return remember { backstack.lookupService(serviceTag) }
}
