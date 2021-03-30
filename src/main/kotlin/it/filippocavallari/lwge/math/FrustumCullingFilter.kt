package it.filippocavallari.lwge.math

import org.joml.FrustumIntersection
import org.joml.Matrix4f

object FrustumCullingFilter {

    private var prjViewMatrix = Matrix4f()
    private var frustumInt = FrustumIntersection()

    fun updateFrustum(projMatrix: Matrix4f, viewMatrix: Matrix4f) {
        // Calculate projection view matrix
        prjViewMatrix.set(projMatrix)
        prjViewMatrix.mul(viewMatrix)
        // Update frustum intersection class
        frustumInt.set(prjViewMatrix)
    }

    fun insideFrustum(x0: Float, y0: Float, z0: Float, boundingRadius: Float): Boolean {
        return frustumInt.testSphere(x0, y0, z0, boundingRadius)
    }
}