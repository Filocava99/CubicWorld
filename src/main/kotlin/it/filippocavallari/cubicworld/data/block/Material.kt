package it.filippocavallari.cubicworld.data.block


enum class Material(val id: Int, val blockState: String) {
    AIR(0,""),
    DIRT(1,"dirt"),
    COBBLESTONE(2,"cobblestone"),
    BRICK(3,"brick");

    companion object {
        fun valueOf(id: Int): Material{
            return values().filter { it.id == id }[0]
        }
    }
}