package it.filippocavallari.cubicworld.graphic.mesh

import it.filippocavallari.cubicworld.data.block.FaceDirection
import it.filippocavallari.cubicworld.manager.ResourceManager
import it.filippocavallari.cubicworld.world.chunk.Chunk
import it.filippocavallari.lwge.graphic.Material
import it.filippocavallari.lwge.graphic.Mesh
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.joml.Vector3f
import java.util.*
import kotlin.math.floor

typealias BlockMaterial = it.filippocavallari.cubicworld.data.block.Material

class ChunkMesh(
    val chunk: Chunk,
    private val material: Material,
    private val resourceManager: ResourceManager
) {

    private val verticesList = LinkedList<Float>()
    private val indicesList = LinkedList<Int>()
    private val normalsList = LinkedList<Float>()
    private val uvsList = LinkedList<Float>()
    private val tangentsList = LinkedList<Float>()


    private val waterVerticesList = LinkedList<Float>()
    private val waterIndicesList = LinkedList<Int>()
    private val waterNormalsList = LinkedList<Float>()
    private val waterUvsList = LinkedList<Float>()
    private val waterTangentsList = LinkedList<Float>()

    lateinit var chunkMesh: Mesh
        private set
    lateinit var waterMesh: Mesh
        private set

    fun buildMesh() {
        addBlocks()
        chunkMesh = Mesh(
            verticesList.toFloatArray(),
            indicesList.toIntArray(),
            normalsList.toFloatArray(),
            uvsList.toFloatArray(),
            tangentsList.toFloatArray(),
            material
        )
        waterMesh = Mesh(
            waterVerticesList.toFloatArray(),
            waterIndicesList.toIntArray(),
            waterNormalsList.toFloatArray(),
            waterUvsList.toFloatArray(),
            waterTangentsList.toFloatArray(),
            material
        )
        clearLists()
    }

    private fun addBlocks() {
        val nCore = Runtime.getRuntime().availableProcessors() - 1
        val sizePerCore = floor(255f / nCore).toInt()
        val lastCoreSize = 255 - (sizePerCore * nCore - 1)
        for (x in 0..15) {
            for (z in 0..15) {
                runBlocking {
                    for (i in 0 until nCore) {
                        if (i == nCore - 1) {
                            launch {
                                multithreadFun(x, z, i * sizePerCore, i * sizePerCore + lastCoreSize)
                            }
                        } else {
                            launch {
                                multithreadFun(x, z, i * sizePerCore, i * sizePerCore + sizePerCore - 1)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun multithreadFun(x: Int, z: Int, from: Int, to: Int) {
        for (y in from..to) {
            val blockId = chunk.getBlock(x, y, z)
            val coord = Vector3f(x.toFloat(), y.toFloat(), z.toFloat())
            if (blockId != 0) {
                if (isTopFaceVisible(x, y, z)) {
                    addFace(FaceDirection.UP, blockId, coord)
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

    private fun isTopFaceVisible(x: Int, y: Int, z: Int): Boolean {
        return if (y + 1 >= 255) {
            true
        } else {
            chunk.getBlock(x, y + 1, z) == 0 || chunk.getBlock(x, y + 1, z) == BlockMaterial.WATER.id
        }
    }

    private fun isBottomFaceVisible(x: Int, y: Int, z: Int): Boolean {
        return if (y - 1 < 0) {
            true

        } else {
            chunk.getBlock(x, y - 1, z) == 0 || chunk.getBlock(x, y - 1, z) == BlockMaterial.WATER.id
        }
    }

    private fun isLeftFaceVisible(x: Int, y: Int, z: Int): Boolean {
        return if (x - 1 < 0) {
            true
        } else {
            chunk.getBlock(x - 1, y, z) == 0 || chunk.getBlock(x - 1, y, z) == BlockMaterial.WATER.id
        }
    }

    private fun isRightFaceVisible(x: Int, y: Int, z: Int): Boolean {
        return if (x + 1 >= 16) {
            true
        } else {
            chunk.getBlock(x + 1, y, z) == 0 || chunk.getBlock(x + 1, y, z) == BlockMaterial.WATER.id
        }
    }

    private fun isFrontFaceVisible(x: Int, y: Int, z: Int): Boolean {
        return if (z - 1 < 0) {
            true

        } else {
            chunk.getBlock(x, y, z - 1) == 0 || chunk.getBlock(x, y, z - 1) == BlockMaterial.WATER.id
        }
    }

    private fun isBackFaceVisible(x: Int, y: Int, z: Int): Boolean {
        return if (z + 1 >= 16) {
            true
        } else {
            chunk.getBlock(x, y, z + 1) == 0 || chunk.getBlock(x, y, z + 1) == BlockMaterial.WATER.id
        }
    }

    private fun addFace(faceDirection: FaceDirection, blockId: Int, coord: Vector3f) {
        val bakedModel = resourceManager.backedMeshes[BlockMaterial.valueOf(blockId)]?.faceMeshMap?.get(faceDirection)
        bakedModel?.let {
            val indexOffset =
                if (blockId == BlockMaterial.WATER.id) waterVerticesList.size / 3 else verticesList.size / 3
            var i = 0
            while (i < bakedModel.vertices.size) {
                if (blockId == BlockMaterial.WATER.id) {
                    waterVerticesList.add(bakedModel.vertices[i] + coord.x)
                    waterVerticesList.add(bakedModel.vertices[i + 1] + coord.y)
                    waterVerticesList.add(bakedModel.vertices[i + 2] + coord.z)
                } else {
                    verticesList.add(bakedModel.vertices[i] + coord.x)
                    verticesList.add(bakedModel.vertices[i + 1] + coord.y)
                    verticesList.add(bakedModel.vertices[i + 2] + coord.z)
                }
                i += 3
            }
            it.indices.forEach { index ->
                if (blockId == BlockMaterial.WATER.id) {
                    waterIndicesList.add(index + indexOffset)
                } else {
                    indicesList.add(index + indexOffset)
                }
            }
            if (blockId == BlockMaterial.WATER.id) {
                waterNormalsList.addAll(it.normals)
                waterUvsList.addAll(it.uvs)
                waterTangentsList.addAll(it.tangents)
            } else {
                normalsList.addAll(it.normals)
                uvsList.addAll(it.uvs)
                tangentsList.addAll(it.tangents)
            }
        }
    }

    private fun clearLists() {
        verticesList.clear()
        indicesList.clear()
        normalsList.clear()
        uvsList.clear()
        tangentsList.clear()
        waterVerticesList.clear()
        waterIndicesList.clear()
        waterNormalsList.clear()
        waterUvsList.clear()
        waterTangentsList.clear()
    }
}