package com.fzanutto.rubikscube

import android.util.Log
import jmini3d.Renderer3d
import jmini3d.ScreenController
import jmini3d.input.KeyListener
import jmini3d.input.TouchListener
import jmini3d.input.TouchPointer
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin


class MainScreenController : ScreenController, TouchListener, KeyListener {
    private var scene: ParentScene = RubikScene()
    private var hudScene: ArrowsHudScene = ArrowsHudScene()

    private var phi = 1.33
    private var theta = 0.28

    override fun onNewFrame(forceRedraw: Boolean): Boolean {
        hudScene.setTitle(scene.title)

        val distance = 8
        val xAxis = (sin(phi) * cos(theta)).toFloat() * distance
        val yAxis = (sin(phi) * sin(theta)).toFloat() * distance
        val zAxis = (cos(phi)).toFloat() * distance

        Log.d("DEBUG_CAMERA", "$phi $theta $xAxis $yAxis $zAxis")

        scene.getCamera().setPosition(
            xAxis,
            yAxis,
            zAxis
        )

        scene.update()
        return true // Render all the frames
    }

    override fun render(renderer3d: Renderer3d) {
        renderer3d.render(scene)
        renderer3d.render(hudScene)
    }

    private var lastXMove: Int? = null
    private var lastYMove: Int? = null

    override fun onTouch(pointers: HashMap<Int, TouchPointer>): Boolean {
        for (key in pointers.keys) {
            val pointer = pointers[key] ?: continue
            when (pointer.status) {
                TouchPointer.TOUCH_DOWN -> {}
                TouchPointer.TOUCH_MOVE -> {
                    val sensibility = 0.015
                    var deltaX = 0.0
                    var deltaY = 0.0
                    if (lastXMove != null) {
                        deltaX = (pointer.x - lastXMove!!) * sensibility
                        theta -= deltaX
                    }

                    if (lastYMove != null) {
                        deltaY = (pointer.y - lastYMove!!) * sensibility
                        phi -= deltaY

                        if(phi > PI) phi = PI
                        if(phi <= 0) phi = 0.0001
                    }

                    lastXMove = pointer.x
                    lastYMove = pointer.y

                    scene.onMoveScreen(deltaX, deltaY)
                }
                TouchPointer.TOUCH_UP -> {
                    lastYMove = null
                    lastXMove = null
                }
            }
        }
        return true
    }

    override fun onKeyDown(key: Int): Boolean {
        when (key) {
            KeyListener.KEY_RIGHT -> {
                return true
            }
            KeyListener.KEY_LEFT -> {
                return true
            }
        }
        return false
    }

    override fun onKeyUp(key: Int): Boolean {
        return false
    }
}