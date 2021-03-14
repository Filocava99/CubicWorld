package it.filippocavallari.cubicworld.graphic.mesh

import it.filippocavallari.cubicworld.data.block.Block
import it.filippocavallari.cubicworld.data.block.FaceDirection
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

    private val verticesList = LinkedList<Float>()
    private val indicesList = LinkedList<Int>()
    private val normalsList = LinkedList<Float>()
    private val uvsList = LinkedList<Float>()
    private val tangentsList = LinkedList<Float>()

    fun buildMesh(): Mesh {
        for (x in 0..15) {
            for (z in 0..15) {
                for (y in 0..255) {
                    val blockId = chunk.getBlock(x, y, z)
                    val coord = Vector3f(x.toFloat(),y.toFloat(),z.toFloat())
                    if (blockId != 0) {
                        if (isTopFaceVisible(x, y, z)) {
                            addFace(FaceDirection.UP,blockId, coord)
                        }
                        if (isBottomFaceVisible(x, y, z)) {
                            addFace(FaceDirection.DOWN, blockId, coord)
                        }
                        if (isFrontFaceVisible(x, y, z)) {
                            addFace(FaceDirection.SOUTH, blockId, coord)
                        }
                        if (isBackFaceVisible(x, y, z)) {
                            addFace(FaceDirection.NORTH, blockId, coord)
                        }
                        if (isLeftFaceVisible(x, y, z)) {
                            addFace(FaceDirection.WEST, blockId, coord)
                        }
                        if (isRightFaceVisible(x, y, z)) {
                            addFace(FaceDirection.EAST, blockId, coord)
                        }
                    }
                }
            }
        }
        val verticesArray = verticesList.toFloatArray()
        val indicesArray = indicesList.toIntArray()
        val normalsArray = normalsList.toFloatArray()
        val uvsArray = uvsList.toFloatArray()
        val tangentsArray = tangentsList.toFloatArray()
        val texture = TextureLoader.createTexture("src/main/resources/textures/blocks/atlas.png")
        val normalMap = TextureLoader.createTexture("src/main/resources/textures/blocks/atlas_n.png")
        //val depthMap = TextureLoader.createTexture("src/main/resources/textures/blocks/dirt_h.png")
        val material = Material(texture, normalMap, null, reflectance = 0f)
        val mesh = Mesh(
            verticesArray,
            indicesArray,
            normalsArray,
            uvsArray,
            tangentsArray,
            material
        )
        Loader.loadMesh(mesh)
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

    private fun addFace(faceDirection: FaceDirection, blockId: Int, coord: Vector3f) {
        val bakedModel = resourceManager.backedMeshes[BlockMaterial.valueOf(blockId)]?.faceMeshMap?.get(faceDirection)
        bakedModel?.let {
            println(faceDirection)
            val indexOffset = verticesList.size
            var i = 0
            while(i < bakedModel.vertices.size){
                verticesList.add(bakedModel.vertices[i]+coord.x)
                verticesList.add(bakedModel.vertices[i+1]+coord.y)
                verticesList.add(bakedModel.vertices[i+2]+coord.z)
                i+=3
            }
            it.indices.forEach{ index ->
                indicesList.add(index+indexOffset)
                indexOffset+1
            }
            normalsList.addAll(it.normals)
            uvsList.addAll(it.uvs)
            tangentsList.addAll(it.tangents)
        }
    }
}