package com.slaviboy.drumpadmachine.events

sealed class ErrorEvent {
    data class ErrorWithMessage(val message: String) : ErrorEvent()
}