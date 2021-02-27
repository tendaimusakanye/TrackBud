package com.tendai.common.extensions

fun <T>MutableList<T>.isIndexPlayable(index: Int, queue: MutableList<T>): Boolean{
    return ( index >= 0 && index < queue.size)
}