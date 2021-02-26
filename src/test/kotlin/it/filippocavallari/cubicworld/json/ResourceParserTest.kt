package it.filippocavallari.cubicworld.json

import com.google.gson.Gson
import org.junit.jupiter.api.Test
import java.io.File

internal class ResourceParserTest {

    @Test
    fun parseBlockState() {
        val blockState = ResourceParser.parseBlockState(File("src/test/resources/blockstates/dirt.json"))
        assert(blockState.variants[""]?.get(0)?.model.equals("block/dirt"))
    }

    @Test
    fun parseModel() {
        val model = ResourceParser.parseModel(File("src/test/resources/models/dirt.json"))
        assert(model.textures["all"].equals("block/dirt"))
    }
}