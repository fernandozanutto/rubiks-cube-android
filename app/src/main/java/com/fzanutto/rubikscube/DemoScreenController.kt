package com.fzanutto.rubikscube

import jmini3d.Renderer3d
import jmini3d.ScreenController
import jmini3d.Vector3
import jmini3d.input.KeyListener
import jmini3d.input.TouchListener
import jmini3d.input.TouchPointer


class DemoScreenController : ScreenController, TouchListener, KeyListener {
    var cameraAngle = 0f
    var initialTime: Long
    var sceneIndex = 0
    var scenes: Array<ParentScene> = arrayOf(
        RubikScene()
    )
    var hudScene: ArrowsHudScene
    var cameraModes = intArrayOf(0, 0, 0, 0, 1, 2, 2)

    init {
        initialTime = System.currentTimeMillis()
        hudScene = ArrowsHudScene()
    }

    override fun onNewFrame(forceRedraw: Boolean): Boolean {
        hudScene.setTitle(scenes[sceneIndex].title)

        // Rotate camera...
        cameraAngle = 0.0005f * (System.currentTimeMillis() - initialTime)
        val d = 5f
        val target: Vector3 = scenes[sceneIndex].getCamera().getTarget()
        when (cameraModes[sceneIndex]) {
            0 -> scenes[sceneIndex].getCamera().setPosition(
                (target.x - d * Math.cos(cameraAngle.toDouble())).toFloat(),
                (target.y - d * Math.sin(cameraAngle.toDouble())).toFloat(),  //
                target.z + (d * Math.sin(cameraAngle.toDouble())).toFloat()
            )
            1 -> scenes[sceneIndex].getCamera().setPosition(
                (target.x - d * Math.cos(cameraAngle.toDouble())).toFloat(),
                (target.y - d * Math.sin(cameraAngle.toDouble())).toFloat(),
                d / 2
            )
            2 -> scenes[sceneIndex].getCamera()
                .setPosition(target.x - d, target.y, target.z + d / 4)
        }
        scenes[sceneIndex].update()
        return true // Render all the frames
    }

    override fun render(renderer3d: Renderer3d) {
        renderer3d.render(scenes[sceneIndex])
        renderer3d.render(hudScene)
    }

    private fun nextScene() {
        if (sceneIndex >= scenes.size - 1) {
            sceneIndex = 0
        } else {
            sceneIndex++
        }
    }

    private fun previousScene() {
        if (sceneIndex <= 0) {
            sceneIndex = scenes.size - 1
        } else {
            sceneIndex--
        }
    }

    override fun onTouch(pointers: HashMap<Int, TouchPointer>): Boolean {
        for (key in pointers.keys) {
            val pointer = pointers[key]
            if (pointer!!.status == TouchPointer.TOUCH_DOWN) {
                if (pointer.x > scenes[sceneIndex].getCamera().getWidth() / 2) {
                    nextScene()
                } else {
                    previousScene()
                }
            }
        }
        return true
    }

    override fun onKeyDown(key: Int): Boolean {
        when (key) {
            KeyListener.KEY_RIGHT -> {
                nextScene()
                return true
            }
            KeyListener.KEY_LEFT -> {
                previousScene()
                return true
            }
        }
        return false
    }

    override fun onKeyUp(key: Int): Boolean {
        return false
    }
}