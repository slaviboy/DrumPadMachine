package com.slaviboy.drumpadmachine.events

sealed class NavigationEvent {
    data class NavigateToDrumPadScreen(val presetId: Int) : NavigationEvent()
}