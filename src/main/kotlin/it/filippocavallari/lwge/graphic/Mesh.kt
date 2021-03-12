package it.filippocavallari.lwge.graphic

import it.filippocavallari.lwge.data.Vao
import it.filippocavallari.lwge.data.Vbo

class Mesh(val vertices: FloatArray, val indices: IntArray, val normals: FloatArray, val uvs: FloatArray?, val tangents: FloatArray?, val material: Material){
    var vao: Vao = Vao(0)
    val vboSet = HashSet<Vbo>()
    val textureVboSet = HashSet<Vbo>()
}