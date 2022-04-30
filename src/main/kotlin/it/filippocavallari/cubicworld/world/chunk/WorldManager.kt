package it.filippocavallari.cubicworld.world.chunk

import it.filippocavallari.cubicworld.data.block.Material
import it.filippocavallari.cubicworld.graphic.mesh.BlockMaterial
import it.filippocavallari.lwge.graphic.entity.Camera
import org.joml.Vector2f
import org.joml.Vector2i
import org.joml.Vector3f
import org.joml.Vector3i
import org.joml.primitives.Intersectionf
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.collections.HashMap
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min

class WorldManager {

    private val chunkGenerator = ChunkGenerator(Random().nextInt())
    var chunks = HashMap<Vector2i, Chunk>()
    val recentlyRemovedChunks = ConcurrentLinkedQueue<Chunk>()
    val recentlyModifiedChunks = ConcurrentLinkedQueue<Chunk>()
    val recentlyLoadedChunks = ConcurrentLinkedQueue<Chunk>()

    var selectedChunk: Chunk? = null
    var selectedBlock: Vector3i? = null
    val viewDistance = 7

    fun getBlock(coordinates: Vector3f): Int {
        return getBlock(Vector3i(coordinates.x.toInt(), coordinates.y.toInt(), coordinates.z.toInt()))
    }

    fun getBlock(coordinates: Vector3i): Int {
        if (coordinates.y < 0) return Material.DIRT.id
        if (coordinates.y > 255) return Material.AIR.id
        val chunkPosition = getChunkPosition(Vector3f(coordinates))
        val chunk = chunks[chunkPosition]
        chunk?.let {
            var blockX = floor(coordinates.x.toDouble()).toInt() % 16
            if (blockX < 0) blockX += 16
            var blockZ = floor(coordinates.z.toDouble()).toInt() % 16
            if (blockZ < 0) blockZ += 16
            return chunk.getBlock(
                blockX,
                floor(coordinates.y.toDouble()).toInt(),
                blockZ
            )
        }
        return Material.AIR.id
    }


    fun updateSelectedBlock(camera: Camera) {
        val playerPosition = camera.position
        val playerChunkPosition = getChunkPosition(playerPosition)
        val currentChunk = chunks[playerChunkPosition];
        val chunks = listOf(
            currentChunk,
            chunks[Vector2i(playerChunkPosition.x + 1, playerChunkPosition.y)],
            chunks[Vector2i(playerChunkPosition.x + 1, playerChunkPosition.y + 1)],
            chunks[Vector2i(playerChunkPosition.x + 1, playerChunkPosition.y - 1)],
            chunks[Vector2i(playerChunkPosition.x - 1, playerChunkPosition.y)],
            chunks[Vector2i(playerChunkPosition.x - 1, playerChunkPosition.y + 1)],
            chunks[Vector2i(playerChunkPosition.x - 1, playerChunkPosition.y - 1)],
            chunks[Vector2i(playerChunkPosition.x, playerChunkPosition.y + 1)],
            chunks[Vector2i(playerChunkPosition.x, playerChunkPosition.y - 1)]
        )
        var dir = Vector3f()
        dir = camera.viewMatrix.positiveZ(dir).negate()
        val min = Vector3f()
        val max = Vector3f()
        val nearFar = Vector2f()
        var closestDistance = Float.POSITIVE_INFINITY
        var newSelectedChunk: Chunk? = null
        var nearestBlock: Vector3i? = null
        for (chunk in chunks) {
            chunk?.let {
                for (x in 0..15) {
                    for (z in 0..15) {
                        for (y in max(0, playerPosition.y.toInt() - 5)..min(playerPosition.y.toInt() + 5, 255)) {
                            if (it.getBlock(x, y, z) != BlockMaterial.AIR.id) {
                                val blockCoordinates = chunkCoordinatesToWorldCoordinates(Vector3i(x,y,z), it.position)
                                min.set(
                                    Vector3f(
                                        blockCoordinates.x - 0.5f,
                                        blockCoordinates.y - 0.5f,
                                        blockCoordinates.z - 0.5f
                                    )
                                )
                                max.set(
                                    Vector3f(
                                        blockCoordinates.x + 0.5f,
                                        blockCoordinates.y,
                                        blockCoordinates.z + 0.5f
                                    )
                                )
                                if (Intersectionf.intersectRayAab(
                                        playerPosition,
                                        dir,
                                        min,
                                        max,
                                        nearFar
                                    ) && nearFar.x < closestDistance
                                ) {
                                    closestDistance = nearFar.x
                                    nearestBlock = Vector3i(x, y, z)
                                    newSelectedChunk = it
                                }
                            }
                        }
                    }
                }
            }
        }
        selectedBlock = nearestBlock
        selectedChunk = newSelectedChunk
    }

    fun updateActiveChunks(chunkPosition: Vector2i) {
        val xLowerBound = chunkPosition.x - viewDistance
        val xUpperBound = chunkPosition.x + viewDistance
        val zLowerBound = chunkPosition.y - viewDistance
        val zUpperBound = chunkPosition.y + viewDistance
        val iterator = chunks.entries.iterator()
        while (iterator.hasNext()) {
            val entry = iterator.next()
            val position = entry.key
            if (position.x < xLowerBound || position.x > xUpperBound || position.y < zLowerBound || position.y > zUpperBound) {
                iterator.remove()
                recentlyRemovedChunks.add(entry.value)
            }
        }
        for (x in xLowerBound..xUpperBound) {
            for (z in zLowerBound..zUpperBound) {
                val coords = Vector2i(x, z)
                if (!chunks.containsKey(coords)) {
                    recentlyLoadedChunks.add(generateChunk(Vector2i(x, z)))
                }
            }
        }
    }

    fun generateChunk(coordinates: Vector2i): Chunk {
        val chunk = chunkGenerator.generateChunk(coordinates.x, coordinates.y)
        chunks[coordinates] = chunk
        return chunk
    }

    companion object {
        fun getChunkPosition(coordinates: Vector3f): Vector2i {
            val chunkPosition = Vector2i()
            if (coordinates.x < 0) {
                chunkPosition.x = floor((-16 + coordinates.x) / 16).toInt()
            } else {
                chunkPosition.x = floor(coordinates.x / 16).toInt()
            }
            if (coordinates.z < 0) {
                chunkPosition.y = floor((-16 + coordinates.z) / 16).toInt()
            } else {
                chunkPosition.y = floor(coordinates.z / 16).toInt()
            }
            return Vector2i(floor(coordinates.x / 16).toInt(), floor(coordinates.z / 16).toInt())
        }

        fun chunkCoordinatesToWorldCoordinates(blockCoordinates: Vector3i, chunkCoordinates: Vector2i): Vector3f{
            val worldCoordinates = Vector3f()
            worldCoordinates.x = chunkCoordinates.x.toFloat() * 16 + blockCoordinates.x
//            if(chunkCoordinates.x < 0){
//                worldCoordinates.x += 16
//            }
            worldCoordinates.z = chunkCoordinates.y.toFloat() * 16 + blockCoordinates.z
//            if(chunkCoordinates.y < 0){
//                worldCoordinates.z += 16
//            }
            worldCoordinates.y = blockCoordinates.y.toFloat()
            return worldCoordinates
        }
    }
}