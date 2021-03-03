package com.zhuinden.simplestackcomposedogexample.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Dog(
    val name: String,
    val age: Int,
    val determinedSex: DeterminedSex,
    val breed: String,
    val imageUrl: String,
) : Parcelable {
    enum class DeterminedSex {
        MALE,
        FEMALE;
    }
}