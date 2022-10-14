package com.udacity


sealed class ButtonState {
    object Downloading : ButtonState()
    object Complete : ButtonState()
}