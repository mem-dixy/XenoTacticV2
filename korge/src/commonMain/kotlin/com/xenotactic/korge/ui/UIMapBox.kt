package com.xenotactic.korge.ui

import korlibs.logger.Logger
import korlibs.korge.view.Container
import korlibs.korge.view.addTo
import korlibs.korge.view.align.centerOn
import korlibs.korge.view.container
import korlibs.korge.view.image
import korlibs.korge.view.renderToBitmap
import korlibs.korge.view.solidRect
import korlibs.image.color.Colors
import korlibs.io.async.launch
import com.xenotactic.gamelogic.globals.PATH_LINES_RATIO
import com.xenotactic.gamelogic.model.GameMap
import com.xenotactic.gamelogic.utils.toScale
import com.xenotactic.korge.scenes.VIEWS_INSTANCE
import kotlinx.coroutines.GlobalScope
import pathing.PathFinder
import kotlin.math.min

inline fun Container.uiMapBox(
    gameMap: GameMap, boxWidth: Double, boxHeight: Double,
    gameMapGridSize: Double = 25.0
): UIMapBox =
    UIMapBox(gameMap, boxWidth, boxHeight, gameMapGridSize).addTo(this)

/**
 * A UI element where a game map is drawn inside of a box.
 */
class UIMapBox(
    gameMap: GameMap,
    val boxWidth: Double, val boxHeight: Double,
    val gameMapGridSize: Float = 25f,
    val paddingTopAndBottom: Float = 5f,
    val paddingLeftAndRight: Float = 5f,
    calculateMapPath: Boolean = false
) : Container() {

    val mapSection = this.solidRect(boxWidth, boxHeight, Colors.BLACK.withAd(0.4))
    val mapContainer: Container = this.container()

    init {


//        mapContainer.scaledWidth = maxMapWidth
//        mapContainer.scaledHeight = maxMapWidth
//        mapContainer.scale = mapScale
//        mapContainer.centerOn(mapSection)

        updateMap(gameMap, calculateMapPath)



        //        launch(Dispatchers.Default) {
        //            val asBitMap = mapContainer.renderToBitmap(scenes.VIEWS_INSTANCE)
        //            mapContainer.removeChildren()
        //            val mapImage = mapContainer.image(asBitMap)
        //        }

    }

    fun updateMap(gameMap: GameMap, calculateMapPath: Boolean) {
        val currentMapContainerHeight = gameMap.height.value * gameMapGridSize
        val currentMapContainerWidth = gameMap.width.value * gameMapGridSize

        val maxMapHeight = boxHeight - paddingTopAndBottom * 2
        val maxMapWidth = boxWidth - paddingLeftAndRight * 2

        val scaledByHeight = maxMapHeight / currentMapContainerHeight
        val scaledByWidth = maxMapWidth / currentMapContainerWidth

        val mapScale = min(scaledByHeight, scaledByWidth)

        mapContainer.scale = mapScale.toScale()
        mapContainer.removeChildren()
        mapContainer.apply {
            UIMap(
                gameMap,
                shortestPath = if (calculateMapPath) PathFinder.getShortestPath(gameMap) else
                    null,
                uiMapSettings = UIMapSettings(
                    drawGridNumbers = false,
                    gridSize = gameMapGridSize,
                    pathLinesRatio = (0.5 / mapScale) * PATH_LINES_RATIO,
                    boardType = BoardType.SOLID
                )
            )
        }
        mapContainer.centerOn(mapSection)

        GlobalScope.launch {
            val asBitMap = mapContainer.renderToBitmap(VIEWS_INSTANCE)
            mapContainer.removeChildren()
            val mapImage = mapContainer.image(asBitMap)
        }
    }

    companion object {
        val logger = Logger<UIMapBox>()
    }
}