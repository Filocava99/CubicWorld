package it.filippocavallari.lwge.graphic.entity.component

import org.joml.Vector3f
import java.util.*

class FrustumFilter(val anchors: List<Vector3f> = LinkedList(), var radius: Float = 0f, var ignoreFrustum: Boolean = false) {

    var insideFrustum = false

}