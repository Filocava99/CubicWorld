package it.filippocavallari.lwge.graphic

import it.filippocavallari.lwge.data.Vao
import it.filippocavallari.lwge.data.Vbo
import it.filippocavallari.lwge.loader.Loader

class Mesh(var vertices: FloatArray, var indices: IntArray, var normals: FloatArray, var uvs: FloatArray?, var tangents: FloatArray?, val material: Material){
    lateinit var vao: Vao
    lateinit var verticesVbo: Vbo
    lateinit var indicesVbo: Vbo
    lateinit var normalsVbo: Vbo
    var uvsVbo: Vbo? = null
    var tangentsVbo: Vbo? = null

    fun clearArrays(){
        vertices = FloatArray(0)
        indices = IntArray(0)
        normals = FloatArray(0)
        uvs = null
        tangents = null
    }

    fun clear(){
        Loader.clearMesh(this)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Mesh

        if (material != other.material) return false
        if (vao != other.vao) return false
        if (verticesVbo != other.verticesVbo) return false
        if (indicesVbo != other.indicesVbo) return false
        if (normalsVbo != other.normalsVbo) return false
        if (uvsVbo != other.uvsVbo) return false
        if (tangentsVbo != other.tangentsVbo) return false

        return true
    }

    override fun hashCode(): Int {
        var result = material.hashCode()
        result = 31 * result + vao.hashCode()
        result = 31 * result + verticesVbo.hashCode()
        result = 31 * result + indicesVbo.hashCode()
        result = 31 * result + normalsVbo.hashCode()
        result = 31 * result + (uvsVbo?.hashCode() ?: 0)
        result = 31 * result + (tangentsVbo?.hashCode() ?: 0)
        return result
    }


}