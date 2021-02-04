package it.filippocavallari.lwge

import it.filippocavallari.lwge.graphic.Mesh
import org.lwjgl.opengl.GL11C.*
import org.lwjgl.opengl.GL30C.glBindVertexArray
import org.joml.Vector3f
import org.joml.Vector3fc
import org.joml.Vector4f
import org.lwjgl.opengl.GL13C.*


class Renderer(val scene: Scene) {

    fun render() {
        clear()
        val camera = scene.camera
        scene.gameItems.forEach { entry ->
            val mesh = entry.key
            val shaderProgram = mesh.shaderProgram
            shaderProgram.bind()
            shaderProgram.setUniform("texture_sampler", 0)
            shaderProgram.setUniform("projectionMatrix", GameEngine.projectionMatrix)
            shaderProgram.setUniform("material",mesh.material)
            var tempPos = Vector4f(scene.pointLight.position,1f).mul(camera.viewMatrix)
            scene.pointLight.position = Vector3f(tempPos.x,tempPos.y,tempPos.z)
            shaderProgram.setUniform("pointLight",scene.pointLight)
            shaderProgram.setUniform("ambientLight", Vector3f(0.3f, 0.3f, 0.3f))
            shaderProgram.setUniform("specularPower", 10f)
            shaderProgram.setUniform("normalMap", 1);
            tempPos = Vector4f(scene.directionalLight.direction,1f).mul(camera.viewMatrix)
            scene.directionalLight.direction = Vector3f(tempPos.x,tempPos.y,tempPos.z)
            shaderProgram.setUniform("directionalLight",scene.directionalLight)
            initRender(mesh)
            entry.value.forEach { gameItem ->
                val modelViewMatrix = gameItem.transformation.getModelViewMatrix(camera.viewMatrix)
                shaderProgram.setUniform("modelViewMatrix", modelViewMatrix)
                glDrawElements(GL_TRIANGLES, mesh.vertexCount, GL_UNSIGNED_INT, 0)
            }
            endRender()
            shaderProgram.unbind()
        }
    }

    private fun initRender(mesh: Mesh) {
        glBindVertexArray(mesh.vao.id)
        mesh.material.texture?.let {
            glActiveTexture(GL_TEXTURE0)
            glBindTexture(GL_TEXTURE_2D, it.id)
        }
        mesh.material.normalMap?.let {
            glActiveTexture(GL_TEXTURE1)
            glBindTexture(GL_TEXTURE_2D, it.id)
        }
    }

    private fun endRender() {
        glBindVertexArray(0)
        glBindTexture(GL_TEXTURE_2D, 0)
    }

    private fun clear() {
        glClear((GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT))
    }
}