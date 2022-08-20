package com.xenotactic.gamelogic.pathing

import com.xenotactic.gamelogic.containers.BlockingPointContainer
import com.xenotactic.gamelogic.model.IRectangleEntity
import com.xenotactic.gamelogic.model.TeleportPair

interface SearcherInterface {
    fun getUpdatablePath(
        mapWidth: Int,
        mapHeight: Int,
        pathingEntities: List<IRectangleEntity>,
        teleportPairs: List<TeleportPair> = emptyList(),
        blockingEntities: List<IRectangleEntity> = emptyList(),
        blockingPoints: BlockingPointContainer.View? = null
    ): GamePath?
}