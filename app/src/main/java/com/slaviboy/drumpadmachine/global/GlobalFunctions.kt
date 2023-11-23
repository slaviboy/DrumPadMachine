package com.slaviboy.drumpadmachine.global

fun anyNotNull(vararg any: Any?): Boolean {
    return any.any { it != null }
}

fun allNotNull(vararg any: Any?): Boolean {
    return any.all { it != null }
}

fun anyTrue(vararg boolean: Boolean): Boolean {
    return boolean.any { it }
}

fun allTrue(vararg boolean: Boolean): Boolean {
    return boolean.all { it }
}