/*
 * Copyright (C) 2021 Gabor Varadi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zhuinden.simplestackcomposeintegration.services

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.zhuinden.simplestack.ScopeKey
import com.zhuinden.simplestack.ScopeLookupMode
import com.zhuinden.simplestackcomposeintegration.core.LocalBackstack

/**
 * Helper function to remember a service looked up from the backstack.
 */
@Composable
inline fun <reified T> rememberService(serviceTag: String = T::class.java.name): T {
    val backstack = LocalBackstack.current

    return remember { backstack.lookupService(serviceTag) }
}

/**
 * Helper function to remember a service looked up from the backstack from a specific scope with the given scope lookup mode.
 */
@Composable
inline fun <reified T> rememberServiceFrom(scopeTag: String, serviceTag: String = T::class.java.name, scopeLookupMode: ScopeLookupMode = ScopeLookupMode.ALL): T {
    val backstack = LocalBackstack.current

    return remember { backstack.lookupFromScope(scopeTag, serviceTag, scopeLookupMode) }
}

/**
 * Helper function to remember a service looked up from the backstack from a specific scope with the given scope lookup mode.
 */
@Composable
inline fun <reified T> rememberServiceFrom(scopeKey: ScopeKey, serviceTag: String = T::class.java.name, scopeLookupMode: ScopeLookupMode = ScopeLookupMode.ALL): T = rememberServiceFrom(
    scopeTag = scopeKey.scopeTag,
    serviceTag = serviceTag,
    scopeLookupMode = scopeLookupMode,
)