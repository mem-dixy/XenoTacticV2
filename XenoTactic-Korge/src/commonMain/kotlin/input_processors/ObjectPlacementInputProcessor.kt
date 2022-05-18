package input_processors

import com.soywiz.korev.MouseButton
import com.soywiz.korev.MouseEvent
import com.soywiz.korge.component.MouseComponent
import com.soywiz.korge.view.*
import com.xenotactic.gamelogic.model.IntPoint
import com.xenotactic.gamelogic.model.MapEntity
import com.xenotactic.gamelogic.model.MapEntityType
import components.ObjectPlacementComponent
import components.GameMapControllerComponent
import engine.Engine
import events.EventBus
import ui.UIMap
import kotlin.math.floor

class ObjectPlacementInputProcessor(
    override val view: View,
    val uiMapView: UIMap,
    val engine: Engine,
    val eventBus: EventBus
) : MouseComponent {
    val objectPlacementComponent = engine.getOneTimeComponent<ObjectPlacementComponent>()
    val gameMapComponent = engine.getOneTimeComponent<GameMapControllerComponent>()

    val gridSize: Double
        get() = uiMapView._gridSize

    override fun onMouseEvent(views: Views, event: MouseEvent) {
        val (gridX, gridY) = uiMapView.getGridPositionsFromGlobalMouse(
            event.x.toDouble(), event.y.toDouble()
        )
        println(event)
        when (event.type) {
            MouseEvent.Type.DOWN -> {
                println(event)
                touchDown(event.button)

                //                println(
                //                    """
                //                    views: $views
                //                    event: $event,
                //                    camera.mouse.downPosGlobal: ${camera.mouse.downPosGlobal}
                //                    camera.mouse.downPosLocal: ${camera.mouse.downPosLocal}
                //                    camera.localToRenderXY(event.x.toDouble(), event.y.toDouble()): ${
                //                        camera.localToRenderXY(
                //                            event.x.toDouble(),
                //                            event.y.toDouble()
                //                        )
                //                    }
                //                    camera.globalToLocalXY(event.x.toDouble(), event.y.toDouble()): ${
                //                        camera.globalToLocalXY(
                //                            event.x.toDouble(),
                //                            event.y.toDouble()
                //                        )
                //                    }
                //                """.trimIndent()
                //                )
            }
            MouseEvent.Type.MOVE -> {
                mouseMoved(gridX, gridY)
            }
        }

        uiMapView.renderHighlightingForPointerAction(objectPlacementComponent.pointerAction)
    }

    fun touchDown(button: MouseButton) {
        //        println("screenX: $screenX, screenY: $screenY, pointer: $pointer, button: $button")
        if (button == MouseButton.LEFT) {
            val pointerAction = objectPlacementComponent.pointerAction
            when (pointerAction) {
                PointerAction.Inactive -> {
                    return
                }
                is PointerAction.HighlightForPlacement -> {
                    if (pointerAction.placementLocation == null) {
                        return
                    }
                    when (pointerAction.mapEntity) {
                        is MapEntity.Start -> TODO()
                        is MapEntity.Finish -> TODO()
                        is MapEntity.TeleportIn -> TODO()
                        is MapEntity.TeleportOut -> TODO()
                        is MapEntity.Rock -> {
                            println("Placing: $pointerAction")
                            gameMapComponent.placeEntity(
                                pointerAction.mapEntity.at(pointerAction.placementLocation!!)
                            )
                        }
                        is MapEntity.CheckPoint -> TODO()
                        is MapEntity.Tower, is MapEntity.SmallBlocker -> {
                            val entityAtPlace =
                                pointerAction.mapEntity.at(pointerAction.placementLocation!!)
                            if (!gameMapComponent.intersectsBlockingEntities(entityAtPlace)) {
                                gameMapComponent.placeEntity(
                                    entityAtPlace
                                )
                            }
                        }
                        else -> TODO()
                    }
                }
                is PointerAction.RemoveEntityAtPlace -> {
                    val data = pointerAction.data
                    if (data != null) {
                        gameMapComponent.removeEntity(data.entity)
                        pointerAction.data = null
                        uiMapView.renderHighlightingForPointerAction(pointerAction)
                    }
                }
                else -> TODO("Unsupported: $pointerAction")
            }
        }
    }

    fun mouseMoved(gridX: Double, gridY: Double) {
        val pointerAction = objectPlacementComponent.pointerAction

        when (pointerAction) {
            PointerAction.Inactive -> {
                return
            }
            is PointerAction.HighlightForPlacement -> {
                val currentEntity = pointerAction.mapEntity
                val (gridXToInt, gridYToInt) =
                    uiMapView.getRoundedGridCoordinates(
                        gridX,
                        gridY,
                        currentEntity.width,
                        currentEntity.height
                    )

                if ((gridXToInt !in 0 until gameMapComponent.width)
                    || gridYToInt !in 0 until gameMapComponent.height
                ) {
                    pointerAction.placementLocation = null
                    return
                }

                pointerAction.placementLocation = IntPoint(gridXToInt, gridYToInt)
                uiMapView.renderHighlightingForPointerAction(pointerAction)
            }
            is PointerAction.RemoveEntityAtPlace -> {
                val roundedGridX = floor(
                    gridX
                ).toInt()

                val roundedGridY = floor(
                    gridY
                ).toInt()

                if (pointerAction.data != null) {
                    val removeEntityData = pointerAction.data!!
                    if (removeEntityData.x == roundedGridX && removeEntityData.y == roundedGridY) {
                        return
                    }
                }

                val firstEntityAtPoint = when (pointerAction.entityType) {
                    MapEntityType.START -> TODO()
                    MapEntityType.FINISH -> TODO()
                    MapEntityType.CHECKPOINT -> TODO()
                    MapEntityType.ROCK -> gameMapComponent.getFirstRockAt(
                        roundedGridX,
                        roundedGridY
                    )
                    MapEntityType.TOWER -> gameMapComponent.getFirstTowerAt(
                        roundedGridX,
                        roundedGridY
                    )
                    MapEntityType.TELEPORT_IN -> TODO()
                    MapEntityType.TELEPORT_OUT -> TODO()
                    MapEntityType.SMALL_BLOCKER -> TODO()
                    MapEntityType.SPEED_AREA -> TODO()
                }

                if (firstEntityAtPoint == null) {
                    pointerAction.data = null
                } else {
                    pointerAction.data = RemoveEntityData(
                        roundedGridX,
                        roundedGridY,
                        firstEntityAtPoint
                    )
                }
            }
            else -> TODO("Unsupported: $pointerAction")
        }
    }
}
