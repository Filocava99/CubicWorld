package it.filippocavallari.cubicworld.data.block


enum class Material(val id: Int, val blockState: String) {
    AIR(0,""),
    DIRT(1,"dirt"),
    GRASS(2,"grass"),
    STONE(3,"stone"),
    COBBLESTONE(4,"cobblestone"),
    WATER(5,"water");

    companion object {
        fun valueOf(id: Int): Material{
            return values().filter { it.id == id }[0]
        }
    }
}