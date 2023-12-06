package com.slaviboy.drumpadmachine.extensions

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp

@Composable
fun Float.pxToDp() = with(LocalDensity.current) { this@pxToDp.toDp() }

fun Float.mapValue(fromY: Float, toY: Float): Float {
    return (toY - fromY) * this + fromY
}

fun Float.mapValue(fromY: Dp, toY: Dp): Dp {
    return (toY - fromY) * this + fromY
}

fun Float.accelerateValue(accelerationFactor: Float = 0.1f): Float {
    val acceleratedValue = this - accelerationFactor * this
    return acceleratedValue.coerceIn(0.0f, 1.0f)
}

fun Float.decelerateValue(decelerationFactor: Float = 0.7f): Float {
    val deceleratedValue = this + (1 - this) * (1 - decelerationFactor)
    return deceleratedValue.coerceIn(0.0f, 1.0f)
}