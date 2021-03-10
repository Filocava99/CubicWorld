package it.filippocavallari.cubicworld.graphic.shader

import it.filippocavallari.lwge.Util
import it.filippocavallari.lwge.graphic.shader.ShaderProgram

class BasicShader : ShaderProgram() {
    init{
        this.createVertexShader(Util.loadResource("src/main/resources/shaders/shader.vert"))
        this.createFragmentShader(Util.loadResource("src/main/resources/shaders/shader.frag"))
        this.link()
        this.validateProgram()
        this.createUniform("cameraPos")
        this.createUniform("textureSampler")
        this.createUniform("normalMap")
        this.createUniform("depthMap")
        this.createUniform("modelViewMatrix")
        this.createUniform("projectionMatrix")
        this.createMaterialUniform("material")
        this.createPointLightUniform("pointLight")
        this.createDirectionalLightUniform("directionalLight")
        this.createUniform("ambientLight")
        this.createUniform("specularPower")
        this.createFogUniform("fog")
        //this.createUniform("modelMatrix")
    }
}