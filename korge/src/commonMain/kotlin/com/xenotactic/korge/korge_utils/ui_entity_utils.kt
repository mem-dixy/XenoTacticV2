package com.xenotactic.korge.korge_utils

import com.soywiz.korge.view.Text
import com.soywiz.korim.text.TextAlignment
import com.xenotactic.ecs.EntityId
import com.xenotactic.ecs.World
import com.xenotactic.gamelogic.globals.ENTITY_LABEL_SIZE
import com.xenotactic.gamelogic.model.MapEntityData
import com.xenotactic.gamelogic.model.MapEntityType
import com.xenotactic.korge.components.EntityCheckpointComponent
import com.xenotactic.korge.components.EntitySpeedAreaComponent
import com.xenotactic.korge.components.EntityTeleportInComponent
import com.xenotactic.korge.components.EntityTeleportOutComponent
import com.xenotactic.korge.ui.ENTITY_TEXT_FONT

fun makeEntityLabelText(text: String): Text {
    return Text(text, textSize = ENTITY_LABEL_SIZE,
    font = ENTITY_TEXT_FONT)
}

fun MapEntityType.getText(entityId: EntityId, world: World): String? {
    return when(this) {
        MapEntityType.START -> "START"
        MapEntityType.FINISH -> "FINISH"
        MapEntityType.CHECKPOINT -> {
            val entityCheckpointComponent = world[entityId, EntityCheckpointComponent::class]
            "CP ${entityCheckpointComponent.ordinalSequenceNumber}"
        }
        MapEntityType.ROCK -> null
        MapEntityType.TOWER -> null
        MapEntityType.TELEPORT_IN -> {
            val entityTeleportInComponent = world[entityId, EntityTeleportInComponent::class]
            "TP ${entityTeleportInComponent.ordinalSequenceNumber} IN"}
        MapEntityType.TELEPORT_OUT -> {
            val entityTeleportOutComponent = world[entityId, EntityTeleportOutComponent::class]
            "TP ${entityTeleportOutComponent.ordinalSequenceNumber} OUT"
        }
        MapEntityType.SMALL_BLOCKER -> null
        MapEntityType.SPEED_AREA -> {
            val entitySpeedAreaComponent = world[entityId, EntitySpeedAreaComponent::class]
            entitySpeedAreaComponent.speedText
        }
        MapEntityType.MONSTER -> null
    }
//    return when(this) {
//        is MapEntityData.Checkpoint -> "CP ${ordinalSequenceNumber}"
//        MapEntityData.Finish -> "FINISH"
//        MapEntityData.Rock -> null
//        MapEntityData.SmallBlocker -> null
//        is MapEntityData.SpeedArea -> "${speedText}"
//        MapEntityData.Start -> "START"
//        is MapEntityData.TeleportIn -> "TP ${ordinalSequenceNumber} IN"
//        is MapEntityData.TeleportOut -> "TP ${ordinalSequenceNumber} OUT"
//        MapEntityData.Tower -> null
//        MapEntityData.Monster -> null
//    }
}
