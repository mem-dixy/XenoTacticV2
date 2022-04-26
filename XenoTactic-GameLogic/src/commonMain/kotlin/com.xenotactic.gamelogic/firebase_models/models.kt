package com.xenotactic.gamelogic.firebase_models

import com.xenotactic.gamelogic.mapid.MapToId
import com.xenotactic.gamelogic.model.GameMap
import com.xenotactic.gamelogic.model.MapEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber

@Serializable
data class FbGameMap(
    val width: Int,
    val height: Int,
    val start: MapEntity.Start? = null,
    val finish: MapEntity.Finish? = null,
    val checkpoints: List<MapEntity.CheckPoint> = emptyList(),
    val teleportIns: List<MapEntity.TeleportIn> = emptyList(),
    val teleportOuts: List<MapEntity.TeleportOut> = emptyList(),
    val towers: List<MapEntity.Tower> = emptyList(),
    val rocks: List<MapEntity.Rock> = emptyList(),
    val smallBlockers: List<MapEntity.SmallBlocker> = emptyList(),
    val speedAreas: List<MapEntity.SpeedArea> = emptyList(),
) {
//    fun toGameMap() {
//        return GameMap(
//            width,
//            height,
//            start,
//            finish,
//            checkpoints.toMutableList(),
//            tele
//        )
//    }
}

@Serializable
data class FbMapData(
    val data: Map<String, FbMapEntry>
)

@Serializable
data class FbMapEntry(
    val data: FbGameMap,
    val timestamp: Long
)