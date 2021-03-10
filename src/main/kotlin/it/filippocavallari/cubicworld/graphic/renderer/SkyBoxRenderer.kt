package it.filippocavallari.cubicworld.graphic.renderer

import it.filippocavallari.lwge.GameEngine
import it.filippocavallari.lwge.Scene
import it.filippocavallari.lwge.renderer.Renderer
import org.lwjgl.opengl.GL13C
import org.lwjgl.opengl.GL30C.glBindVertexArray

class SkyBoxRenderer(val scene: Scene) : Renderer {
    override fun render() {
        //clear()
        val camera = scene.camera
        val skyBox = scene.skyBox
        val shaderProgram = skyBox.shaderProgram
        shaderProgram.bind()
        shaderProgram.setUniform("ambientLight", scene.ambientLight)
        shaderProgram.setUniform("projectionMatrix", GameEngine.projectionMatrix)
        shaderProgram.setUniform("modelViewMatrix", skyBox.getModelViewMatrix(camera))
        shaderProgram.setUniform("textureSampler",0)
        val mesh = skyBox.mesh
        glBindVertexArray(mesh.vao.id)
        mesh.material.texture?.let {
            GL13C.glActiveTexture(GL13C.GL_TEXTURE0)
            GL13C.glBindTexture(GL13C.GL_TEXTURE_2D, it.id)
        }
        GL13C.glDrawElements(GL13C.GL_TRIANGLES, mesh.vertexCount, GL13C.GL_UNSIGNED_INT, 0)
        glBindVertexArray(0)
        GL13C.glBindTexture(GL13C.GL_TEXTURE_2D, 0)
        shaderProgram.unbind()
    }

    override fun clear() {
        GL13C.glClear((GL13C.GL_COLOR_BUFFER_BIT or GL13C.GL_DEPTH_BUFFER_BIT))
    }
}