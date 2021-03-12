package it.filippocavallari.cubicworld.world.chunk

import it.filippocavallari.cubicworld.data.block.Block

class Chunk(private val blocks: Array<Block> = Array<Block>(65536) { Block(0) }) {

    fun setBlock(x: Int, y: Int, z: Int, blockId: Int){
        blocks[x + y*256 + z*16].id = blockId
    }

    fun getBlock(x: Int, y: Int, z: Int): Int{
        return blocks[x + y*256 + z*16].id
    }

}