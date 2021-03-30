package it.filippocavallari.lwge.graphic.entity

import it.filippocavallari.lwge.graphic.Mesh
import it.filippocavallari.lwge.graphic.entity.component.FrustumFilter
import it.filippocavallari.lwge.graphic.entity.component.Transformation
import org.joml.Vector3f

open class Entity(open val mesh: Mesh, val frustumFilter: FrustumFilter = FrustumFilter()) {

    val transformation = Transformation()
}