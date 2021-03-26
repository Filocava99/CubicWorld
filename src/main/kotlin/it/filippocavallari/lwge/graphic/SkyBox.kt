package it.filippocavallari.lwge.graphic

import it.filippocavallari.lwge.graphic.entity.Camera
import it.filippocavallari.lwge.graphic.entity.Entity
import it.filippocavallari.lwge.graphic.shader.ShaderProgram
import org.joml.Matrix4f

class SkyBox(override val mesh: Mesh, val shaderProgram: ShaderProgram) : Entity(mesh){

    fun getModelViewMatrix(camera: Camera): Matrix4f{
        val viewMatrix = Matrix4f(camera.viewMatrix)
        viewMatrix.m30(0f)
        viewMatrix.m31(0f)
        viewMatrix.m32(0f)
        transformation.run {
            viewMatrix.translate(position).rotateX(
                Math.toRadians(-rotation.x.toDouble())
                    .toFloat()
            ).rotateY(Math.toRadians(-rotation.y.toDouble()).toFloat()).rotateZ(
                Math.toRadians(-rotation.z.toDouble())
                    .toFloat()
            ).scale(scale)
        }
        return viewMatrix
    }

}