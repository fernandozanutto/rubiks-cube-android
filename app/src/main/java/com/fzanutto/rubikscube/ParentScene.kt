package com.fzanutto.rubikscube

import jmini3d.Scene

open class ParentScene(var title: String) : Scene() {
    open fun update() {}
    open fun onMoveScreen(deltaX: Double, deltaY: Double) {}
}