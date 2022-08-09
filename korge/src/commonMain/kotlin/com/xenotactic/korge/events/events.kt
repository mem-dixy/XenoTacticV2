package com.xenotactic.korge.events

import MapVerificationResult
import com.soywiz.korge.view.View
import com.soywiz.korio.file.VfsFile
import com.xenotactic.gamelogic.model.GameMap
import com.xenotactic.gamelogic.model.MapEntity
import com.xenotactic.korge.components.GoalData

object EscapeButtonActionEvent

data class UpdatedPathLengthEvent(val newPathLength: Double?)

data class RemovedEntityEvent(val entity: MapEntity)

data class AddEntityEvent(val entity: MapEntity)

data class UpdatedGoalDataEvent(val data: GoalData)

object LeftControlAndMinus

object LeftControlAndEqual

object SpawnCreepEvent

data class PlayMapEvent(val gameMap: GameMap)

object ExitGameSceneEvent

object PointerActionChangeEvent

data class GoldensEntryClickEvent(
    val gameMap: GameMap
)

data class GoldensEntryHoverOnEvent(
    val mapFile: VfsFile,
    val gameMap: GameMap,
    val verificationResult: MapVerificationResult
)

object GoldensEntryHoverOutEvent

data class UIEntityClickedEvent(
    val view: View,
    val entity: MapEntity
)