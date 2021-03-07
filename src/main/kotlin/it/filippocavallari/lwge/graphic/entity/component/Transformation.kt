package it.filippocavallari.lwge.graphic.entity.component

import org.joml.Matrix4f
import org.joml.Vector3f

class Transformation() {
    private val modelViewMatrix = Matrix4f()

    var position = Vector3f()
    var scale = 1f
    var rotation = Vector3f()

    fun getModelViewMatrix(viewMatrix: Matrix4f): Matrix4f {
        modelViewMatrix.set(viewMatrix).translate(position).rotateX(
            Math.toRadians(-rotation.x.toDouble())
                .toFloat()
        ).rotateY(Math.toRadians(-rotation.y.toDouble()).toFloat()).rotateZ(
            Math.toRadians(-rotation.z.toDouble())
                .toFloat()
        ).scale(scale)
        return modelViewMatrix
    }

    fun getModelMatrix(): Matrix4f{
        return Matrix4f().translate(position).rotateX(
            Math.toRadians(-rotation.x.toDouble())
                .toFloat()
        ).rotateY(Math.toRadians(-rotation.y.toDouble()).toFloat()).rotateZ(
            Math.toRadians(-rotation.z.toDouble())
                .toFloat()
        ).scale(scale)
    }

    fun setPosition(x: Float, y: Float, z: Float) {
        position.x = x
        position.y = y
        position.z = z
    }

    fun setRotation(x: Float, y: Float, z: Float) {
        rotation.x = x
        rotation.y = y
        rotation.z = z
    }

}