package it.filippocavallari.lwge.graphic

import it.filippocavallari.lwge.graphic.Texture
import org.joml.Vector4f

data class Material(
    val texture: Texture?,
    val ambientColor: Vector4f = Vector4f(255.0f, 255.0f, 255.0f, 1.0f),
    val diffuseColor: Vector4f = Vector4f(255.0f, 255.0f, 255.0f, 1.0f),
    val specularColor: Vector4f = Vector4f(255.0f, 255.0f, 255.0f, 1.0f),
    val reflectance: Float
) {
    fun isTextured(): Boolean {
        return texture != null
    }
}