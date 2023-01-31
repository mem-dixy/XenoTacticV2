package com.xenotactic.korge.systems

import com.soywiz.klogger.Logger
import com.xenotactic.ecs.FamilyConfiguration
import com.xenotactic.ecs.System
import com.xenotactic.ecs.World
import com.xenotactic.gamelogic.components.*
import com.xenotactic.gamelogic.utils.toGameUnit
import com.xenotactic.korge.korge_utils.getCenterPoint
import com.xenotactic.korge.state.GameMapApi
import kotlin.time.Duration

class TowerAttackSystem(
    val world: World,
    val gameMapApi: GameMapApi
) : System() {
    companion object {
        val logger = Logger<TowerAttackSystem>()
    }

    override val familyConfiguration: FamilyConfiguration = FamilyConfiguration(
        allOfComponents = setOf(
            EntityTowerComponent::class,
            BottomLeftPositionComponent::class,
            SizeComponent::class,
            RangeComponent::class,
            TargetingComponent::class,
            ReloadTimeComponent::class,
            ReadyToAttackComponent::class
        ),
    )

    override fun update(deltaTime: Duration) {
        getFamily().getSequence().forEach { towerId ->
//            logger.info { "update: tower id: $towerId" }
            val sizeComponent = world[towerId, SizeComponent::class]
            val bottomLeftPositionComponent =
                world[towerId, BottomLeftPositionComponent::class]
            val towerCenterPoint = getCenterPoint(bottomLeftPositionComponent, sizeComponent)

            val damage = gameMapApi.calculateTowerDamage(towerId)

            val targetingComponent = world[towerId, TargetingComponent::class]

            world.addEntity {
                addComponentOrThrow(ProjectileComponent)
                addComponentOrThrow(targetingComponent)
                addComponentOrThrow(VelocityComponent(0.2.toGameUnit()))
                addComponentOrThrow(
                    MutableCenterPositionComponent(
                        towerCenterPoint.x,
                        towerCenterPoint.y
                    )
                )
                addComponentOrThrow(ProjectileDamageComponent(damage))
            }

            world.modifyEntity(towerId) {
                addComponentOrThrow(ReloadDowntimeComponent(0.0))
                removeComponent<ReadyToAttackComponent>()
            }
        }
    }
}