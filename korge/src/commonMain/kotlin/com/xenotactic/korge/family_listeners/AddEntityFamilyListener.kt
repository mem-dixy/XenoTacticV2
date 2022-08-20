package com.xenotactic.korge.family_listeners

import com.soywiz.korge.view.addTo
import com.soywiz.korge.view.xy
import com.xenotactic.ecs.Entity
import com.xenotactic.ecs.FamilyConfiguration
import com.xenotactic.ecs.FamilyListener
import com.xenotactic.ecs.World
import com.xenotactic.gamelogic.components.*
import com.xenotactic.gamelogic.model.MapEntityData
import com.xenotactic.gamelogic.utils.getCenterPoint
import com.xenotactic.korge.korge_utils.makeEntityLabelText
import com.xenotactic.gamelogic.views.UIEntity
import com.xenotactic.korge.ui.UIMapV2

class AddEntityFamilyListener(
    val world: World
) : FamilyListener {
    override val familyConfiguration: FamilyConfiguration = FamilyConfiguration(
        allOfComponents = setOf(
            MapEntityComponent::class, SizeComponent::class, BottomLeftPositionComponent::class
        ),
    )

    val uiMapV2 = world.injections.getSingleton<UIMapV2>()
    val mapEntityTypeContainer = world.getComponentContainer<MapEntityComponent>()
    val sizeComponentContainer = world.getComponentContainer<SizeComponent>()
    val bottomLeftPositionComponentContainer =
        world.getComponentContainer<BottomLeftPositionComponent>()

    override fun onAdd(entity: Entity) {
        val mapEntityComponent = mapEntityTypeContainer.getComponent(entity)
        val bottomLeftPositionComponent = bottomLeftPositionComponentContainer.getComponent(entity)
        val sizeComponent = sizeComponentContainer.getComponent(entity)
        val (worldX, worldY) = uiMapV2.getWorldCoordinates(
            bottomLeftPositionComponent.x, bottomLeftPositionComponent.y, sizeComponent.height
        )

        println("onAdd. mapEntityComponent: $mapEntityComponent")

        val uiEntity = UIEntity(
            mapEntityComponent.entityData.toMapEntityType(),
            sizeComponent.width,
            sizeComponent.height,
            uiMapV2.gridSize,
            uiMapV2.borderSize,
            if (mapEntityComponent.entityData is MapEntityData.SpeedArea) (mapEntityComponent.entityData as MapEntityData.SpeedArea).speedEffect else null
        ).apply {
            if (mapEntityComponent.entityData is MapEntityData.SpeedArea) {
                addTo(uiMapV2.speedAreaLayer)
            } else {
                addTo(uiMapV2.entityLayer)
            }
            xy(worldX, worldY)
        }

        val text = mapEntityComponent.entityData.getText()
        if (text != null) {
            val centerPoint = getCenterPoint(bottomLeftPositionComponent, sizeComponent)
            val (centerWorldX, centerWorldY) = uiMapV2.getWorldCoordinates(
                centerPoint.x.toInt(),
                centerPoint.y.toInt()
            )
            val textView = makeEntityLabelText(text).apply {
                addTo(uiMapV2._entityLabelLayer)
                xy(centerWorldX, centerWorldY)
                scaledHeight = uiMapV2.gridSize / 2
                scaledWidth = scaledHeight * unscaledWidth / unscaledHeight
            }
            world.modifyEntity(entity) {
                addComponentOrThrow(UIMapEntityTextComponent(textView))
            }
        }

        world.modifyEntity(entity) {
            addComponentOrThrow(UIMapEntityComponent(uiEntity))
        }

    }

    override fun onRemove(entity: Entity) {
    }

    override fun onExisting(entity: Entity) {
    }

}