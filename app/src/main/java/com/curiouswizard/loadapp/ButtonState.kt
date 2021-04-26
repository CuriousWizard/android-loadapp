package com.curiouswizard.loadapp

/**
 * Represents different states of our custom buttom view
 */
sealed class ButtonState {
    object Clicked : ButtonState()
    object Loading : ButtonState()
    object Completed : ButtonState()
}