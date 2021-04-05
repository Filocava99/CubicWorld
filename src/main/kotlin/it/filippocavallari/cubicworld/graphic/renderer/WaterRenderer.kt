package it.filippocavallari.cubicworld.graphic.renderer

import it.filippocavallari.lwge.GameEngine
import it.filippocavallari.lwge.Scene
import it.filippocavallari.lwge.graphic.Mesh
import it.filippocavallari.lwge.graphic.Texture
import it.filippocavallari.lwge.graphic.shader.ShaderProgram
import it.filippocavallari.lwge.graphic.water.WaterFrameBuffers
import it.filippocavallari.lwge.loader.TextureLoader
import it.filippocavallari.lwge.renderer.Renderer
import org.lwjgl.opengl.GL11C
import org.lwjgl.opengl.GL13C
import org.lwjgl.opengl.GL30C

@Suppress("SpellCheckingInspection")
class WaterRenderer(val shaderProgram: ShaderProgram, val scene: Scene, val waterFrameBuffers: WaterFrameBuffers) : Renderer {

    private val waveSpeed = 0.03f
    private var moveFactor = 0f
    var interval = 0f
    private val camera = scene.camera
    private val entities = scene.waterEntities
    private val dudvTexture: Texture = TextureLoader.createTexture("src/main/resources/textures/water/water_dudv.png")!!
    private val normalMap: Texture = TextureLoader.createTexture("src/main/resources/textures/water/water_normal.png")!!

    override fun render() {
        moveFactor += waveSpeed * interval
        shaderProgram.bind()
        shaderProgram.setUniform("reflectionTexture", 0)
        shaderProgram.setUniform("refractionTexture", 1)
        shaderProgram.setUniform("dudvMap",2)
        shaderProgram.setUniform("normalMap",3)
        shaderProgram.setUniform("moveFactor",moveFactor)
        shaderProgram.setUniform("projectionMatrix", GameEngine.projectionMatrix)
        shaderProgram.setUniform("cameraPosition", camera.position)
        GL11C.glEnable(GL30C.GL_CLIP_DISTANCE0)
        entities.forEach { entry ->
            val mesh = entry.key
            initRender(mesh)
            entry.value.forEach { gameItem ->
                val modelViewMatrix = gameItem.transformation.getModelViewMatrix(camera.viewMatrix)
                shaderProgram.setUniform("modelViewMatrix", modelViewMatrix)
                shaderProgram.setUniform("modelMatrix", gameItem.transformation.getModelMatrix())
                GL13C.glDrawElements(GL13C.GL_TRIANGLES, mesh.vertices.size, GL13C.GL_UNSIGNED_INT, 0)
            }
            endRender()
        }
        shaderProgram.unbind()
    }

    private fun initRender(mesh: Mesh) {
        GL30C.glBindVertexArray(mesh.vao.id)
        GL13C.glActiveTexture(GL13C.GL_TEXTURE0)
        GL13C.glBindTexture(GL13C.GL_TEXTURE_2D, waterFrameBuffers.reflectionTexture)
        GL13C.glActiveTexture(GL13C.GL_TEXTURE1)
        GL13C.glBindTexture(GL13C.GL_TEXTURE_2D, waterFrameBuffers.refractionTexture)
        GL13C.glActiveTexture(GL13C.GL_TEXTURE2)
        GL13C.glBindTexture(GL13C.GL_TEXTURE_2D, dudvTexture.id)
        GL13C.glActiveTexture(GL13C.GL_TEXTURE3)
        GL13C.glBindTexture(GL13C.GL_TEXTURE_2D, normalMap.id)
    }

    private fun endRender() {
        GL30C.glBindVertexArray(0)
    }

    override fun clear() {
        TODO("Not yet implemented")
    }
}
