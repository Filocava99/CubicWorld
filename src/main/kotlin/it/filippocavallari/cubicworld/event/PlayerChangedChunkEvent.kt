package it.filippocavallari.cubicworld.event

import it.filippocavallari.lwge.event.Event
import org.joml.Vector2i

class PlayerChangedChunkEvent(val previousChunkPosition: Vector2i, val newChunkPosition: Vector2i):Event() {
}