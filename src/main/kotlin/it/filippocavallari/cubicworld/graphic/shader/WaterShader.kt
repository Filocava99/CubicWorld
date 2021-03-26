package it.filippocavallari.cubicworld.graphic.shader

import it.filippocavallari.lwge.Util
import it.filippocavallari.lwge.graphic.shader.ShaderProgram

class WaterShader : ShaderProgram() {

    init{
        this.createVertexShader(Util.loadResource("src/main/resources/shaders/water.vert"))
        this.createFragmentShader(Util.loadResource("src/main/resources/shaders/water.frag"))
        this.link()
        this.validateProgram()
        this.createUniform("reflectionTexture")
        this.createUniform("refractionTexture")
        this.createUniform("projectionMatrix")
        this.createUniform("modelViewMatrix")
        this.createUniform("dudvMap")
        this.createUniform("normalMap")
        this.createUniform("moveFactor")
        this.createUniform("modelMatrix")
        this.createUniform("cameraPosition")
    }
}