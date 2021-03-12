package it.filippocavallari.cubicworld.data.block

import org.joml.Vector3f

enum class FaceDirection(val normal: Vector3f) {
    UP(Vector3f(0f,1f,0f)),
    DOWN(Vector3f(0f,-1f,0f)),
    SOUTH(Vector3f(0f,0f,-1f)),
    NORTH(Vector3f(0f,0f,1f)),
    WEST(Vector3f(-1f,0f,0f)),
    EAST(Vector3f(1f,0f,0f));

}