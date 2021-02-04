package it.filippocavallari.lwge.graphic

import org.joml.Vector3f


class PointLight(
    val color: Vector3f,
    var position: Vector3f,
    val intensity: Float,
    val attenuation: Attenuation = Attenuation(1f, 0f, 0f)
) {

    class Attenuation(var constant: Float, var linear: Float, var exponent: Float)
}