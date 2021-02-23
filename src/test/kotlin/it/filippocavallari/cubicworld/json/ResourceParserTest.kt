package it.filippocavallari.cubicworld.json

import org.junit.jupiter.api.Test

internal class ResourceParserTest {

    @Test
    fun parseBlockState() {
        val blockState = ResourceParser.parseBlockState("dirt.json")
        assert(blockState.variants[""]?.get(0)?.model.equals("block/dirt"))
    }

    @Test
    fun parseModel() {
        val model = ResourceParser.parseModel("dirt.json")
        assert(model.textures["all"].equals("block/dirt"))
    }
}