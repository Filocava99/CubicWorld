package it.filippocavallari.cubicworld.world.chunk

import it.filippocavallari.cubicworld.noise.FastNoiseLite
import org.joml.Matrix3f
import org.joml.Matrix4f

class ChunkGenerator(val seed: Int) {

    val matrix: Matrix4f = Matrix4f()
    private val noise = FastNoiseLite()

    fun generateChunk(chunkX: Int, chunkZ: Int){
        val list = ArrayList<Float>()
        for(x in 0..15){
            for(z in 0..15){
                val height = sumOctave(16, chunkX, chunkZ, 0.005f,0.5f,60,120).toInt()
                for(y in 0..height){

                }
            }
        }
    }

    fun sumOctave(numIteration: Int, x: Int, y: Int, persistance: Float, scale: Float, low: Int, high: Int): Float {
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