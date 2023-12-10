package com.slaviboy.drumpadmachine.extensions

fun String.containsString(
    text: String
): Boolean {
    return this.split(" ", "-").any {
        it.contains(text, true)
    }
}