package it.filippocavallari.cubicworld.world.chunk

import it.filippocavallari.cubicworld.noise.FastNoiseLite
import org.joml.Matrix3f
import org.joml.Matrix4f

class ChunkGenerator(val seed: Int) {

    private val noise = FastNoiseLite()

    init {
        noise.SetNoiseType(FastNoiseLite.NoiseType.OpenSimplex2)
    }

    fun generateChunk(chunkX: Int, chunkZ: Int): Chunk{
        val chunk = Chunk()
        for(x in 0..15){
            for(z in 0..15){
                val height = sumOctave(16, chunkX*16+x, chunkZ*16+z, 0.1f,0.3f,30,70).toInt()
                for(y in 0..height){
                    chunk.setBlock(x,y,z,1)
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