import com.soywiz.klock.Frequency
import com.soywiz.korge.Korge
import com.soywiz.korge.input.onClick
import com.soywiz.korge.ui.uiButton
import com.soywiz.korge.ui.uiText
import com.soywiz.korge.view.*
import com.soywiz.korim.color.Colors
import com.soywiz.korim.color.MaterialColors
import com.soywiz.korim.font.readTtfFont
import com.soywiz.korim.format.ASE
import com.soywiz.korim.format.readImageDataContainer
import com.soywiz.korim.format.toProps
import com.soywiz.korim.text.TextAlignment
import com.soywiz.korio.async.runBlockingNoJs
import com.soywiz.korio.file.std.resourcesVfs
import com.soywiz.korma.geom.*
import com.xenotactic.gamelogic.views.EightDirection
import com.xenotactic.gamelogic.views.UIEightDirectionalSprite
import com.xenotactic.korge.korge_utils.getDirection8
import com.xenotactic.korge.korge_utils.kAngleTo
import com.xenotactic.korge.ui.UIFixedGrid
import com.xenotactic.korge.ui.UITextRect
import kotlin.jvm.JvmStatic

object DebugMain4 {

    @JvmStatic
    fun main(args: Array<String>) = runBlockingNoJs {
        Korge(width = 640, height = 480, bgcolor = Colors.LIGHTGRAY) {

//            val font = resourcesVfs["fonts/AtkinsonHyperlegible-Regular.ttf"].readTtfFont()
            val font = resourcesVfs["fonts/AtkinsonHyperlegible-Bold.ttf"].readTtfFont()

//            val container = container {
//                solidRect(400, 300, color = MaterialColors.AMBER_900)
////                uiText("Global\nDamage\nUpgrade", 50.0, 50.0) {
////                    bgcolor = Colors.LIGHTGRAY
////                }
//                val textBg = solidRect(50, 50, Colors.DARKGRAY)
//                val text = text("Global\nDamage\nUpgrade", alignment = TextAlignment.MIDDLE_CENTER, font = font) {
//                    smoothing = false
//                    scaleWhileMaintainingAspect(ScalingOption.ByWidthAndHeight(48.0, 48.0))
//                    xy(25, 25)
//                }
//            }


            val fixedGrid = UIFixedGrid(4, 2, 400.0, 200.0, 5.0, 5.0)
            addChild(fixedGrid)

            val globalDamageUpgradeView = UITextRect(
                "Global\nDamage\nUpgrade",
                50.0, 50.0, 5.0, font).apply {

            }

            val globalRangeUpgradeView = UITextRect(
                "Global\nRange\nUpgrade",
                50.0, 50.0, 5.0, font).apply {
            }

            val incomeUpgradeView = UITextRect(
                "Income\nUpgrade",
                50.0, 50.0, 5.0, font).apply {
            }

            val towerDamageUpgradeView = UITextRect(
                "Tower\nDamage\nUpgrade",
                50.0, 50.0, 5.0, font).apply {
            }

            val towerSpeedUpgradeView = UITextRect(
                "Tower\nSpeed\nUpgrade",
                50.0, 50.0, 5.0, font).apply {
            }

            fixedGrid.setEntry(0, 0, globalDamageUpgradeView)
            fixedGrid.setEntry(1, 0, globalRangeUpgradeView)
            fixedGrid.setEntry(2, 0, incomeUpgradeView)
            fixedGrid.setEntry(0, 1, towerDamageUpgradeView)
            fixedGrid.setEntry(1, 1, towerSpeedUpgradeView)
        }
    }
}