package korge_components

import com.soywiz.klock.TimeSpan
import com.soywiz.korge.component.UpdateComponent
import com.soywiz.korge.view.Container
import com.soywiz.korge.view.View
import com.soywiz.korge.view.circle
import com.soywiz.korge.view.xy
import com.soywiz.korim.color.Colors
import com.xenotactic.gamelogic.model.MONSTER_HEIGHT
import com.xenotactic.gamelogic.model.MONSTER_WIDTH
import com.xenotactic.gamelogic.model.MonsterEntity
import com.xenotactic.gamelogic.utils.toWorldCoordinates
import com.xenotactic.gamelogic.utils.toWorldDimensions
import components.GameMapControllerEComponent
import engine.EComponent
import engine.Engine
import events.EventBus
import events.SpawnCreepEvent
import pathing.PathSequenceTraversal

class MonstersEComponent(
    override val view: Container,
    val engine: Engine,
    val eventBus: EventBus,
    val gridSize: Double
) : UpdateComponent, EComponent {
    data class MonsterWithView(
        val monsterEntity: MonsterEntity, val view: View,
        val monsterRadius: Double
    )

    val gameMapControllerComponent = engine.getOneTimeComponent<GameMapControllerEComponent>()
    val monsters = mutableListOf<MonsterWithView>()

    init {
        eventBus.register<SpawnCreepEvent> {
            handleSpawnCreepEvent()
        }
    }

    fun handleSpawnCreepEvent() {
        if (gameMapControllerComponent.shortestPath != null) {
            val monsterEntity = MonsterEntity(
                PathSequenceTraversal(gameMapControllerComponent.shortestPath!!)
            )
            val (worldX, worldY) = toWorldCoordinates(
                gridSize,
                monsterEntity.currentPoint,
                gameMapControllerComponent.width, gameMapControllerComponent.height
            )
            val (worldWidth, worldHeight) = toWorldDimensions(MONSTER_WIDTH, MONSTER_HEIGHT, gridSize)
            val monsterRadius = worldWidth / 2
            monsters.add(
                MonsterWithView(
                    monsterEntity,
                    view.circle(monsterRadius, Colors.RED)
                        .xy(worldX - monsterRadius, worldY - monsterRadius),
                    monsterRadius
                )
            )
        }
    }

    override fun update(dt: TimeSpan) {
        for (monster in monsters) {
            val gameUnitDeltaDistance = dt.seconds * monster.monsterEntity.movementSpeedGameUnits
            val monsterEntity = monster.monsterEntity
            monsterEntity.pathSequenceTraversal.traverse(gameUnitDeltaDistance)
            val (worldX, worldY) = toWorldCoordinates(
                gridSize,
                monsterEntity.currentPoint,
                gameMapControllerComponent.width, gameMapControllerComponent.height
            )
            monster.view.xy(
                worldX - monster.monsterRadius,
                worldY - monster.monsterRadius
            )
        }

        monsters.removeAll {
            if (it.monsterEntity.pathSequenceTraversal.finishedTraversal()) {
                it.view.removeFromParent()
                true
            } else {
                false
            }

        }
    }
}