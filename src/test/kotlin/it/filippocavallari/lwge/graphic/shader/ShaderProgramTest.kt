package it.filippocavallari.lwge.graphic.shader

import it.filippocavallari.lwge.Util
import it.filippocavallari.lwge.Window
import it.filippocavallari.lwge.graphic.Material
import it.filippocavallari.lwge.graphic.light.DirectionalLight
import it.filippocavallari.lwge.graphic.light.PointLight
import org.joml.Matrix4f
import org.joml.Vector3f
import org.joml.Vector4f
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class ShaderProgramTest {

    private val vertexShader = Util.loadResource("src/test/resources/shaders/shaderprogramtest/shader.vert")
    private val fragmentShader = Util.loadResource("src/test/resources/shaders/shaderprogramtest/shader.frag")
/*
    @Test
    fun createVertexShader() {
        val window = Window("test",100,100)
        window.showWindow(false)
        val shaderProgram = ShaderProgram()
        assertDoesNotThrow{
            shaderProgram.createVertexShader(vertexShader)
        }
    }

    @Test
    fun createFragmentShader() {
        val window = Window("test",100,100)
        window.showWindow(false)
        val shaderProgram = ShaderProgram()
        assertDoesNotThrow{
            shaderProgram.createFragmentShader(fragmentShader)
        }
    }

    @Test
    fun link() {
        val window = Window("test",100,100)
        window.showWindow(false)
        val shaderProgram = ShaderProgram()
        shaderProgram.createVertexShader(vertexShader)
        shaderProgram.createFragmentShader(fragmentShader)
        assertDoesNotThrow { shaderProgram.link() }
    }

    @Test
    fun validateProgram() {
        val window = Window("test",100,100)
        window.showWindow(false)
        val shaderProgram = ShaderProgram()
        shaderProgram.createVertexShader(vertexShader)
        shaderProgram.createFragmentShader(fragmentShader)
        shaderProgram.link()
        assertDoesNotThrow { shaderProgram.validateProgram() }
    }

    @Test
    fun bind() {
        val window = Window("test",100,100)
        window.showWindow(false)
        val shaderProgram = ShaderProgram()
        shaderProgram.createVertexShader(vertexShader)
        shaderProgram.createFragmentShader(fragmentShader)
        shaderProgram.link()
        shaderProgram.validateProgram()
        assertDoesNotThrow { shaderProgram.bind() }
    }

    @Test
    fun unbind() {
        val window = Window("test",100,100)
        window.showWindow(false)
        val shaderProgram = ShaderProgram()
        shaderProgram.createVertexShader(vertexShader)
        shaderProgram.createFragmentShader(fragmentShader)
        shaderProgram.link()
        shaderProgram.validateProgram()
        shaderProgram.bind()
        assertDoesNotThrow { shaderProgram.unbind() }
    }

    @Test
    fun cleanup() {
        val window = Window("test",100,100)
        window.showWindow(false)
        val shaderProgram = ShaderProgram()
        shaderProgram.createVertexShader(vertexShader)
        shaderProgram.createFragmentShader(fragmentShader)
        shaderProgram.link()
        shaderProgram.validateProgram()
        assertDoesNotThrow { shaderProgram.cleanup() }
    }

    @Test
    fun createUniform() {
        val window = Window("test",100,100)
        window.showWindow(false)
        val shaderProgram = ShaderProgram()
        shaderProgram.createVertexShader(vertexShader)
        shaderProgram.createFragmentShader(fragmentShader)
        shaderProgram.link()
        shaderProgram.validateProgram()
        assertDoesNotThrow { shaderProgram.createUniform("normalMap") }
    }

    @Test
    fun createMaterialUniform() {
        val window = Window("test",100,100)
        window.showWindow(false)
        val shaderProgram = ShaderProgram()
        shaderProgram.createVertexShader(vertexShader)
        shaderProgram.createFragmentShader(fragmentShader)
        shaderProgram.link()
        shaderProgram.validateProgram()
        assertDoesNotThrow { shaderProgram.createMaterialUniform("material") }
    }

    @Test
    fun createPointLightUniform() {
        val window = Window("test",100,100)
        window.showWindow(false)
        val shaderProgram = ShaderProgram()
        shaderProgram.createVertexShader(vertexShader)
        shaderProgram.createFragmentShader(fragmentShader)
        shaderProgram.link()
        shaderProgram.validateProgram()
        assertDoesNotThrow { shaderProgram.createPointLightUniform("pointLight") }
    }

    @Test
    fun createDirectionalLightUniform() {
        val window = Window("test",100,100)
        window.showWindow(false)
        val shaderProgram = ShaderProgram()
        shaderProgram.createVertexShader(vertexShader)
        shaderProgram.createFragmentShader(fragmentShader)
        shaderProgram.link()
        shaderProgram.validateProgram()
        assertDoesNotThrow { shaderProgram.createDirectionalLightUniform("directionalLight") }
    }

    @Test
    fun setIntUniform() {
        val window = Window("test",100,100)
        window.showWindow(false)
        val shaderProgram = ShaderProgram()
        shaderProgram.createVertexShader(vertexShader)
        shaderProgram.createFragmentShader(fragmentShader)
        shaderProgram.link()
        shaderProgram.validateProgram()
        shaderProgram.createUniform("normalMap")
        shaderProgram.bind()
        assertDoesNotThrow {
            shaderProgram.setUniform("normalMap",1)
        }
        shaderProgram.unbind()
    }

    @Test
    fun setMatrix4fUniform() {
        val window = Window("test",100,100)
        window.showWindow(false)
        val shaderProgram = ShaderProgram()
        shaderProgram.createVertexShader(vertexShader)
        shaderProgram.createFragmentShader(fragmentShader)
        shaderProgram.link()
        shaderProgram.validateProgram()
        shaderProgram.createUniform("projectionMatrix")
        shaderProgram.bind()
        assertDoesNotThrow {
            shaderProgram.setUniform("projectionMatrix", Matrix4f())
        }
        shaderProgram.unbind()
    }

    @Test
    fun setMaterialUniform() {
        val window = Window("test",100,100)
        window.showWindow(false)
        val shaderProgram = ShaderProgram()
        shaderProgram.createVertexShader(vertexShader)
        shaderProgram.createFragmentShader(fragmentShader)
        shaderProgram.link()
        shaderProgram.validateProgram()
        shaderProgram.createMaterialUniform("material")
        shaderProgram.bind()
        assertDoesNotThrow {
            shaderProgram.setUniform("material", Material(null,null,null,reflectance = 0f))
        }
        shaderProgram.unbind()
    }

    @Test
    fun setPointLightUniform() {
        val window = Window("test",100,100)
        window.showWindow(false)
        val shaderProgram = ShaderProgram()
        shaderProgram.createVertexShader(vertexShader)
        shaderProgram.createFragmentShader(fragmentShader)
        shaderProgram.link()
        shaderProgram.validateProgram()
        shaderProgram.createPointLightUniform("pointLight")
        shaderProgram.bind()
        assertDoesNotThrow {
            shaderProgram.setUniform("pointLight", PointLight(Vector3f(1f,1f,1f), Vector3f(10000f,0f,10f),1f))
        }
        shaderProgram.unbind()
    }

    @Test
    fun setDirectionalLightUniform() {
        val window = Window("test",100,100)
        window.showWindow(false)
        val shaderProgram = ShaderProgram()
        shaderProgram.createVertexShader(vertexShader)
        shaderProgram.createFragmentShader(fragmentShader)
        shaderProgram.link()
        shaderProgram.validateProgram()
        shaderProgram.createDirectionalLightUniform("directionalLight")
        shaderProgram.bind()
        assertDoesNotThrow {
            shaderProgram.setUniform("directionalLight", DirectionalLight(Vector3f(1f,1f,1f), Vector3f(0.5f, -1f,0f),1f))
        }
        shaderProgram.unbind()
    }

    @Test
    fun setFloatUniform() {
        val window = Window("test",100,100)
        window.showWindow(false)
        val shaderProgram = ShaderProgram()
        shaderProgram.createVertexShader(vertexShader)
        shaderProgram.createFragmentShader(fragmentShader)
        shaderProgram.link()
        shaderProgram.validateProgram()
        shaderProgram.createUniform("specularPower")
        shaderProgram.bind()
        assertDoesNotThrow {
            shaderProgram.setUniform("specularPower", 1f)
        }
        shaderProgram.unbind()
    }

    @Test
    fun setVector3fUniform() {
        val window = Window("test",100,100)
        window.showWindow(false)
        val shaderProgram = ShaderProgram()
        shaderProgram.createVertexShader(vertexShader)
        shaderProgram.createFragmentShader(fragmentShader)
        shaderProgram.link()
        shaderProgram.validateProgram()
        shaderProgram.createUniform("ambientLight")
        shaderProgram.bind()
        assertDoesNotThrow {
            shaderProgram.setUniform("ambientLight", Vector3f())
        }
        shaderProgram.unbind()
    }

    @Test
    fun setVector4fUniform() {
        val window = Window("test",100,100)
        window.showWindow(false)
        val shaderProgram = ShaderProgram()
        shaderProgram.createVertexShader(vertexShader)
        shaderProgram.createFragmentShader(fragmentShader)
        shaderProgram.link()
        shaderProgram.validateProgram()
        shaderProgram.createUniform("ambientColorBis")
        shaderProgram.bind()
        assertDoesNotThrow {
            shaderProgram.setUniform("ambientColorBis", Vector4f())
        }
        shaderProgram.unbind()
    }
    */
}