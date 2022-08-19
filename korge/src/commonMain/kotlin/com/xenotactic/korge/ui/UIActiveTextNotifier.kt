package com.xenotactic.korge.ui

import com.soywiz.korge.view.Container
import com.soywiz.korge.view.Text
import com.soywiz.korge.view.addTo
import com.soywiz.korge.view.alpha
import com.soywiz.korge.view.text
import com.soywiz.korim.text.TextAlignment
import com.xenotactic.gamelogic.model.MapEntityType
import com.xenotactic.korge.ecomponents.ObjectPlacementEComponent
import com.xenotactic.korge.engine.Engine
import com.xenotactic.korge.events.EventBus
import com.xenotactic.korge.events.PointerActionChangeEvent
import com.xenotactic.korge.input_processors.PointerAction

inline fun Container.uiActiveTextNotifier(
    engine: Engine,
    eventBus: EventBus
): UIActiveTextNotifier =
    UIActiveTextNotifier(engine, eventBus).addTo(this)

class UIActiveTextNotifier(
    val engine: Engine,
    val eventBus: EventBus
) : Container() {
    val placementComponent = engine.injections.getSingleton<ObjectPlacementEComponent>()
    val activeButtonText: Text

    init {
        activeButtonText = text(
            "Hello world", alignment =
            TextAlignment.CENTER
        ).alpha(0.0)

        eventBus.register<PointerActionChangeEvent> {
            afterPointerActionChange()
        }
    }

    private fun afterPointerActionChange() {
        val pointerAction = placementComponent.pointerAction
        if (pointerAction == PointerAction.Inactive) {
            activeButtonText.alpha = 0.0
            return
        }

        activeButtonText.alpha = 1.0
        when (pointerAction) {
            PointerAction.Inactive -> TODO()
            is PointerAction.HighlightForPlacement -> {
                activeButtonText.text = "Placing entity: ${pointerAction.mapEntity.friendlyName}"
            }

            is PointerAction.RemoveEntityAtPlace -> {
                activeButtonText.text = when (pointerAction.entityType) {
                    MapEntityType.START -> TODO()
                    MapEntityType.FINISH -> TODO()
                    MapEntityType.CHECKPOINT -> TODO()
                    MapEntityType.ROCK -> "Removing rock"
                    MapEntityType.TOWER -> "Removing tower"
                    MapEntityType.TELEPORT_IN -> TODO()
                    MapEntityType.TELEPORT_OUT -> TODO()
                    MapEntityType.SMALL_BLOCKER -> TODO()
                    MapEntityType.SPEED_AREA -> TODO()
                }
            }
        }
    }
}