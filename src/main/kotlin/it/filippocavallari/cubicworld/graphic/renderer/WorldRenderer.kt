package it.filippocavallari.cubicworld.graphic.renderer

import it.filippocavallari.lwge.GameEngine
import it.filippocavallari.lwge.Scene
import it.filippocavallari.lwge.graphic.Mesh
import it.filippocavallari.lwge.graphic.light.DirectionalLight
import it.filippocavallari.lwge.graphic.light.PointLight
import it.filippocavallari.lwge.renderer.Renderer
import org.joml.Vector3f
import org.joml.Vector4f
import org.lwjgl.opengl.GL13C.*
import org.lwjgl.opengl.GL30C.glBindVertexArray


class WorldRenderer(val scene: Scene) : Renderer{

    override fun render() {
        clear()
        val camera = scene.camera
        val shaderProgram = scene.shaderProgram
        shaderProgram.bind()
        shaderProgram.setUniform("cameraPos",camera.position)
        shaderProgram.setUniform("textureSampler", 0)
        shaderProgram.setUniform("projectionMatrix", GameEngine.projectionMatrix)
        shaderProgram.setUniform("pointLight", scene.pointLight)
        shaderProgram.setUniform("specularPower", 10f)
        shaderProgram.setUniform("normalMap", 1)
        shaderProgram.setUniform("depthMap", 2)
        shaderProgram.setUniform("fog",scene.fog)
        scene.directionalLight.run {
            val directionalLightPosition = Vector4f(direction, 0f).mul(camera.viewMatrix)
            val newDirectionalLight = DirectionalLight(
                color,
                Vector3f(directionalLightPosition.x, directionalLightPosition.y, directionalLightPosition.z),
                intensity
            )
            shaderProgram.setUniform("directionalLight", newDirectionalLight)
        }
        scene.pointLight.run {
            val pointLightPosition = Vector4f(position, 1f).mul(camera.viewMatrix)
            val newPointLight = PointLight(
                color,
                Vector3f(pointLightPosition.x, pointLightPosition.y, pointLightPosition.z),
                intensity,
                attenuation
            )
            shaderProgram.setUniform("pointLight", newPointLight)
        }
        scene.gameItems.forEach { entry ->
            val mesh = entry.key
            shaderProgram.setUniform("material", mesh.material)
            initRender(mesh)
            entry.value.forEach { gameItem ->
                val modelViewMatrix = gameItem.transformation.getModelViewMatrix(camera.viewMatrix)
                //shaderProgram.setUniform("modelMatrix", gameItem.transformation.getModelMatrix())
                shaderProgram.setUniform("modelViewMatrix", modelViewMatrix)
                glDrawElements(GL_TRIANGLES, mesh.vertices.size, GL_UNSIGNED_INT, 0)
            }
            endRender()
        }
        shaderProgram.unbind()
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
        mesh.material.depthMap?.let {
            glActiveTexture(GL_TEXTURE2)
            glBindTexture(GL_TEXTURE_2D, it.id)
        }
    }

    private fun endRender() {
        glBindVertexArray(0)
        glBindTexture(GL_TEXTURE_2D, 0)
    }

    override fun clear() {
        glClear((GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT))
    }
}