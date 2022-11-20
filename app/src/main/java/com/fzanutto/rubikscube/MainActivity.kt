package com.fzanutto.rubikscube

import android.os.Bundle
import com.fzanutto.rubikscube.databinding.ActivityMainBinding
import cube.Cube
import jmini3d.android.Activity3d
import jmini3d.android.input.InputController

class MainActivity : Activity3d() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)

        super.onCreate(savedInstanceState)

        val screenController = DemoScreenController()

        glSurfaceView3d.setScreenController(screenController)
        glSurfaceView3d.setLogFps(true)
        val inputController = InputController(glSurfaceView3d)
        inputController.setTouchListener(screenController)
        inputController.setKeyListener(screenController)
    }


    override fun onCreateSetContentView() {
        setContentView(binding.root)

        binding.view3d.addView(glSurfaceView3d)
    }
}