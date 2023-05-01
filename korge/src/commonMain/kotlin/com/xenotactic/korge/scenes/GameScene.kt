package com.xenotactic.korge.scenes

import korlibs.time.TimeSpan
import korlibs.logger.Logger
import korlibs.event.Key
import korlibs.korge.input.draggable
import korlibs.korge.scene.Scene
import korlibs.korge.view.*
import com.xenotactic.korge.bridges.MapBridge
import com.xenotactic.korge.ecomponents.GameMapControllerEComponent
import com.xenotactic.korge.ecomponents.GoalEComponent
import com.xenotactic.korge.ecomponents.ObjectPlacementEComponent
import com.xenotactic.gamelogic.engine.Engine
import com.xenotactic.gamelogic.events.EventBus
import com.xenotactic.korge.events.ExitGameSceneEvent
import com.xenotactic.gamelogic.events.UpdatedPathLineEvent
import com.xenotactic.korge.input_processors.CameraInputProcessor
import com.xenotactic.korge.input_processors.KeyInputProcessor
import com.xenotactic.korge.input_processors.ObjectPlacementInputProcessor
import com.xenotactic.korge.korge_components.ResizeDebugComponent
import com.xenotactic.korge.korge_utils.alignBottomToBottomOfWindow
import com.xenotactic.korge.korge_utils.alignRightToRightOfWindow
import com.xenotactic.korge.renderer.MapRendererUpdater
import com.xenotactic.korge.ui.UIMap
import com.xenotactic.korge.ui.UIPathText
import com.xenotactic.korge.ui.UIPlacementButton
import com.xenotactic.korge.ui.uiActiveTextNotifier
import com.xenotactic.korge.ui.uiPlacement
import korlibs.korge.view.align.centerXOnStage
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class GameScene(val mapBridge: MapBridge) : Scene() {
    val eventBus = EventBus(MainScope())

    init {
        logger.debug {
            "init called"
        }
    }

    override suspend fun SContainer.sceneInit() {
        Logger.defaultLevel = Logger.Level.DEBUG
        logger.debug {
            "sceneInit called"
        }
        val engine = Engine(eventBus)
        val gameMapControllerComponent = GameMapControllerEComponent(engine, eventBus)
        val objectPlacementComponent = ObjectPlacementEComponent()
        engine.injections.setSingletonOrThrow(gameMapControllerComponent)
        engine.injections.setSingletonOrThrow(objectPlacementComponent)

        //        val gameMap = loadGameMapFromGoldensBlocking("00051.json")
        val gameMap = mapBridge.gameMap

        gameMapControllerComponent.updateMap(gameMap)

        //        this.setSize(gameMap.width * GRID_SIZE, gameMap.height * GRID_SIZE)

        val uiMap =
            UIMap(gameMap, engine, shortestPath = gameMapControllerComponent.shortestPath).apply {
                draggable()
            }
        MapRendererUpdater(engine, uiMap, eventBus)
        engine.injections.setSingletonOrThrow(uiMap)

        val cameraInputProcessor = CameraInputProcessor(uiMap, engine)
        cameraInputProcessor.setZoomFactor(0.7)
        cameraInputProcessor.setup(this)

        val objectPlacementInputProcessor = ObjectPlacementInputProcessor(
            this, uiMap, engine, eventBus
        )
        objectPlacementInputProcessor.setup(this)

        val resizeDebugComponent = ResizeDebugComponent(this)
        resizeDebugComponent.setup(this)

        val uiPlacement = uiPlacement(engine, eventBus).apply {
            onStageResized(true) { width: Int, height: Int ->
                alignRightToRightOfWindow()
                alignBottomToBottomOfWindow()
            }

            onButtonClick {
                println("Button clicked: $it")
                when (it) {
                    UIPlacementButton.VIEW_ROCK_COUNTERS -> {
                        uiMap.viewRockCounters()
                    }
                }
            }
        }

        uiActiveTextNotifier(engine, eventBus).run {
            centerXOnStage()
        }

        val pathText = UIPathText().addTo(this@sceneInit) {
            onStageResized(true) { width: Int, height: Int ->
                alignBottomToBottomOfWindow()
            }

            eventBus.register<UpdatedPathLineEvent> {
                updatePathLength(it.newPathLength)
            }

            updatePathLength(gameMapControllerComponent.shortestPath?.pathLength)
        }

        val keyInputProcessor = KeyInputProcessor(this, engine)
        keyInputProcessor.setup(this)
//        val monstersComponent = MonstersEComponent(uiMap, engine, eventBus, uiMap._gridSize)
//        addComponent(monstersComponent)

        val goalComponent = GoalEComponent(engine, eventBus)
        engine.injections.setSingletonOrThrow(goalComponent)
        //        goalComponent.calculateGoalForMap()

        val MOVE_MAP_DELTA = 7

        addFixedUpdater(TimeSpan(10.0)) {
            val keys = views.keys
            if (keys[Key.UP]) uiMap.y = uiMap.y + MOVE_MAP_DELTA
            if (keys[Key.DOWN]) uiMap.y = uiMap.y - MOVE_MAP_DELTA
            if (keys[Key.LEFT]) uiMap.x = uiMap.x + MOVE_MAP_DELTA
            if (keys[Key.RIGHT]) uiMap.x = uiMap.x - MOVE_MAP_DELTA
        }

        eventBus.register<ExitGameSceneEvent> {
            launch {
                sceneContainer.back()
            }
        }
    }

    override suspend fun sceneAfterInit() {
        logger.info {
            "sceneAfterInit called"
        }

    }

    override suspend fun sceneDestroy() {
        logger.debug {
            "sceneDestroy called"
        }
    }

    override suspend fun sceneBeforeLeaving() {
        logger.debug {
            "sceneBeforeLeaving called"
        }
    }

    override suspend fun sceneAfterDestroy() {
        logger.debug {
            "sceneAfterDestroy called"
        }
    }


    companion object {
        val logger = Logger<GameScene>()
    }
}
