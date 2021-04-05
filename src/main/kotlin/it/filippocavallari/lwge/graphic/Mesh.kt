package it.filippocavallari.lwge.graphic

import it.filippocavallari.lwge.data.Vao
import it.filippocavallari.lwge.data.Vbo
import it.filippocavallari.lwge.loader.Loader

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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Mesh

        if (material != other.material) return false
        if (vao != other.vao) return false
        if (vboSet != other.vboSet) return false
        if (textureVboSet != other.textureVboSet) return false

        return true
    }

    override fun hashCode(): Int {
        var result = material.hashCode()
        result = 31 * result + vao.hashCode()
        result = 31 * result + vboSet.hashCode()
        result = 31 * result + textureVboSet.hashCode()
        return result
    }

    fun clear(){
        Loader.clearMesh(this)
    }


}