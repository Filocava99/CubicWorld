package it.filippocavallari.lwge

import it.filippocavallari.lwge.graphic.Mesh
import org.lwjgl.opengl.GL11C.*
import org.lwjgl.opengl.GL13C.GL_TEXTURE0
import org.lwjgl.opengl.GL13C.glActiveTexture
import org.lwjgl.opengl.GL30C.glBindVertexArray

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
    }

    private fun endRender() {
        glBindVertexArray(0)
        glBindTexture(GL_TEXTURE_2D, 0)
    }

    private fun clear() {
        glClear((GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT))
    }
}