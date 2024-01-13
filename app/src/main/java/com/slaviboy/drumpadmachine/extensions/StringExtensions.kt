package com.slaviboy.drumpadmachine.extensions

fun String.containsString(
    text: String
): Boolean {
    return this.contains(text, true) || this.split(" ", "-").any {
        it.contains(text, true)
    }
}