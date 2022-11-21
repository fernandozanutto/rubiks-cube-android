package com.fzanutto.rubikscube

import java.util.Random
import jmini3d.Color4
import jmini3d.CubeMapTexture
import jmini3d.Object3d
import jmini3d.Texture
import jmini3d.Vector3
import jmini3d.VertexColors
import jmini3d.geometry.BoxGeometry
import jmini3d.geometry.Geometry
import jmini3d.geometry.SkyboxGeometry
import jmini3d.geometry.VariableGeometry
import jmini3d.material.Material
import kotlin.math.abs
import kotlin.math.sign


class RubikScene : ParentScene("Rubik demo") {
    private var initialTimeMovement: Long = 0
    private var initialTime: Long
    private val direction = Vector3(0F, 1F, 0f)
    private val side = Vector3(1f, 0f, 0f)
    private val up = Vector3(0f, 0f, 1f)
    private val pos = Vector3(0f, 0f, 0f)
    private val cube3dObject = Object3d()
    private val rotationGroup = Object3d()
    private var nextRotationAxis = -1
    private var nextRotationSection = 0
    private var nextRotationAngle = 0f
    private val random: Random = Random()
    private val axis = arrayOf(
        Vector3(1f, 0f, 0f), // x axis
        Vector3(0f, 1f, 0f), // y axis
        Vector3(0f, 0f, 1f)  // z axis
    )
    private var rotations = arrayOfNulls<Vector3>(9 * 3)
    private var positions = arrayOfNulls<Vector3>(9)

    init {
        val envMap = CubeMapTexture(
            arrayOf(
                "posx.png",
                "negx.png",
                "posy.png",
                "negy.png",
                "posz.png",
                "negz.png"
            )
        )
        val skyboxGeometry: VariableGeometry = SkyboxGeometry(300f)
        val skyboxMaterial = Material()
        skyboxMaterial.setEnvMap(envMap, 0f)
        skyboxMaterial.setUseEnvMapAsMap(true)
        val skybox = Object3d(skyboxGeometry, skyboxMaterial)
        addChild(skybox)
        val map = Texture("cube.png")
        val material1 = Material(map)
        material1.setUseVertexColors(true)
        material1.setApplyColorToAlpha(true)
        material1.setColor(Color4(255, 0, 0, 255))
        val geometry: Geometry = BoxGeometry(1f)
        val colors = arrayOf(
            arrayOf(
                Color4(200, 0, 0, 255),  // x+
                Color4(200, 200, 0, 255)
            ), arrayOf(
                Color4(200, 200, 200, 255),  // y+
                Color4(0, 175, 0, 255)
            ), arrayOf(
                Color4(0, 0, 175, 255),  // z+
                Color4(200, 100, 0, 255)
            )
        )

        for (iz in -1..1) {
            for (iy in -1..1) {
                for (ix in -1..1) {
                    val vertexColors = VertexColors(6 * 4)
                    var vertexIndex = 0
                    val index = intArrayOf(ix, iy, iz)
                    var i = 0
                    while (i < geometry.vertex().size) {
                        for (coordinate in 0..2) {
                            if (index[coordinate] > 0 && geometry.vertex()[i] > 0 && geometry.normals()[i] > 0) {
                                vertexColors.setColor(
                                    vertexIndex,
                                    colors[coordinate][0].r,
                                    colors[coordinate][0].g,
                                    colors[coordinate][0].b,
                                    colors[coordinate][0].a
                                )
                            }
                            if (index[coordinate] < 0 && geometry.vertex()[i] < 0 && geometry.normals()[i] < 0) {
                                vertexColors.setColor(
                                    vertexIndex,
                                    colors[coordinate][0].r,
                                    colors[coordinate][1].g,
                                    colors[coordinate][1].b,
                                    colors[coordinate][1].a
                                )
                            }
                            ++i
                        }
                        ++vertexIndex
                    }
                    val piece = Object3d(geometry, material1, vertexColors)
                    piece.scale = 0.5f
                    piece.setPosition(ix * 1f, iy * 1f, iz * 1f)
                    cube3dObject.addChild(piece)
                }
            }
        }
        cube3dObject.setPosition(0f, 0f, 0f)
        cube3dObject.scale = 0.5f
        addChild(cube3dObject)
        initialTime = System.currentTimeMillis()

        var i = 0
        while (i < rotations.size) {
            rotations[i] = Vector3(axis[1])
            rotations[i + 1] = Vector3(axis[0])
            rotations[i + 2] = Vector3(axis[2])
            i += 3
        }

        for (j in positions.indices) {
            positions[j] = Vector3(0f, 0f, 0f)
        }
    }

    private fun rotate(axis: Int, angle: Float, v: Vector3) {
        v.rotateAxis(this.axis[axis], angle)
    }

    private fun rotate(axis: Int, angle: Float, direction: Vector3, up: Vector3, side: Vector3) {
        rotate(axis, angle, direction)
        rotate(axis, angle, side)
        rotate(axis, angle, up)
    }

    private val cubeDirectionVector = Vector3(0F, 1F, 0f)
    private val cubeSideVector = Vector3(1f, 0f, 0f)
    private val cubeUpVector = Vector3(0f, 0f, 1f)

    override fun update() {
        cubeDirectionVector.setAllFrom(axis[1])
        cubeSideVector.setAllFrom(axis[0])
        cubeUpVector.setAllFrom(axis[2])

        //rotate(2, Math.toRadians(-theta).toFloat(), cubeDirectionVector, cubeUpVector, cubeSideVector)
        //rotate(1, Math.toRadians(phi).toFloat(), cubeDirectionVector, cubeUpVector, cubeSideVector)

        cube3dObject.setRotationMatrix(cubeDirectionVector, cubeUpVector, cubeSideVector)

        if (nextRotationAxis == -1) {
            initialTimeMovement = System.currentTimeMillis()

            // decide next movement (axis, section and angle)
            nextRotationAxis = random.nextInt(3) // 0,1,2
            nextRotationSection = random.nextInt(3) - 1 // -1,0,+1
            var nextRotation: Int = random.nextInt(4) - 1
            if (nextRotation <= 0) {
                --nextRotation // -2,-1,+1,+2
            }
            nextRotationAngle = 90.0f * nextRotation // -180,-90,+90,+180
            rotationGroup.getChildren().clear()
            var i = 0
            var j = 0
            for (piece in cube3dObject.getChildren()) {
                if (abs(
                        Vector3.dot(
                            piece.position,
                            axis[nextRotationAxis]
                        ) - nextRotationSection
                    ) < 1e-2
                ) {
                    rotationGroup.addChild(piece)
                    piece.getRotationMatrix(rotations[i++], rotations[i++], rotations[i++])
                    positions[j++]!!.setAllFrom(piece.position)
                }
            }
        } else if (nextRotationAxis >= 0) {
            val step = (System.currentTimeMillis() - initialTimeMovement) / 3f
            var angle = step * sign(nextRotationAngle)
            if (abs(angle) >= abs(nextRotationAngle)) {
                angle = nextRotationAngle
            }
            var i = 0
            var j = 0
            for (piece in rotationGroup.getChildren()) {
                direction.setAllFrom(rotations[i++])
                up.setAllFrom(rotations[i++])
                side.setAllFrom(rotations[i++])
                rotate(
                    nextRotationAxis,
                    Math.toRadians(angle.toDouble()).toFloat(),
                    direction,
                    up,
                    side
                )
                piece.setRotationMatrix(direction, up, side)
                pos.setAllFrom(positions[j++])
                rotate(nextRotationAxis, Math.toRadians(angle.toDouble()).toFloat(), pos)
                piece.position = pos
            }
            if (angle == nextRotationAngle) {
                nextRotationAxis = -1
            }
        }
    }

    override fun onMoveScreen(deltaX: Double, deltaY: Double) {}
}