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

    val recentModifiedChunks = ConcurrentLinkedQueue<Chunk>()
    private val chunkGenerator = ChunkGenerator(Random().nextInt())
    var chunks = HashMap<Vector2i, Chunk>()
    val recentlyRemovedChunks = ConcurrentLinkedQueue<Chunk>()

    var selectedChunk: Chunk? = null
    var selectedBlock: Vector3i? = null
    val viewDistance = 10

    fun getBlock(coordinates: Vector3f): Int {
        return getBlock(Vector3i(coordinates.x.toInt(), coordinates.y.toInt(), coordinates.z.toInt()))
    }

    fun getBlock(coordinates: Vector3i): Int {
        if (coordinates.y < 0) return Material.DIRT.id
        if (coordinates.y > 255) return Material.AIR.id
        val chunkPosition =
            Vector2i(floor(coordinates.x.toDouble()).toInt() / 16, floor(coordinates.z.toDouble()).toInt() / 16)
        val chunk = chunks[chunkPosition]
        chunk?.let {
            var blockX = floor(coordinates.x.toDouble()).toInt() % 16
            if(blockX < 0) blockX += 16
            var blockZ = floor(coordinates.z.toDouble()).toInt() % 16
            if(blockZ < 0) blockZ += 16
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
        for (chunk in chunks) {
            chunk?.let {
                min.set(Vector3f(chunk.position.x.toFloat() * 16, 0f, chunk.position.y.toFloat()))
                max.set(Vector3f((1f + chunk.position.x.toFloat()) * 16, 255f, (chunk.position.y.toFloat() + 1f) * 16))
                if (Intersectionf.intersectRayAab(
                        playerPosition,
                        dir,
                        min,
                        max,
                        nearFar
                    ) && nearFar.x < closestDistance
                ) {
                    closestDistance = nearFar.x
                    newSelectedChunk = chunk
                }
            }
        }
        selectedChunk = newSelectedChunk
        selectedChunk?.let {
            nearFar.zero()
            var nearestBlock: Vector3i? = null
            closestDistance = Float.POSITIVE_INFINITY
            for (x in 0..15) {
                for (z in 0..15) {
                    for (y in max(0, playerPosition.y.toInt() - 5)..min(playerPosition.y.toInt() + 5, 255)) {
                        if (it.getBlock(x, y, z) != BlockMaterial.AIR.id) {
                            min.set(
                                Vector3f(
                                    it.position.x.toFloat() * 16 + x - 0.5f,
                                    y.toFloat() - 0.5f,
                                    it.position.y.toFloat() + z - 0.5f
                                )
                            )
                            max.set(
                                Vector3f(
                                    it.position.x.toFloat() * 16 + x + 0.5f,
                                    y + 0.5f,
                                    it.position.y.toFloat() * 16 + z + 0.5f
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
                            }
                        }
                    }
                }
            }
            selectedBlock = nearestBlock
        }
    }

    fun updateActiveChunks(chunkPosition: Vector2i){
        val xLowerBound = chunkPosition.x-viewDistance
        val xUpperBound = chunkPosition.x+viewDistance
        val zLowerBound = chunkPosition.y-viewDistance
        val zUpperBound = chunkPosition.y+viewDistance
        val iterator = chunks.entries.iterator()
        while(iterator.hasNext()){
            val entry = iterator.next()
            val position = entry.key
            if(position.x < xLowerBound || position.x > xUpperBound || position.y < zLowerBound || position.y > zUpperBound){
                iterator.remove()
                recentlyRemovedChunks.add(entry.value)
            }
        }
        for (x in xLowerBound..xUpperBound){
            for(z in zLowerBound..zUpperBound){
                val coords = Vector2i(x,z)
                if (!chunks.containsKey(coords)){
                    recentModifiedChunks.add(generateChunk(Vector2i(x,z)))
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
        fun getChunkPosition(coordinates: Vector3f): Vector2i{
            return Vector2i(floor(coordinates.x/16).toInt(), floor(coordinates.z/16).toInt())
        }
    }
}