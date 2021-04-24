package it.filippocavallari.cubicworld.graphic.shader

import it.filippocavallari.lwge.Util
import it.filippocavallari.lwge.graphic.shader.ShaderProgram

class GuiShader : ShaderProgram(){
    init{
        this.createVertexShader(Util.loadResource("src/main/resources/shaders/gui.vert"))
        this.createFragmentShader(Util.loadResource("src/main/resources/shaders/gui.frag"))
        this.link()
        this.validateProgram()
        this.createUniform("projModelMatrix")
        this.createUniform("textureSampler")
        this.createUniform("color")
    }
}