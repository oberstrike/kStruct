package com.maju.utils

import org.jetbrains.kotlin.util.collectionUtils.concat
import java.util.*

fun String.firstCharToLower(): String = replaceFirstChar { it.lowercase(Locale.getDefault()) }

fun <T> Collection<T>.concatAll(vararg collections: Collection<T>): Collection<T>? {
    return collections.reduceOrNull { acc, collection -> acc.concat(collection) ?: listOf() }
}