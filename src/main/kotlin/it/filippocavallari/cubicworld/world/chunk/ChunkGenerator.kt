package it.filippocavallari.cubicworld.world.chunk

import it.filippocavallari.cubicworld.data.block.Material
import it.filippocavallari.cubicworld.noise.FastNoiseLite
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.joml.Vector2i

class ChunkGenerator(val seed: Int) {

    private val noise = FastNoiseLite()

    init {
        noise.SetNoiseType(FastNoiseLite.NoiseType.OpenSimplex2)
    }

    fun generateChunk(chunkX: Int, chunkZ: Int): Chunk{
        val waterLevel = 80
        val chunk = Chunk(Vector2i(chunkX,chunkZ))
        fun inFun(x: Int){
            for(z in 0..15){
                val height = sumOctave(16, chunkX*16+x, chunkZ*16+z, 0.01f,0.08f,0,100).toInt()
                for(y in 0..height){
                    val blockId = if(y == height) Material.GRASS.id else if(y >= height-5) Material.DIRT.id else Material.STONE.id
                    chunk.setBlock(x,y,z,blockId)
                }
                if(height <= waterLevel){
                    for (y in height..waterLevel){
                        chunk.setBlock(x,y,z,Material.WATER.id)
                    }
                }
            }
        }
        runBlocking {
            for(x in 0..15){
                launch {
                    inFun(Integer.valueOf(x)!!)
                }
            }
        }
        return chunk
    }

    private fun sumOctave(numIteration: Int, x: Int, y: Int, persistance: Float, scale: Float, low: Int, high: Int): Float {
        var maxAmp = 0.0f
        var amp = 1.0f
        var freq = scale
        var result = 0.0f
        for (i in 0 until numIteration) {
            result += noise.GetNoise(x.toFloat() * freq, y.toFloat() * freq) * amp
            maxAmp += amp
            amp *= persistance
            freq *= 2.0f
        }
        result /= maxAmp
        result = result * (high - low) + (high + low)
        return result
    }

}