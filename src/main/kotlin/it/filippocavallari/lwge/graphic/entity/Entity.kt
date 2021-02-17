package it.filippocavallari.lwge.graphic.entity

import it.filippocavallari.lwge.graphic.entity.component.Transformation

import org.joml.Vector3f

class Entity {
    var position = Vector3f()
    var scale = 1f
    var rotation = Vector3f()
    var insideFrustum = false
    var ignoreFrustum = false
    val transformation = Transformation(this)

    fun setPosition(x: Float, y: Float, z: Float) {
        position.x = x
        position.y = y
        position.z = z
    }

    fun setRotation(x: Float, y: Float, z: Float) {
        rotation.x = x
        rotation.y = y
        rotation.z = z
    }
}