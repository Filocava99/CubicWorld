package it.filippocavallari.cubicworld.world.chunk

class Chunk(private val blocks: IntArray = IntArray(65536) { 0 }) {

    fun setBlock(x: Int, y: Int, z: Int, blockId: Int){
        blocks[x + y*256 + z*16] = blockId
    }

    fun getBlock(x: Int, y: Int, z: Int): Int{
        return blocks[x + y*256 + z*16]
    }

}