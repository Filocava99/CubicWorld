package it.filippocavallari.lwge

import org.joml.Matrix4f
import org.joml.Vector3f
import kotlin.math.cos
import kotlin.math.sin

class Camera(val position: Vector3f = Vector3f(0f, 0f, 0f), val rotation: Vector3f = Vector3f(0f, 0f, 0f)) {
    private val cameraStep = 0.1f
    private val preparedMovement : Vector3f = Vector3f()

    val viewMatrix = Matrix4f()

    fun setPosition(x: Float, y: Float, z: Float) {
        position.x = x
        position.y = y
        position.z = z
        updateViewMatrix()
    }

    fun setRotation(x: Float, y: Float, z: Float) {
        rotation.x = x
        rotation.y = y
        rotation.z = z
        updateViewMatrix()
    }

    fun rotate(x: Float, y: Float, z: Float){
        rotation.x += x
        rotation.y += y
        rotation.z += z
        updateViewMatrix()
    }

    fun prepareMovement(offsetX: Float, offsetY: Float, offsetZ: Float){
        preparedMovement.x += offsetX
        preparedMovement.y += offsetY
        preparedMovement.z += offsetZ
    }

    fun movePosition() {
        if (preparedMovement.z != 0f) {
            position.x += sin(Math.toRadians(rotation.y.toDouble())).toFloat() * -cameraStep * preparedMovement.z
            position.z += cos(Math.toRadians(rotation.y.toDouble())).toFloat() * cameraStep * preparedMovement.z
        }
        if (preparedMovement.x != 0f) {
            position.x += sin(Math.toRadians((rotation.y - 90).toDouble())).toFloat() * -cameraStep * preparedMovement.x
            position.z += cos(Math.toRadians((rotation.y - 90).toDouble())).toFloat() * cameraStep * preparedMovement.x
        }
        position.y += cameraStep * preparedMovement.y
        preparedMovement.x = 0f
        preparedMovement.y = 0f
        preparedMovement.z = 0f
        updateViewMatrix()
    }

    private fun updateViewMatrix(){
        // First do the rotation so camera rotates over its position
        viewMatrix.rotationX(Math.toRadians(rotation.x.toDouble()).toFloat())
            .rotateY(Math.toRadians(rotation.y.toDouble()).toFloat())
            .translate(-position.x, -position.y, -position.z)
    }
}