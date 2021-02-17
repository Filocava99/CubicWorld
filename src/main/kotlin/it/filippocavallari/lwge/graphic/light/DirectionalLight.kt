package it.filippocavallari.lwge.graphic.light

import org.joml.Vector3f

class DirectionalLight(val color: Vector3f, var direction: Vector3f, val intensity: Float){

    constructor(directionalLight: DirectionalLight) : this(directionalLight.color, directionalLight.direction, directionalLight.intensity)

}
