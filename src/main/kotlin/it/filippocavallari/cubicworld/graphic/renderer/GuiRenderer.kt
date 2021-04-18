package it.filippocavallari.cubicworld.graphic.renderer

import it.filippocavallari.cubicworld.graphic.shader.GuiShader
import it.filippocavallari.lwge.GameEngine
import it.filippocavallari.lwge.graphic.gui.GuiScene
import it.filippocavallari.lwge.graphic.shader.ShaderProgram
import it.filippocavallari.lwge.renderer.Renderer
import org.joml.Matrix4f
import org.joml.Vector4f
import org.lwjgl.opengl.GL11C.*
import org.lwjgl.opengl.GL13C
import org.lwjgl.opengl.GL13C.glActiveTexture
import org.lwjgl.opengl.GL30C

class GuiRenderer(val scene: GuiScene) : Renderer{

    val shaderProgram:ShaderProgram

    init {
        shaderProgram = GuiShader()
    }

    override fun render() {
        shaderProgram.bind()
        shaderProgram.setUniform("textureSampler",0)
        shaderProgram.setUniform("color",Vector4f(0f,0f,0f,0f))
        initRender()
        scene.entities.forEach { guiEntity ->
            guiEntity.vao?.let { vao ->
                glBindTexture(GL_TEXTURE_2D, guiEntity.texture.id)
                shaderProgram.setUniform("projModelMatrix",Matrix4f(guiEntity.transformation.getModelMatrix()).mul(GameEngine.projectionMatrix))
                GL30C.glBindVertexArray(vao.id)
                glDrawElements(GL_TRIANGLES, mesh.vertices.size, GL_UNSIGNED_INT, 0)
            }
        }
        endRender()
    }

    override fun clear() {
        TODO("Not yet implemented")
    }

    private fun initRender(){
        glEnable(GL_BLEND)
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
        glActiveTexture(GL13C.GL_TEXTURE0)
    }

    private fun endRender(){
        glDisable(GL_BLEND)
    }

}