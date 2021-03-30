package it.filippocavallari.lwge.graphic.shader

import it.filippocavallari.lwge.graphic.Material
import org.joml.Matrix4f
import org.joml.Vector3f
import org.joml.Vector4f
import org.lwjgl.opengl.GL20.*
import org.lwjgl.system.MemoryStack
import java.util.*
import it.filippocavallari.lwge.graphic.light.PointLight
import it.filippocavallari.lwge.graphic.light.DirectionalLight
import it.filippocavallari.lwge.graphic.Fog


open class ShaderProgram {

    private var programId = 0
    private var uniforms: MutableMap<String, Int> = HashMap()
    private var vertexShaderId = 0
    private var fragmentShaderId = 0

    init {
        programId = glCreateProgram()
        uniforms = HashMap()
        if (programId == 0) {
            throw Exception("Could not create Shader")
        }
    }

    fun createVertexShader(shaderCode: String?) {
        vertexShaderId = createShader(shaderCode, GL_VERTEX_SHADER)
    }

    fun createFragmentShader(shaderCode: String?) {
        fragmentShaderId = createShader(shaderCode, GL_FRAGMENT_SHADER)
    }

    fun createUniform(uniformName: String) {
        val uniformLocation: Int = glGetUniformLocation(
            programId,
            uniformName
        )
        if (uniformLocation < 0) {
            throw Exception("Could not find uniform: $uniformName")
        }
        uniforms[uniformName] = uniformLocation
    }

    fun createMaterialUniform(uniformName: String) {
        createUniform("$uniformName.ambient")
        createUniform("$uniformName.diffuse")
        createUniform("$uniformName.specular")
        createUniform("$uniformName.hasTexture")
        createUniform("$uniformName.hasNormalMap")
        createUniform("$uniformName.hasDepthMap")
        createUniform("$uniformName.reflectance")
    }

    fun createPointLightUniform(uniformName: String) {
        createUniform("$uniformName.color")
        createUniform("$uniformName.position")
        createUniform("$uniformName.intensity")
        createUniform("$uniformName.attenuation.constant")
        createUniform("$uniformName.attenuation.linear")
        createUniform("$uniformName.attenuation.exponent")
    }

    fun createDirectionalLightUniform(uniformName: String) {
        createUniform("$uniformName.color")
        createUniform("$uniformName.direction")
        createUniform("$uniformName.intensity")
    }

    fun createFogUniform(uniformName: String) {
        createUniform("$uniformName.enabled")
        createUniform("$uniformName.color")
        createUniform("$uniformName.density")
    }

    fun setUniform(uniformName: String, value: Int) {
        glUniform1i(uniforms[uniformName]!!, value)
    }

    fun setUniform(uniformName: String, value: Matrix4f) {
        // Dump the matrix into a float buffer
        MemoryStack.stackPush().use {
            glUniformMatrix4fv(uniforms[uniformName]!!, false, value[it.mallocFloat(16)])
        }
    }

    fun setUniform(uniformName: String, material: Material) {
        setUniform("$uniformName.ambient", material.ambientColor)
        setUniform("$uniformName.diffuse", material.diffuseColor)
        setUniform("$uniformName.specular", material.specularColor)
        setUniform("$uniformName.hasTexture", if (material.isTextured()) 1 else 0)
        setUniform("$uniformName.hasNormalMap", if (material.hasNormalMap()) 1 else 0)
        setUniform("$uniformName.hasDepthMap", if (material.hasDepthMap()) 1 else 0)
        setUniform("$uniformName.reflectance", material.reflectance)
    }

    fun setUniform(uniformName: String, pointLight: PointLight) {
        setUniform("$uniformName.color", pointLight.color)
        setUniform("$uniformName.position", pointLight.position)
        setUniform("$uniformName.intensity", pointLight.intensity)
        val att = pointLight.attenuation
        setUniform("$uniformName.attenuation.constant", att.constant)
        setUniform("$uniformName.attenuation.linear", att.linear)
        setUniform("$uniformName.attenuation.exponent", att.exponent)
    }

    fun setUniform(uniformName: String, directionalLight: DirectionalLight) {
        setUniform("$uniformName.color", directionalLight.color)
        setUniform("$uniformName.direction", directionalLight.direction)
        setUniform("$uniformName.intensity", directionalLight.intensity)
    }

    fun setUniform(uniformName: String, value: Float) {
        glUniform1f(uniforms[uniformName]!!, value)
    }

    fun setUniform(uniformName: String, value: Vector3f) {
        glUniform3f(uniforms[uniformName]!!, value.x, value.y, value.z)
    }

    fun setUniform(uniformName: String, value: Vector4f) {
        glUniform4f(uniforms[uniformName]!!, value.x, value.y, value.z, value.w)
    }

    fun setUniform(uniformName: String, fog: Fog) {
        setUniform("$uniformName.enabled", if (fog.enabled) 1 else 0)
        setUniform("$uniformName.color", fog.color)
        setUniform("$uniformName.density", fog.density)
    }

    private fun createShader(shaderCode: String?, shaderType: Int): Int {
        val shaderId: Int = glCreateShader(shaderType)
        if (shaderId == 0) {
            throw Exception("Error creating shader. Type: $shaderType")
        }
        glShaderSource(shaderId, shaderCode)
        glCompileShader(shaderId)
        if (glGetShaderi(shaderId, GL_COMPILE_STATUS) == 0) {
            throw Exception("Error compiling Shader code: " + glGetShaderInfoLog(shaderId, 1024))
        }
        glAttachShader(programId, shaderId)
        return shaderId
    }

    fun link() {
        glLinkProgram(programId)
        if (glGetProgrami(programId, GL_LINK_STATUS) == 0) {
            throw Exception("Error linking Shader code: " + glGetProgramInfoLog(programId, 1024))
        }
        if (vertexShaderId != 0) {
            glDetachShader(programId, vertexShaderId)
        }
        if (fragmentShaderId != 0) {
            glDetachShader(programId, fragmentShaderId)
        }
    }

    fun validateProgram() {
        glValidateProgram(programId)
        if (glGetProgrami(programId, GL_VALIDATE_STATUS) == 0) {
            System.err.println("Warning validating Shader code: " + glGetProgramInfoLog(programId, 1024))
        }
    }


    fun bind() {
        glUseProgram(programId)
    }

    fun unbind() {
        glUseProgram(0)
    }

    fun cleanup() {
        unbind()
        if (programId != 0) {
            glDeleteProgram(programId)
        }
    }

    private inline fun MemoryStack.use(block: (MemoryStack) -> Unit) {
        block(this)
        //this.pop()
        MemoryStack.stackPop()
    }
}