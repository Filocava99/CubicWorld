package it.filippocavallari.lwge.loader

import it.filippocavallari.lwge.data.Vao
import it.filippocavallari.lwge.data.Vbo
import it.filippocavallari.lwge.graphic.Mesh
import org.lwjgl.opengl.GL32C.*
import org.lwjgl.opengl.GL43C
import org.lwjgl.system.MemoryUtil
import java.nio.Buffer
import java.nio.FloatBuffer
import java.util.*
import java.util.stream.Collectors


object Loader {

    private val VBOsPool = LinkedList<Vbo>()
    private val VAOsPool = LinkedList<Vao>()

    private val usedVBOs = LinkedList<Vbo>()
    private val usedVAOs = LinkedList<Vao>()

    fun getVBO() : Vbo {
        if(VBOsPool.isEmpty()){
            createVBO()
        }
        val vbo = VBOsPool.pollFirst()
        usedVBOs.add(vbo)
        return vbo
    }

    fun getVAO() : Vao {
        if(VBOsPool.isEmpty()){
            createVAO()
        }
        val vao = VAOsPool.pollFirst()
        usedVAOs.add(vao)
        return vao
    }

    fun createVAOs(amount: Int){
        val intArray = IntArray(amount)
        glGenVertexArrays(intArray)
        VAOsPool.addAll(intArray.toMutableList().stream().map { Vao(it) }.collect(Collectors.toList()))
    }

    private fun createVAO(){
        VAOsPool.add(Vao(glGenVertexArrays()))
    }

    fun createVBOs(amount: Int){
        val intArray = IntArray(amount)
        glGenBuffers(intArray)
        VBOsPool.addAll(intArray.toMutableList().stream().map { Vbo(it) }.collect(Collectors.toList()))
    }

    private fun createVBO(){
        VBOsPool.add(Vbo(glGenBuffers()))
    }

    fun loadMesh(mesh: Mesh){
        val vao = getVAO()
        mesh.vao = vao
        bindVAO(vao)
        //Vertices
        val verticesVbo = getVBO()
        mesh.verticesVbo = verticesVbo
        loadVerticesInVbo(verticesVbo,mesh.vertices)
        loadVBOinVAO(vao, verticesVbo,0,3)
        //Indices
        val indicesVbo = getVBO()
        mesh.indicesVbo = indicesVbo
        loadIndicesInVbo(indicesVbo, mesh.indices)
        //Normals
        val normalsVbo = getVBO()
        mesh.normalsVbo = normalsVbo
        loadNormalsInVbo(normalsVbo, mesh.normals)
        loadVBOinVAO(vao, normalsVbo,2,3)
        //UVs
        mesh.uvs?.let {
            val uvsVbo = getVBO()
            mesh.uvsVbo = uvsVbo
            loadUVsInVbo(uvsVbo, it)
            loadVBOinVAO(vao, uvsVbo,1,2)
        }
        //Tangents
        mesh.tangents?.let{
            val tangentsVbo = getVBO()
            mesh.tangentsVbo = tangentsVbo
            loadTangentsInVbo(tangentsVbo, it)
            loadVBOinVAO(vao, tangentsVbo, 3,3)
        }
        glBindVertexArray(0)
    }

    fun loadMeshInVBOs(mesh: Mesh){
        //Vertices
        val verticesVbo = getVBO()
        println(verticesVbo)
        mesh.verticesVbo = verticesVbo
        loadVerticesInVbo(verticesVbo,mesh.vertices)
        //Normals
        val normalsVbo = getVBO()
        mesh.normalsVbo = normalsVbo
        loadNormalsInVbo(normalsVbo, mesh.normals)
        //UVs
        mesh.uvs?.let {
            val uvsVbo = getVBO()
            mesh.uvsVbo = uvsVbo
            loadUVsInVbo(uvsVbo, it)
        }
        //Tangents
        mesh.tangents?.let{
            val tangentsVbo = getVBO()
            mesh.tangentsVbo = tangentsVbo
            loadTangentsInVbo(tangentsVbo, it)
        }
    }

    fun loadVerticesInVbo(vbo: Vbo, array: FloatArray){
        val buffer = MemoryUtil.memAllocFloat(array.size)
        array.forEach { buffer.put(it) }
        buffer.flip()
        loadFloatBufferInVbo(buffer, vbo)
        MemoryUtil.memFree(buffer)
    }

    fun loadIndicesInVbo(vbo: Vbo, indices: IntArray) {
        val buffer = MemoryUtil.memAllocInt(indices.size)
        for (index in indices) {
            buffer.put(index)
        }
        buffer.flip()
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vbo.id)
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, buffer, GL_STATIC_DRAW)
        MemoryUtil.memFree(buffer)
    }

    fun loadUVsInVbo(vbo: Vbo, uvs: FloatArray) {
        val textCoordsBuffer = MemoryUtil.memAllocFloat(uvs.size)
        for (uv in uvs) {
            textCoordsBuffer.put(uv)
        }
        textCoordsBuffer.flip()
        loadFloatBufferInVbo(textCoordsBuffer, vbo)
        MemoryUtil.memFree(textCoordsBuffer)
    }

    fun loadNormalsInVbo(vbo: Vbo, normals: FloatArray) {
        val normalsBuffer = MemoryUtil.memAllocFloat(normals.size)
        for (normal in normals) {
            normalsBuffer.put(normal)
        }
        normalsBuffer.flip()
        loadFloatBufferInVbo(normalsBuffer, vbo)
        MemoryUtil.memFree(normalsBuffer)
    }

    fun loadTangentsInVbo(vbo: Vbo, tangents: FloatArray){
        val tangentsBuffer = MemoryUtil.memAllocFloat(tangents.size)
        for (tangent in tangents) {
            tangentsBuffer.put(tangent)
        }
        tangentsBuffer.flip()
        loadFloatBufferInVbo(tangentsBuffer, vbo)
        MemoryUtil.memFree(tangentsBuffer)
    }

    fun bindVBO(vbo:Vbo){
        glBindBuffer(GL_ARRAY_BUFFER,vbo.id)
    }

    fun bindVAO(vao: Vao){
        glBindVertexArray(vao.id)
    }

    fun loadVBOinVAO(vao: Vao, vbo: Vbo, index: Int, size: Int){
        bindVAO(vao)
        bindVBO(vbo)
        glEnableVertexAttribArray(index)
        glVertexAttribPointer(index, size, GL_FLOAT, false, 0, 0)
    }

    fun clearMesh(mesh: Mesh){
        GL43C.glInvalidateBufferData(mesh.verticesVbo.id)
        VBOsPool.add(mesh.verticesVbo)
        GL43C.glInvalidateBufferData(mesh.indicesVbo.id)
        VBOsPool.add(mesh.indicesVbo)
        GL43C.glInvalidateBufferData(mesh.normalsVbo.id)
        VBOsPool.add(mesh.normalsVbo)
        mesh.uvsVbo?.let {
            GL43C.glInvalidateBufferData(it.id)
            VBOsPool.add(it)
        }
        mesh.tangentsVbo?.let {
            GL43C.glInvalidateBufferData(it.id)
            VBOsPool.add(it)
        }
        VAOsPool.add(mesh.vao)
    }

    private fun loadFloatBufferInVbo(buffer: Buffer, vbo: Vbo){
        println("aaa")
        glBindBuffer(GL_ARRAY_BUFFER, vbo.id)
        println("bbb")
        glBufferData(GL_ARRAY_BUFFER, buffer as FloatBuffer, GL_STATIC_DRAW)
        println("ccc")
    }

}