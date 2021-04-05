package it.filippocavallari.cubicworld.event

import it.filippocavallari.lwge.event.Event
import org.joml.Vector3f

class PlayerMoveEvent(val previousPosition: Vector3f, val newPosition: Vector3f): Event() {
}