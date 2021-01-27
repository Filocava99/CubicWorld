package it.filippocavallari.lwge

import it.filippocavallari.lwge.graphic.Mesh

import org.joml.Vector3f

class GameItem(var mesh: Mesh) {
    var position = Vector3f()
    var scale = 1f
    var rotation = Vector3f()
    var insideFrustum = false
    var ignoreFrustum = false

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