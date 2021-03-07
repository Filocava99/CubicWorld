package it.filippocavallari.cubicworld.graphic.shader

import it.filippocavallari.lwge.Util
import it.filippocavallari.lwge.graphic.shader.ShaderProgram

class SkyBoxShader : ShaderProgram() {

    init{
        this.createVertexShader(Util.loadResource("src/main/resources/shaders/skybox.vert"))
        this.createFragmentShader(Util.loadResource("src/main/resources/shaders/skybox.frag"))
        this.link()
        this.validateProgram()
        this.createUniform("textureSampler")
        this.createUniform("projectionMatrix");
        this.createUniform("modelViewMatrix");
        this.createUniform("ambientLight");
    }

}