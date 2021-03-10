package it.filippocavallari.lwge.math

import org.joml.Vector3f
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class MathTest {

    @Test
    fun calculateSurfaceNormal() {
        assertEquals(Vector3f(0f,1f,0f),Math.calculateSurfaceNormal(Vector3f(0f,0f,0f), Vector3f(0.5f,0f,1f),Vector3f(1f,0f,0f)))
    }
}