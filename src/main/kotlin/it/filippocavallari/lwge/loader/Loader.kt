package it.filippocavallari.lwge.loader

import it.filippocavallari.lwge.data.Vao
import it.filippocavallari.lwge.data.Vbo
import it.filippocavallari.lwge.graphic.Mesh
import org.lwjgl.BufferUtils.createByteBuffer
import org.lwjgl.opengl.GL32C.*
import org.lwjgl.system.MemoryUtil
import java.nio.Buffer
import java.nio.ByteBuffer
import java.nio.FloatBuffer
import java.nio.channels.Channels
import java.nio.file.Files
import java.nio.file.Paths
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
        glBindVertexArray(vao.id)
        val verticesVbo = getVBO()
        mesh.vboSet.add(verticesVbo)
        loadVerticesInVbo(verticesVbo,mesh.vertices)
        val indicesVbo = getVBO()
        mesh.vboSet.add(indicesVbo)
        loadIndicesInVbo(indicesVbo, mesh.indices)
        val normalsVbo = getVBO()
        mesh.vboSet.add(normalsVbo)
        loadNormalsInVbo(normalsVbo, mesh.normals)
        mesh.uvs?.let {
            val uvsVbo = getVBO()
            mesh.textureVboSet.add(uvsVbo)
            loadUVsInVbo(uvsVbo, it)
        }
        mesh.tangents?.let{
            val tangentsVbo = getVBO()
            mesh.vboSet.add(tangentsVbo)
            loadTangentsInVbo(tangentsVbo, it)
        }
        glBindVertexArray(0)
    }

    fun loadVerticesInVbo(vbo: Vbo, array: FloatArray){
        val buffer = MemoryUtil.memAllocFloat(array.size)
        array.forEach { buffer.put(it) }
        buffer.flip()
        loadFloatBufferInVbo(buffer, vbo, 3, 0)
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
        loadFloatBufferInVbo(textCoordsBuffer, vbo, 2, 1)
        MemoryUtil.memFree(textCoordsBuffer)
    }

    fun loadNormalsInVbo(vbo: Vbo, normals: FloatArray) {
        val normalsBuffer = MemoryUtil.memAllocFloat(normals.size)
        for (normal in normals) {
            normalsBuffer.put(normal)
        }
        normalsBuffer.flip()
        loadFloatBufferInVbo(normalsBuffer, vbo, 3, 2)
        MemoryUtil.memFree(normalsBuffer)
    }

    fun loadTangentsInVbo(vbo: Vbo, tangents: FloatArray){
        val tangentsBuffer = MemoryUtil.memAllocFloat(tangents.size)
        for (tangent in tangents) {
            tangentsBuffer.put(tangent)
        }
        tangentsBuffer.flip()
        loadFloatBufferInVbo(tangentsBuffer, vbo, 3, 3)
        MemoryUtil.memFree(tangentsBuffer)
    }

    fun loadBiTangentsInVbo(vbo: Vbo, biTangents: FloatArray){
        val biTangentsBuffer = MemoryUtil.memAllocFloat(biTangents.size)
        for (biTangent in biTangents) {
            biTangentsBuffer.put(biTangent)
        }
        biTangentsBuffer.flip()
        loadFloatBufferInVbo(biTangentsBuffer, vbo, 3, 4)
        MemoryUtil.memFree(biTangentsBuffer)
    }

    private fun loadFloatBufferInVbo(buffer: Buffer, vbo: Vbo, size: Int, index: Int){
        glBindBuffer(GL_ARRAY_BUFFER, vbo.id)
        glBufferData(GL_ARRAY_BUFFER, buffer as FloatBuffer, GL_STATIC_DRAW)
        glEnableVertexAttribArray(index)
        glVertexAttribPointer(index, size, GL_FLOAT, false, 0, 0)
    }

    fun resourceToByteBuffer(resource: String, bufferSize: Int): ByteBuffer {
        var buffer: ByteBuffer
        val path = Paths.get(resource)
        if (Files.isReadable(path)) {
            Files.newByteChannel(path).use { fc ->
                buffer = createByteBuffer(fc.size().toInt() + 1)
                while (fc.read(buffer) != -1);
            }
        } else {
            Loader::class.java.getResourceAsStream(resource).use { source ->
                Channels.newChannel(source).use { rbc ->
                    buffer = createByteBuffer(bufferSize)
                    while (true) {
                        val bytes: Int = rbc.read(buffer)
                        if (bytes == -1) {
                            break
                        }
                        if (buffer.remaining() == 0) {
                            buffer = resizeBuffer(buffer, buffer.capacity() * 2)
                        }
                    }
                }
            }
        }
        buffer.flip()
        return buffer
    }

    private fun resizeBuffer(buffer: ByteBuffer, newCapacity: Int): ByteBuffer {
        val newBuffer = createByteBuffer(newCapacity)
        buffer.flip()
        newBuffer.put(buffer)
        return newBuffer
    }

}