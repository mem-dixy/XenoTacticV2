package korge_utils

import com.soywiz.korev.MouseButton
import com.soywiz.korev.MouseEvent
import com.soywiz.korge.component.ResizeComponent
import com.soywiz.korge.input.MouseEvents
import com.soywiz.korge.view.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import model.GameMap
import kotlin.math.min


fun <T : View> T.getReferenceParent(): Container {
    val parentView = this.parent!!
    return parentView.referenceParent ?: parentView
}

fun <T : View> T.alignLeftToLeftOfWindow(): T {
    this.x = getReferenceParent().getVisibleLocalArea().x
    return this
}

fun <T : View> T.alignTopToTopOfWindow(): T {
    this.y = getReferenceParent().getVisibleLocalArea().y
    return this
}

fun <T : View> T.scaleWhileMaintainingAspect(width: Double, height: Double): T {
    val scaledByWidth = width / this.scaledWidth
    val scaledByHeight = height / this.scaledHeight
    val scaleValue = min(scaledByHeight, scaledByWidth)
    this.scaledHeight = this.scaledHeight * scaleValue
    this.scaledWidth = this.scaledWidth * scaleValue
    return this
}

fun <T : View> T.alignBottomToBottomOfWindow(): T {
    val refParent = getReferenceParent()
    val globalArea = refParent.getVisibleGlobalArea()
    val localArea = refParent.getVisibleLocalArea()
    val windowsBound = refParent.windowBounds
    val localAreaFromGlobal = globalToLocalXY(globalArea.width, globalArea.height)
    val globalAreaFromLocal = localToGlobalXY(localArea.width, localArea.height)
    val windowBoundsToGlobal = localToGlobalXY(windowBounds.height, windowBounds.width)
    val windowBoundsToLocal = globalToLocalXY(windowBounds.height, windowBounds.width)
    val windowsArea = this.getVisibleWindowArea()
    println(
        """
        refParent.getVisibleLocalArea(): ${refParent.getVisibleLocalArea()}
        refParent.getVisibleGlobalArea(): ${refParent.getVisibleGlobalArea()}
        refParent.getVisibleWindowArea(): ${refParent.getVisibleWindowArea()}
        localAreaFromGlobal: $localAreaFromGlobal
        globalAreaFromLocal: $globalAreaFromLocal
        refParent.height: ${refParent.height}
        refParent.width: ${refParent.width}
        refParent.windowBounds: ${refParent.windowBounds}
        this.scaledHeight: ${this.scaledHeight}
        this.height: ${this.height}
        this.unscaledHeight: ${this.unscaledHeight}
        windowBoundsToGlobal: ${windowBoundsToGlobal}
        windowBoundsToLocal: ${windowBoundsToLocal}
    """.trimIndent()
    )

    return alignBottomToBottomOfWindow(windowsArea.width.toInt(), windowsArea.height.toInt())
}

fun <T : View> T.alignBottomToBottomOfWindow(resizedWidth: Int, resizedHeight: Int): T {
    val refParent = getReferenceParent()
    val globalArea = refParent.getVisibleGlobalArea()
    val localArea = refParent.getVisibleLocalArea()
    val windowsBounds = refParent.windowBounds
    val localAreaFromGlobal = globalToLocalXY(globalArea.width, globalArea.height)
    val globalAreaFromLocal = localToGlobalXY(localArea.width, localArea.height)
    val windowBoundsToGlobal = localToGlobalXY(windowBounds.height, windowBounds.width)
    val windowBoundsToLocal = globalToLocalXY(windowBounds.height, windowBounds.width)
    val windowsArea = this.getVisibleWindowArea()
    val resizeWHToLocal =
        refParent.globalToLocalXY(resizedWidth.toDouble(), resizedHeight.toDouble())
    println(
        """
        resizedWidth: $resizedWidth, resizedHeight: $resizedHeight
        refParent.getVisibleLocalArea(): ${refParent.getVisibleLocalArea()}
        refParent.getVisibleGlobalArea(): ${refParent.getVisibleGlobalArea()}
        refParent.getVisibleWindowArea(): ${refParent.getVisibleWindowArea()}
        globalArea: $globalArea
        localArea: $localArea
        windowsBounds: $windowsBounds
        localAreaFromGlobal: $localAreaFromGlobal
        globalAreaFromLocal: $globalAreaFromLocal
        refParent.height: ${refParent.height}
        refParent.width: ${refParent.width}
        refParent.windowBounds: ${refParent.windowBounds}
        this.scaledHeight: ${this.scaledHeight}
        this.height: ${this.height}
        this.unscaledHeight: ${this.unscaledHeight}
        windowBoundsToGlobal: ${windowBoundsToGlobal}
        windowBoundsToLocal: ${windowBoundsToLocal}
        resizeWHToLocal: $resizeWHToLocal
    """.trimIndent()
    )

    this.y = resizeWHToLocal.y - this.scaledHeight
    return this
}

fun <T : View> T.alignRightToRightOfWindow(): T {
    val refParent = getReferenceParent()
    //    println("""
    //        refParent.getVisibleLocalArea(): ${refParent.getVisibleLocalArea()}
    //        refParent.getVisibleGlobalArea(): ${refParent.getVisibleGlobalArea()}
    //        refParent.getVisibleWindowArea(): ${refParent.getVisibleWindowArea()}
    //    """.trimIndent())
    this.x = getReferenceParent().getVisibleGlobalArea().width - this.width
    return this
}



fun <T : View> T.scaledDimensions() =
    Pair(this.scaledWidth, this.scaledHeight)

fun <T : View> T.unscaledDimensions() =
    Pair(this.unscaledWidth, this.unscaledHeight)

fun <T : Container> T.onStageResizedV2(
    firstTrigger: Boolean = true, block: Views.(
        width: Int,
        height: Int
    ) -> Unit
): T = this.apply {
    if (firstTrigger) {
        deferWithViews { views ->
            val windowsArea = getVisibleWindowArea()
            block(
                views,
                windowsArea.width.toInt(),
                windowsArea.height.toInt()
            )
        }
    }
    addComponent(ResizeComponent(this, block))
}

fun MouseEvents.isScrollDown(): Boolean {
    val event = this.lastEvent
    return event.type == MouseEvent.Type.SCROLL && event.button == MouseButton.BUTTON_WHEEL &&
            event.scrollDeltaYLines > 0
}

fun MouseEvents.isScrollUp(): Boolean {
    val event = this.lastEvent
    return event.type == MouseEvent.Type.SCROLL && event.button == MouseButton.BUTTON_WHEEL &&
            event.scrollDeltaYLines < 0
}