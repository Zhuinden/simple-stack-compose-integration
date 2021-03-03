package com.zhuinden.simplestackcomposedogexample.core.models

import com.zhuinden.simplestackcomposedogexample.data.models.Dog

fun Dog.contentDescription(): String = run {
    val dog = this
    "${dog.name}, ${dog.determinedSex.name}, ${dog.breed}, ${dog.age} years old"
}