package com.xenotactic.korge.ui

import com.soywiz.korge.annotations.KorgeExperimental
import com.soywiz.korge.input.onClick
import com.soywiz.korge.ui.*
import com.soywiz.korge.view.*
import com.soywiz.korui.UiButton
import com.xenotactic.ecs.World
import com.xenotactic.gamelogic.model.MapEntityType
import com.xenotactic.gamelogic.utils.toGameUnit
import com.xenotactic.korge.engine.Engine
import com.xenotactic.korge.events.EntitySelectionChangedEvent
import com.xenotactic.korge.events.EscapeButtonActionEvent
import com.xenotactic.korge.events.ResizeMapEvent
import com.xenotactic.korge.input_processors.MouseDragInputProcessor
import com.xenotactic.korge.input_processors.PlacedEntityEvent
import com.xenotactic.korge.input_processors.SelectorMouseProcessorV2
import com.xenotactic.korge.korge_utils.alignBottomToBottomOfWindow
import com.xenotactic.korge.state.EditorState
import com.xenotactic.korge.state.GameMapApi
import com.xenotactic.korge.state.GameMapDimensionsState

@OptIn(KorgeExperimental::class)
class UIEditorButtonsV2(
    val engine: Engine,
    val uiMapV2: UIMapV2,
    val baseView: SContainer
) : Container() {
    private val gameMapDimensionsState = engine.injections.getSingleton<GameMapDimensionsState>()
    private val editorState = engine.injections.getSingleton<EditorState>()
    private val mouseDragInputProcessor = engine.injections.getSingleton<MouseDragInputProcessor>()
    private val gameMapApi = engine.injections.getSingleton<GameMapApi>()
    private val selectorMouseProcessor = engine.injections.getSingleton<SelectorMouseProcessorV2>()

    private val DEFAULT_NOTIFICATION_TEXT = "N/A"

    init {
        val buttonStack = uiHorizontalStack {
            val addStartButton = uiButton(text = "Add Start") {
                onClick {
                    if (editorState.isEditingEnabled && editorState.entityTypeToPlace == MapEntityType.START) { // Switching to playing mode
                        switchToPlayingMode()
                    } else { // Switch to editing mode
                        switchToEditingMode(MapEntityType.START)
                    }
                }
            }
            val addFinishButton = uiButton(text = "Add Finish") {
                onClick {
                    if (editorState.isEditingEnabled && editorState.entityTypeToPlace == MapEntityType.FINISH) { // Switching to playing mode
                        switchToPlayingMode()
                    } else { // Switch to editing mode
                        switchToEditingMode(MapEntityType.FINISH)
                    }
                }
            }
            val addCheckpoint = uiButton(text = "Add Checkpoint") {
                onClick {
                    if (editorState.isEditingEnabled && editorState.entityTypeToPlace == MapEntityType.FINISH) { // Switching to playing mode
                        switchToPlayingMode()
                    } else { // Switch to editing mode
                        switchToEditingMode(MapEntityType.CHECKPOINT)
                    }
                }
            }
            val addTeleport = uiButton(text = "Add Teleport") {
                onClick {
                    if (editorState.isEditingEnabled && editorState.entityTypeToPlace == MapEntityType.FINISH) { // Switching to playing mode
                        switchToPlayingMode()
                    } else { // Switch to editing mode
                        switchToEditingMode(MapEntityType.TELEPORT_IN)
                    }
                }
            }
            uiButton(text = "Add rocks") {
                onClick {
                    if (editorState.isEditingEnabled && editorState.entityTypeToPlace == MapEntityType.ROCK) { // Switching to playing mode
                        switchToPlayingMode()
                    } else { // Switch to editing mode
                        switchToEditingMode(MapEntityType.ROCK)
                    }
                }
            }
            uiButton("Resize map") {
                onClick {
                    mouseDragInputProcessor.adjustSettings {
                        isEnabled = false
                    }
                    baseView.uiWindow("Resize Map", 150.0, 150.0) {
                        val thisWindow = it
                        uiVerticalStack {
                            uiText("Width:")
                            val widthInput = uiTextInput(uiMapV2.mapWidth.toString())
                            uiText("Height:")
                            val heightInput = uiTextInput(uiMapV2.mapHeight.toString())
                            uiButton("Apply") {
                                onClick {
                                    thisWindow.close()
                                    mouseDragInputProcessor.adjustSettings {
                                        isEnabled = true
                                    }
                                    gameMapDimensionsState.changeDimensions(
                                        widthInput.text.toInt().toGameUnit(),
                                        heightInput.text.toInt().toGameUnit()
                                    )
                                }
                            }
                            centerXOn(it)
                        }
                        it.centerOn(baseView)
                    }
                }
            }
            engine.eventBus.register<PlacedEntityEvent> {
                when (it.entityType) {
                    MapEntityType.START -> addStartButton.disable()
                    MapEntityType.FINISH -> addFinishButton.disable()
                    MapEntityType.CHECKPOINT -> Unit
                    MapEntityType.ROCK -> TODO()
                    MapEntityType.TOWER -> TODO()
                    MapEntityType.TELEPORT_IN -> TODO()
                    MapEntityType.TELEPORT_OUT -> Unit
                    MapEntityType.SMALL_BLOCKER -> TODO()
                    MapEntityType.SPEED_AREA -> TODO()
                    MapEntityType.MONSTER -> TODO()
                }
                switchToPlayingMode()
            }
        }

        engine.eventBus.register<EscapeButtonActionEvent> {
            switchToPlayingMode()
        }

        val deleteEntitiesButton = UIButton(text="Delete entities")

        engine.eventBus.register<EntitySelectionChangedEvent> {
            println("EntitySelectionChangedEvent: Went in here?! ${engine.gameWorld.selectionFamily.getList()}")
            if (engine.gameWorld.selectionFamily.getList().isEmpty()) {
                deleteEntitiesButton.removeFromParent()
            } else {
                buttonStack.addChild(deleteEntitiesButton)
            }

            resize()
        }
    }

    fun resize() {
        centerXOn(this.baseView)
        alignBottomToBottomOf(this.baseView)
//        alignBottomToBottomOfWindow()
    }

    fun switchToPlayingMode() {
        engine.eventBus.send(NotificationTextUpdateEvent(DEFAULT_NOTIFICATION_TEXT))
        mouseDragInputProcessor.adjustSettings {
            isEnabled = true
        }
        editorState.isEditingEnabled = false
        uiMapV2.hideHighlightRectangle()
        uiMapV2.clearHighlightLayer()
        selectorMouseProcessor.isEnabled = true
        println("selectorMouseProcessor is enabled")
    }

    fun switchToEditingMode(entityType: MapEntityType) {
        engine.eventBus.send(
            NotificationTextUpdateEvent(
                gameMapApi.getNotificationText(
                    entityType
                )
            )
        )
        mouseDragInputProcessor.adjustSettings {
            allowLeftClickDragging = false
        }
        editorState.isEditingEnabled = true
        editorState.entityTypeToPlace = entityType
        selectorMouseProcessor.isEnabled = false
        selectorMouseProcessor.reset()
    }

}