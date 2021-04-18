package it.filippocavallari.lwge.graphic.gui

import it.filippocavallari.lwge.data.Vao
import it.filippocavallari.lwge.graphic.Texture
import it.filippocavallari.lwge.graphic.entity.component.Transformation

class GuiEntity(val vertices: FloatArray, val uvs: FloatArray, val texture: Texture) {
    val transformation = Transformation()
    var vao: Vao? = null
}