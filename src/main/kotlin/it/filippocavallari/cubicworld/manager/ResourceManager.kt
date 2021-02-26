package it.filippocavallari.cubicworld.manager

import it.filippocavallari.cubicworld.data.blockstate.BlockState
import it.filippocavallari.cubicworld.data.model.Model
import it.filippocavallari.cubicworld.json.ResourceParser
import java.io.File

class ResourceManager {

    val models = HashMap<String, Model>()
    val blockStates = HashMap<String, BlockState>()

    init{
        val modelsFolder = File("src/main/resources/models")
        val blockStatesFolder = File("src/main/resources/blockstates")
        modelsFolder.listFiles()?.forEach {
            models[it.name.split(".")[0]] = ResourceParser.parseModel(it)
        }
        blockStatesFolder.listFiles()?.forEach {
            blockStates[it.name.split(".")[0]] = ResourceParser.parseBlockState(it)
        }
    }

}