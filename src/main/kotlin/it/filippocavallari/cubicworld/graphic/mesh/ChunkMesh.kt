package it.filippocavallari.cubicworld.graphic.mesh

import it.filippocavallari.cubicworld.manager.ResourceManager
import it.filippocavallari.cubicworld.world.chunk.Chunk
import it.filippocavallari.lwge.graphic.Material
import it.filippocavallari.lwge.graphic.Mesh
import it.filippocavallari.lwge.loader.Loader
import it.filippocavallari.lwge.loader.TextureLoader
import it.filippocavallari.lwge.math.Math
import org.joml.Vector2f
import org.joml.Vector3f
import org.lwjgl.opengl.GL30C.glBindVertexArray
import java.util.*

typealias BlockMaterial = it.filippocavallari.cubicworld.data.block.Material

class ChunkMesh(val chunk: Chunk, val resourceManager: ResourceManager) {

    private val verticesList = LinkedList<Vector3f>()
    private val indicesList = LinkedList<Int>()
    private val normalsList = LinkedList<Vector3f>()
    private val uvsList = LinkedList<Vector2f>()
    private val tangentsList = LinkedList<Vector3f>()
    private val biTangentsList = LinkedList<Vector3f>()

    fun buildMesh(): Mesh {
        for (x in 0..15) {
            for (z in 0..15) {
                for (y in 0..255) {
                    val blockId = chunk.getBlock(x, y, z)
                    if (blockId != 0) {
                        if (isTopFaceVisible(x, y, z)) {
                            addTopFace(x, y, z, blockId)
                        }
                        if (isBottomFaceVisible(x, y, z)) {
                            addBottomFace(x, y, z, blockId)
                        }
                        if (isFrontFaceVisible(x, y, z)) {
                            addFrontFace(x, y, z, blockId)
                        }
                        if (isBackFaceVisible(x, y, z)) {
                            addBackFace(x, y, z, blockId)
                        }
                        if (isLeftFaceVisible(x, y, z)) {
                            addLeftFace(x, y, z, blockId)
                        }
                        if (isRightFaceVisible(x, y, z)) {
                            addRightFace(x, y, z, blockId)
                        }
                    }
                }
            }
        }
        val verticesArray = FloatArray(verticesList.size * 3)
        verticesList.forEachIndexed { index, vector3f ->
            verticesArray[index * 3] = vector3f.x
            verticesArray[index * 3 + 1] = vector3f.y
            verticesArray[index * 3 + 2] = vector3f.z
        }
        val indicesArray = IntArray(indicesList.size)
        indicesList.forEachIndexed { index, int -> indicesArray[index] = int }
        val normalsArray = FloatArray(normalsList.size * 3)
        normalsList.forEachIndexed { index, vector3f ->
            normalsArray[index * 3] = vector3f.x
            normalsArray[index * 3 + 1] = vector3f.y
            normalsArray[index * 3 + 2] = vector3f.z
        }
        val uvsArray = FloatArray(uvsList.size * 2)
        uvsList.forEachIndexed { index, vector2f ->
            uvsArray[index * 2] = vector2f.x
            uvsArray[index * 2 + 1] = vector2f.y
        }
        val tangentsArray = FloatArray(tangentsList.size * 3)
        tangentsList.forEachIndexed { index, vector3f ->
            tangentsArray[index * 3] = vector3f.x
            tangentsArray[index * 3 + 1] = vector3f.y
            tangentsArray[index * 3 + 2] = vector3f.z
        }
        val biTangentsArray = FloatArray(biTangentsList.size * 3)
        biTangentsList.forEachIndexed { index, vector3f ->
            biTangentsArray[index * 3] = vector3f.x
            biTangentsArray[index * 3 + 1] = vector3f.y
            biTangentsArray[index * 3 + 2] = vector3f.z
        }
        val vao = Loader.getVAO()
        val verticesVbo = Loader.getVBO()
        val indicesVbo = Loader.getVBO()
        val normalsVbo = Loader.getVBO()
        val uvsVbo = Loader.getVBO()
        val tangentsVbo = Loader.getVBO()
        val biTangentsVbo = Loader.getVBO()
        glBindVertexArray(vao.id)
        Loader.loadVerticesInVbo(verticesVbo, verticesArray)
        Loader.loadIndicesInVbo(indicesVbo, indicesArray)
        Loader.loadNormalsInVbo(normalsVbo, normalsArray)
        Loader.loadUVsInVbo(uvsVbo, uvsArray)
        Loader.loadTangentsInVbo(tangentsVbo, tangentsArray)
        Loader.loadBiTangentsInVbo(biTangentsVbo, biTangentsArray)
        glBindVertexArray(0)
        val texture = TextureLoader.createTexture("src/main/resources/textures/blocks/bricks2.jpg")
        val normalMap = TextureLoader.createTexture("src/main/resources/textures/blocks/bricks2_n.jpg")
        val depthMap = TextureLoader.createTexture("src/main/resources/textures/blocks/bricks2_h.jpg")
        val material = Material(texture, null, null, reflectance = 0f)
        val mesh = Mesh(
            material,
            vao,
            mutableSetOf(verticesVbo, indicesVbo, normalsVbo),
            mutableSetOf(uvsVbo),
            indicesArray.size,
        )
        return mesh
    }

    private fun isTopFaceVisible(x: Int, y: Int, z: Int): Boolean {
        return if (y + 1 >= 255) {
            true
        } else {
            chunk.getBlock(x, y + 1, z) == 0
        }
    }

    private fun isBottomFaceVisible(x: Int, y: Int, z: Int): Boolean {
        return if (y - 1 < 0) {
            true

        } else {
            chunk.getBlock(x, y - 1, z) == 0
        }
    }

    private fun isLeftFaceVisible(x: Int, y: Int, z: Int): Boolean {
        return if (x - 1 < 0) {
            true
        } else {
            chunk.getBlock(x - 1, y, z) == 0
        }
    }

    private fun isRightFaceVisible(x: Int, y: Int, z: Int): Boolean {
        return if (x + 1 >= 16) {
            true
        } else {
            chunk.getBlock(x + 1, y, z) == 0
        }
    }

    private fun isFrontFaceVisible(x: Int, y: Int, z: Int): Boolean {
        return if (z - 1 < 0) {
            true

        } else {
            chunk.getBlock(x, y, z - 1) == 0
        }
    }

    private fun isBackFaceVisible(x: Int, y: Int, z: Int): Boolean {
        return if (z + 1 >= 16) {
            true
        } else {
            chunk.getBlock(x, y, z + 1) == 0
        }
    }

    private fun addTopFace(x: Int, y: Int, z: Int, blockId: Int) {
        val currentVerticesListSize = verticesList.size
        val pos1 = Vector3f(-0.5f + x, 0.5f + y, 0.5f + z)
        val pos2 = Vector3f(-0.5f + x, 0.5f + y, -0.5f + z)
        val pos3 = Vector3f(0.5f + x, 0.5f + y, -0.5f + z)
        val pos4 = Vector3f(0.5f + x, 0.5f + y, 0.5f + z)
        //Vertices
        verticesList.add(pos1)
        verticesList.add(pos2)
        verticesList.add(pos3)
        verticesList.add(pos4)
        //Indices
        indicesList.add(currentVerticesListSize)
        indicesList.add(currentVerticesListSize + 3)
        indicesList.add(currentVerticesListSize + 1)
        indicesList.add(currentVerticesListSize+3)
        indicesList.add(currentVerticesListSize + 2)
        indicesList.add(currentVerticesListSize + 1)
        //Normals
        normalsList.add(Vector3f(0f, 1f, 0f))
        normalsList.add(Vector3f(0f, 1f, 0f))
        normalsList.add(Vector3f(0f, 1f, 0f))
        normalsList.add(Vector3f(0f, 1f, 0f))
        //UVs
        val uv1 = Vector2f(0f, 0f)
        val uv2 = Vector2f(0f, 1f)
        val uv3 = Vector2f(1f, 1f)
        val uv4 = Vector2f(1f, 0f)
        uvsList.add(uv1)
        uvsList.add(uv2)
        uvsList.add(uv3)
        uvsList.add(uv4)
        val tangents1 = Math.calculateNormalTangents(pos1,pos2,pos3,uv1,uv2,uv3)
        tangentsList.add(tangents1.tangent)
        tangentsList.add(tangents1.tangent)
        tangentsList.add(tangents1.tangent)
        tangentsList.add(tangents1.tangent)
        biTangentsList.add(tangents1.biTangent)
        biTangentsList.add(tangents1.biTangent)
        biTangentsList.add(tangents1.biTangent)
        biTangentsList.add(tangents1.biTangent)
    }

    private fun addBottomFace(x: Int, y: Int, z: Int, blockId: Int) {
        val currentVerticesListSize = verticesList.size
        val pos1 = Vector3f(Vector3f(-0.5f + x, -0.5f + y, -0.5f + z))
        val pos2 = Vector3f(Vector3f(-0.5f + x, -0.5f + y, 0.5f + z))
        val pos3 = Vector3f(Vector3f(0.5f + x, -0.5f + y, 0.5f + z))
        val pos4 = Vector3f(Vector3f(0.5f + x, -0.5f + y, -0.5f + z))
        //Vertices
        verticesList.add(pos1)
        verticesList.add(pos2)
        verticesList.add(pos3)
        verticesList.add(pos4)
        //Indices
        indicesList.add(currentVerticesListSize)
        indicesList.add(currentVerticesListSize + 3)
        indicesList.add(currentVerticesListSize + 1)
        indicesList.add(currentVerticesListSize+3)
        indicesList.add(currentVerticesListSize + 2)
        indicesList.add(currentVerticesListSize + 1)
        //Normals
        normalsList.add(Vector3f(0f, -1f, 0f))
        normalsList.add(Vector3f(0f, -1f, 0f))
        normalsList.add(Vector3f(0f, -1f, 0f))
        normalsList.add(Vector3f(0f, -1f, 0f))
        //UVs
        val uv1 = Vector2f(0f, 0f)
        val uv2 = Vector2f(0f, 1f)
        val uv3 = Vector2f(1f, 1f)
        val uv4 = Vector2f(1f, 0f)
        uvsList.add(uv1)
        uvsList.add(uv2)
        uvsList.add(uv3)
        uvsList.add(uv4)
        val tangents1 = Math.calculateNormalTangents(pos1,pos2,pos3,uv1,uv2,uv3)
        tangentsList.add(tangents1.tangent)
        tangentsList.add(tangents1.tangent)
        tangentsList.add(tangents1.tangent)
        tangentsList.add(tangents1.tangent)
        biTangentsList.add(tangents1.biTangent)
        biTangentsList.add(tangents1.biTangent)
        biTangentsList.add(tangents1.biTangent)
        biTangentsList.add(tangents1.biTangent)
    }

    private fun addFrontFace(x: Int, y: Int, z: Int, blockId: Int) {
        val currentVerticesListSize = verticesList.size
        val pos1 = Vector3f(-0.5f + x, 0.5f + y, -0.5f + z)
        val pos2 = Vector3f(-0.5f + x, -0.5f + y, -0.5f + z)
        val pos3 = Vector3f(0.5f + x, -0.5f + y, -0.5f + z)
        val pos4 = Vector3f(0.5f + x, 0.5f + y, -0.5f + z)
        //Vertices
        verticesList.add(pos1)
        verticesList.add(pos2)
        verticesList.add(pos3)
        verticesList.add(pos4)
        //Indices
        indicesList.add(currentVerticesListSize)
        indicesList.add(currentVerticesListSize + 3)
        indicesList.add(currentVerticesListSize + 1)
        indicesList.add(currentVerticesListSize+3)
        indicesList.add(currentVerticesListSize + 2)
        indicesList.add(currentVerticesListSize + 1)
        //Normals
        normalsList.add(Vector3f(0f, 0f, -1f))
        normalsList.add(Vector3f(0f, 0f, -1f))
        normalsList.add(Vector3f(0f, 0f, -1f))
        normalsList.add(Vector3f(0f, 0f, -1f))
        //UVs
        val uv1 = Vector2f(0f, 0f)
        val uv2 = Vector2f(0f, 1f)
        val uv3 = Vector2f(1f, 1f)
        val uv4 = Vector2f(1f, 0f)
        uvsList.add(uv1)
        uvsList.add(uv2)
        uvsList.add(uv3)
        uvsList.add(uv4)
        val tangents1 = Math.calculateNormalTangents(pos1,pos2,pos3,uv1,uv2,uv3)
        tangentsList.add(tangents1.tangent)
        tangentsList.add(tangents1.tangent)
        tangentsList.add(tangents1.tangent)
        tangentsList.add(tangents1.tangent)
        biTangentsList.add(tangents1.biTangent)
        biTangentsList.add(tangents1.biTangent)
        biTangentsList.add(tangents1.biTangent)
        biTangentsList.add(tangents1.biTangent)
    }

    private fun addBackFace(x: Int, y: Int, z: Int, blockId: Int) {
        val currentVerticesListSize = verticesList.size
        val pos1 = Vector3f(0.5f + x, 0.5f + y, 0.5f + z)
        val pos2 = Vector3f(0.5f + x, -0.5f + y, 0.5f + z)
        val pos3 = Vector3f(-0.5f + x, -0.5f + y, 0.5f + z)
        val pos4 = Vector3f(-0.5f + x, 0.5f + y, 0.5f + z)
        //Vertices
        verticesList.add(pos1)
        verticesList.add(pos2)
        verticesList.add(pos3)
        verticesList.add(pos4)
        //Indices
        indicesList.add(currentVerticesListSize)
        indicesList.add(currentVerticesListSize + 3)
        indicesList.add(currentVerticesListSize + 1)
        indicesList.add(currentVerticesListSize+3)
        indicesList.add(currentVerticesListSize + 2)
        indicesList.add(currentVerticesListSize + 1)
        //Normals
        normalsList.add(Vector3f(0f, 0f, 1f))
        normalsList.add(Vector3f(0f, 0f, 1f))
        normalsList.add(Vector3f(0f, 0f, 1f))
        normalsList.add(Vector3f(0f, 0f, 1f))
        //UVs
        val uv1 = Vector2f(0f, 0f)
        val uv2 = Vector2f(0f, 1f)
        val uv3 = Vector2f(1f, 1f)
        val uv4 = Vector2f(1f, 0f)
        uvsList.add(uv1)
        uvsList.add(uv2)
        uvsList.add(uv3)
        uvsList.add(uv4)
        val tangents1 = Math.calculateNormalTangents(pos1,pos2,pos3,uv1,uv2,uv3)
        tangentsList.add(tangents1.tangent)
        tangentsList.add(tangents1.tangent)
        tangentsList.add(tangents1.tangent)
        tangentsList.add(tangents1.tangent)
        biTangentsList.add(tangents1.biTangent)
        biTangentsList.add(tangents1.biTangent)
        biTangentsList.add(tangents1.biTangent)
        biTangentsList.add(tangents1.biTangent)
    }

    private fun addLeftFace(x: Int, y: Int, z: Int, blockId: Int) {
        val currentVerticesListSize = verticesList.size
        val pos1 = Vector3f(-0.5f + x, 0.5f + y, 0.5f + z)
        val pos2 = Vector3f(-0.5f + x, -0.5f + y, 0.5f + z)
        val pos3 = Vector3f(-0.5f + x, -0.5f + y, -0.5f + z)
        val pos4 = Vector3f(-0.5f + x, 0.5f + y, -0.5f + z)
        //Vertices
        verticesList.add(pos1)
        verticesList.add(pos2)
        verticesList.add(pos3)
        verticesList.add(pos4)
        //Indices
        indicesList.add(currentVerticesListSize)
        indicesList.add(currentVerticesListSize + 3)
        indicesList.add(currentVerticesListSize + 1)
        indicesList.add(currentVerticesListSize+3)
        indicesList.add(currentVerticesListSize + 2)
        indicesList.add(currentVerticesListSize + 1)
        //Normals
        normalsList.add(Vector3f(-1f, 0f, 0f))
        normalsList.add(Vector3f(-1f, 0f, 0f))
        normalsList.add(Vector3f(-1f, 0f, 0f))
        normalsList.add(Vector3f(-1f, 0f, 0f))
        //UVs
        val uv1 = Vector2f(0f, 0f)
        val uv2 = Vector2f(0f, 1f)
        val uv3 = Vector2f(1f, 1f)
        val uv4 = Vector2f(1f, 0f)
        uvsList.add(uv1)
        uvsList.add(uv2)
        uvsList.add(uv3)
        uvsList.add(uv4)
        val tangents1 = Math.calculateNormalTangents(pos1,pos2,pos3,uv1,uv2,uv3)
        tangentsList.add(tangents1.tangent)
        tangentsList.add(tangents1.tangent)
        tangentsList.add(tangents1.tangent)
        tangentsList.add(tangents1.tangent)
        biTangentsList.add(tangents1.biTangent)
        biTangentsList.add(tangents1.biTangent)
        biTangentsList.add(tangents1.biTangent)
        biTangentsList.add(tangents1.biTangent)
    }

    private fun addRightFace(x: Int, y: Int, z: Int, blockId: Int) {
        val currentVerticesListSize = verticesList.size
        val pos1 = Vector3f(0.5f + x, 0.5f + y, -0.5f + z)
        val pos2 = Vector3f(0.5f + x, -0.5f + y, -0.5f + z)
        val pos3 = Vector3f(0.5f + x, -0.5f + y, 0.5f + z)
        val pos4 = Vector3f(0.5f + x, 0.5f + y, 0.5f + z)
        //Vertices
        verticesList.add(pos1)
        verticesList.add(pos2)
        verticesList.add(pos3)
        verticesList.add(pos4)
        //Indices
        indicesList.add(currentVerticesListSize)
        indicesList.add(currentVerticesListSize + 3)
        indicesList.add(currentVerticesListSize + 1)
        indicesList.add(currentVerticesListSize+3)
        indicesList.add(currentVerticesListSize + 2)
        indicesList.add(currentVerticesListSize + 1)
        //Normals
        normalsList.add(Vector3f(1f, 0f, 0f))
        normalsList.add(Vector3f(1f, 0f, 0f))
        normalsList.add(Vector3f(1f, 0f, 0f))
        normalsList.add(Vector3f(1f, 0f, 0f))
        //UVs
        val uv1 = Vector2f(0f, 0f)
        val uv2 = Vector2f(0f, 1f)
        val uv3 = Vector2f(1f, 1f)
        val uv4 = Vector2f(1f, 0f)
        uvsList.add(uv1)
        uvsList.add(uv2)
        uvsList.add(uv3)
        uvsList.add(uv4)
        val tangents1 = Math.calculateNormalTangents(pos1,pos2,pos3,uv1,uv2,uv3)
        tangentsList.add(tangents1.tangent)
        tangentsList.add(tangents1.tangent)
        tangentsList.add(tangents1.tangent)
        tangentsList.add(tangents1.tangent)
        biTangentsList.add(tangents1.biTangent)
        biTangentsList.add(tangents1.biTangent)
        biTangentsList.add(tangents1.biTangent)
        biTangentsList.add(tangents1.biTangent)
    }
}