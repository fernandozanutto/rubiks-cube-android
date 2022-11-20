package com.fzanutto.rubikscube

import jmini3d.Font
import jmini3d.HudScene
import jmini3d.Object3d
import jmini3d.Rect
import jmini3d.Texture
import jmini3d.geometry.SpriteGeometry
import jmini3d.material.SpriteMaterial


class ArrowsHudScene : HudScene() {
    var width = 0
    var font: Font = ArialFont()
    private var _title = "xxxxxxxxx A very long string to be replaced xxxxxxxxxx"
    var titleObject3d: Object3d
    var fm: Rect = Rect()
    var buttonRight: Object3d
    var buttonLeft: Object3d

    init {
        titleObject3d = font.getTextLine(_title, fm)
        addChild(titleObject3d)
        val buttonRightMaterial = SpriteMaterial(Texture("arrow_right.png"))
        val buttonRightGeometry = SpriteGeometry(1)
        buttonRightGeometry.addSprite(0f, 0f, 0f, 0f)
        buttonRight = Object3d(buttonRightGeometry, buttonRightMaterial)
        addChild(buttonRight)
        val buttonLeftMaterial = SpriteMaterial(Texture("arrow_left.png"))
        val buttonLeftGeometry = SpriteGeometry(1)
        buttonLeftGeometry.addSprite(0f, 0f, 0f, 0f)
        buttonLeft = Object3d(buttonLeftGeometry, buttonLeftMaterial)
        addChild(buttonLeft)
    }

    /**
     * Hud elements need viewport size, this method is also called each time that the viewport is changed
     */
    override fun onViewPortChanged(width: Int, height: Int) {
        this.width = width
        setTitleObjectScale()
        val buttonWidth = (Math.min(width, height) / 5).toFloat()
        (buttonLeft.getGeometry3d() as SpriteGeometry).setSpritePosition(
            0,
            0f,
            height - buttonWidth,
            buttonWidth,
            height.toFloat()
        )
        (buttonRight.getGeometry3d() as SpriteGeometry).setSpritePosition(
            0,
            width - buttonWidth,
            height - buttonWidth,
            width.toFloat(),
            height.toFloat()
        )
    }

    fun setTitle(title: String) {
        if (this._title !== title) {
            font.setTextLine(titleObject3d, title, fm)
            setTitleObjectScale()
        }
    }

    private fun setTitleObjectScale() {
        // Do not exceed screen width
        var titleScale = width.toFloat() / fm.right
        if (titleScale > 1) {
            titleScale = 1f
        }
        titleObject3d.scale = titleScale
        // center in screen
        titleObject3d.setPosition(
            (width - fm.right.toFloat() * titleScale) / 2,
            fm.bottom * titleScale * 0.5f,
            0f
        )
    }
}