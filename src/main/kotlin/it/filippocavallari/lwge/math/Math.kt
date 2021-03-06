package it.filippocavallari.lwge.math

import org.joml.Vector2f
import org.joml.Vector3f

object Math {

    fun calculateNormalTangents(pos1: Vector3f, pos2: Vector3f, pos3: Vector3f, uv1: Vector2f, uv2: Vector2f, uv3: Vector2f): NormalTangents{
        val edge1 = Vector3f(pos2).sub(pos1)
        val edge2 = Vector3f(pos3).sub(pos1)
        val deltaUV1 = Vector2f(uv2).sub(uv1)
        val deltaUV2 = Vector2f(uv3).sub(uv1)

        val f = 1.0f / (deltaUV1.x * deltaUV2.y - deltaUV2.x * deltaUV1.y)

        val tangent = Vector3f()
        tangent.x = f * (deltaUV2.y * edge1.x - deltaUV1.y * edge2.x)
        tangent.y = f * (deltaUV2.y * edge1.y - deltaUV1.y * edge2.y)
        tangent.z = f * (deltaUV2.y * edge1.z - deltaUV1.y * edge2.z)

        val biTangent = Vector3f()
        biTangent.x = f * (-deltaUV2.x * edge1.x + deltaUV1.x * edge2.x)
        biTangent.y = f * (-deltaUV2.x * edge1.y + deltaUV1.x * edge2.y)
        biTangent.z = f * (-deltaUV2.x * edge1.z + deltaUV1.x * edge2.z)

        return NormalTangents(tangent, biTangent)
    }

}