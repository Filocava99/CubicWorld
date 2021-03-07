package it.filippocavallari.lwge.graphic.entity

import it.filippocavallari.lwge.graphic.Mesh
import it.filippocavallari.lwge.graphic.entity.component.Transformation

open class Entity(open val mesh: Mesh) {

    var insideFrustum = false
    var ignoreFrustum = false
    val transformation = Transformation()

}