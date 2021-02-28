package it.filippocavallari.cubicworld.data.block

import it.filippocavallari.cubicworld.data.blockstate.BlockState

enum class Material(val id: Int, val blockState: String) {
    AIR(0,""),
    DIRT(1,"dirt"),
    COBBLESTONE(2,"cobblestone"),
    BRICK(3,"brick");
}