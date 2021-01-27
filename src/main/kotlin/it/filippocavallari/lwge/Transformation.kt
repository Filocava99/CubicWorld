package it.filippocavallari.lwge

import org.joml.Matrix4f

class Transformation(private val modelViewMatrix: Matrix4f = Matrix4f()) {

    fun getModelViewMatrix(gameItem: GameItem, viewMatrix: Matrix4f): Matrix4f {
        val rotation = gameItem.rotation
        modelViewMatrix.set(viewMatrix).translate(gameItem.position).rotateX(
            Math.toRadians(-rotation.x.toDouble())
                .toFloat()
        ).rotateY(Math.toRadians(-rotation.y.toDouble()).toFloat()).rotateZ(
            Math.toRadians(-rotation.z.toDouble())
                .toFloat()
        ).scale(gameItem.scale)
        return modelViewMatrix
    }

}