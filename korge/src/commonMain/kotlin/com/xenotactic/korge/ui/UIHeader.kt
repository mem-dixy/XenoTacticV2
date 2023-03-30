package com.xenotactic.korge.ui

import com.soywiz.korge.input.onClick
import korlibs.korge.view.Container
import korlibs.korge.view.SolidRect
import korlibs.korge.view.Text
import korlibs.korge.view.alignLeftToRightOf
import korlibs.korge.view.alignRightToRightOf
import korlibs.korge.view.centerOn
import korlibs.korge.view.centerYOn
import korlibs.korge.view.container
import korlibs.korge.view.solidRect
import korlibs.korge.view.text
import korlibs.image.color.Colors
import korlibs.io.async.Signal
import com.xenotactic.korge.korge_utils.scaledDimensions
import kotlin.collections.set

enum class UIHeaderSection {
    PLAY,
    EDITOR,
    MY_MAPS
}

class UIHeader(
    val userName: String,
    headerHeight: Double = 50.0,
    headerWidth: Double
) : Container() {
    val onHeaderSectionClick = Signal<UIHeaderSection>()

    lateinit var selectedSectionBox: SolidRect

    val sectionToTextMap = mutableMapOf<UIHeaderSection, Text>()

    init {
        val HEADER_TITLE_PADDING_LEFT = 10.0
        val PROFILE_PADDING_TOP_AND_BOTTOM = 10.0
        val PROFILE_PADDING_RIGHT = 5.0

        val headerSection = this.solidRect(
            headerWidth, headerHeight,
            color = Colors.BROWN
        )
        this.text(userName, textSize = 30.0) {
            this.centerYOn(headerSection)
            this.x += HEADER_TITLE_PADDING_LEFT
        }

        val headerTextSize = 20.0
        val textSpacing = 50.0
        val textWidth = 75.0

        val headerOptions = this.container {
            val playText = this.text("Play", textSize = headerTextSize) {
                sectionToTextMap[UIHeaderSection.PLAY] = this
                onClick {
                    onHeaderSectionClick(UIHeaderSection.PLAY)
                }
            }
//            val editorText = this.text("Editor", textSize = headerTextSize) {
//                sectionToTextMap[UIHeaderSection.EDITOR] = this
//                onClick {
//                    onHeaderSectionClick(UIHeaderSection.EDITOR)
//                }
//            }
            val myMapsText = this.text("My Maps", textSize = headerTextSize) {
                sectionToTextMap[UIHeaderSection.MY_MAPS] = this
                onClick {
                    onHeaderSectionClick(UIHeaderSection.MY_MAPS)
                }
            }

            val textViews = mutableListOf(playText, myMapsText)
            textViews.windowed(2) {
                it[1].alignLeftToRightOf(it[0], textSpacing)
            }

            selectedSectionBox = this.solidRect(25, 25, Colors.BLACK.withAd(0.5))
            this.sendChildToBack(selectedSectionBox)

            updateSelectionBox(UIHeaderSection.PLAY)

            println("uiHeader:")
            println(
                """
                    playText.scaledDimensions(): ${playText.scaledDimensions()}
                    myMapsText.scaledDimensions(): ${myMapsText.scaledDimensions()}
                """.trimIndent()
            )
        }

        headerOptions.centerYOn(headerSection)
        headerOptions.x += 200.0

        val profileSection = this.solidRect(
            100.0, headerHeight - PROFILE_PADDING_TOP_AND_BOTTOM,
            color = Colors.BLACK.withAd(0.25)
        )
        profileSection.alignRightToRightOf(headerSection, padding = PROFILE_PADDING_RIGHT)
        profileSection.centerYOn(headerSection)

        val profileText = this.text(userName, textSize = 20.0)
        profileText.centerOn(profileSection)
    }

    fun updateSelectionBox(uiHeaderSection: UIHeaderSection) {
        val uiText = sectionToTextMap[uiHeaderSection]!!
        selectedSectionBox.scaledWidth = uiText.scaledWidth + 10
        selectedSectionBox.scaledHeight = uiText.scaledHeight + 10
        selectedSectionBox.centerOn(uiText)
    }
}