package it.filippocavallari.cubicworld.graphic.shader

import it.filippocavallari.lwge.Util
import it.filippocavallari.lwge.graphic.shader.ShaderProgram

class BasicShader : ShaderProgram() {
    init{
        this.createVertexShader(Util.loadResource("src/main/resources/shader.vert"))
        this.createFragmentShader(Util.loadResource("src/main/resources/shader.frag"))
        this.link()
        this.validateProgram()
        this.createUniform("textureSampler")
        this.createUniform("normalMap")
        this.createUniform("modelViewMatrix")
        this.createUniform("projectionMatrix")
        this.createMaterialUniform("material")
        this.createPointLightUniform("pointLight")
        this.createDirectionalLightUniform("directionalLight")
        this.createUniform("ambientLight")
        this.createUniform("specularPower")
    }
}