package dev.ciox.ratiba.components.extensions.android

import android.content.Intent
import android.os.Parcelable

fun <T : Parcelable> Intent.putExtra(key: String, items: List<T>) {
    putParcelableArrayListExtra(key, items.toArrayList())
}

fun <T : Parcelable> Intent.getParcelableListExtra(key: String): List<T>? {
    return getParcelableArrayListExtra<T>(key)?.toList()
}