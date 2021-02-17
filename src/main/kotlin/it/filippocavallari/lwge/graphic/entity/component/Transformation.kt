package it.filippocavallari.lwge.graphic.entity.component

import it.filippocavallari.lwge.graphic.entity.Entity
import org.joml.Matrix4f

class Transformation(private val entity: Entity) {
    private val modelViewMatrix = Matrix4f()

    fun getModelViewMatrix(viewMatrix: Matrix4f): Matrix4f {
        val rotation = entity.rotation
        modelViewMatrix.set(viewMatrix).translate(entity.position).rotateX(
            Math.toRadians(-rotation.x.toDouble())
                .toFloat()
        ).rotateY(Math.toRadians(-rotation.y.toDouble()).toFloat()).rotateZ(
            Math.toRadians(-rotation.z.toDouble())
                .toFloat()
        ).scale(entity.scale)
        return modelViewMatrix
    }

}