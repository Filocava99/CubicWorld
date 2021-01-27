package it.filippocavallari.lwge

import org.joml.Matrix4f
import org.joml.Vector3f

class Camera(val position: Vector3f = Vector3f(0f, 0f, 0f), val rotation: Vector3f = Vector3f(0f, 0f, 0f)) {
    private val cameraStep = 1f
    private val cameraMovement = Vector3f()
    private val transformation = Transformation()
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

    private fun updateViewMatrix(){
        // First do the rotation so camera rotates over its position
        viewMatrix.rotationX(Math.toRadians(rotation.x.toDouble()).toFloat())
            .rotateY(Math.toRadians(rotation.y.toDouble()).toFloat())
            .translate(-position.x, -position.y, -position.z)
    }
}