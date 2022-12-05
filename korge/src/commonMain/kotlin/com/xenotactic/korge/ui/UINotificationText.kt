package com.xenotactic.korge.ui

import com.soywiz.korge.view.*
import com.soywiz.korim.color.Colors
import com.soywiz.korio.async.launchImmediately
import com.xenotactic.korge.engine.Engine
import com.xenotactic.korge.input_processors.PlaceEntityErrorEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay

data class NotificationTextUpdateEvent(
    val text: String
)

class UINotificationText(
    val engine: Engine,
    text: String = "N/A"
) : Container() {
    private val NOTIFICATION_TEXT_SIZE = 20.0
    private val notificationText = text(text, textSize = NOTIFICATION_TEXT_SIZE) {
    }
    private val errorText = text("Unable to place entity!", textSize = NOTIFICATION_TEXT_SIZE, color = Colors.RED) {
        alignTopToBottomOf(notificationText)
        visible = false
    }
    private val DEFAULT_DISPLAY_TIME_MILLIS = 5000L
    private val DISPLAY_TIME_UNTIL_FADE_MILLIS = 2000L
    private var errorTextDisplayTimeMillis = DEFAULT_DISPLAY_TIME_MILLIS
    private val TICK_RATE_MILLIS = 100L

    init {
        resize()
        launchImmediately(Dispatchers.Default) {
            while (true) {
                errorTextDisplayTimeMillis = maxOf(errorTextDisplayTimeMillis - TICK_RATE_MILLIS, 0)
                errorText.alpha =
                    errorTextDisplayTimeMillis.toDouble() / (DEFAULT_DISPLAY_TIME_MILLIS - DISPLAY_TIME_UNTIL_FADE_MILLIS)
                if (errorTextDisplayTimeMillis <= 0) {
                    errorText.visible = false
                }
                delay(TICK_RATE_MILLIS)
            }
        }
        engine.eventBus.register<NotificationTextUpdateEvent> {
            this.notificationText.text = it.text
            resize()
        }
        engine.eventBus.register<PlaceEntityErrorEvent> {
            notifyErrorText(it.errorMsg)
            resize()
        }
    }

    private fun resize() {
        notificationText.centerXOnStage()
        errorText.centerXOnStage()
    }

    private fun notifyErrorText(s: String) {
        errorText.text = s
        errorTextDisplayTimeMillis = DEFAULT_DISPLAY_TIME_MILLIS
        errorText.visible = true
    }
}