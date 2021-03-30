package it.filippocavallari.lwge.graphic

import it.filippocavallari.lwge.data.Vao
import it.filippocavallari.lwge.data.Vbo
import kotlin.math.tan

class Mesh(var vertices: FloatArray, var indices: IntArray, var normals: FloatArray, var uvs: FloatArray?, var tangents: FloatArray?, val material: Material){
    var vao: Vao = Vao(0)
    val vboSet = HashSet<Vbo>()
    val textureVboSet = HashSet<Vbo>()

    fun clearArrays(){
        vertices = FloatArray(0)
        indices = IntArray(0)
        normals = FloatArray(0)
        uvs = null
        tangents = null
    }
}