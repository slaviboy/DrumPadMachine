package com.slaviboy.drumpadmachine.events

import com.slaviboy.drumpadmachine.data.entities.Preset

sealed class NavigationEvent {
    data class NavigateToDrumPadScreen(val preset: Preset) : NavigationEvent()
}