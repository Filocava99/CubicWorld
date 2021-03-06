package it.filippocavallari.cubicworld.manager

import it.filippocavallari.cubicworld.data.blockstate.BlockState
import it.filippocavallari.cubicworld.data.model.Model
import it.filippocavallari.cubicworld.json.ResourceParser
import org.joml.Vector2f
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import kotlin.math.ceil
import kotlin.math.sqrt

class ResourceManager {

    val models = HashMap<String, Model>()
    val blockStates = HashMap<String, BlockState>()
    val textureAtlasCoordinates = HashMap<String,Vector2f>()

    init{
        loadModels()
        loadBlockStates()
        generateTextureAtlas()
    }

    private fun loadModels(){
        val modelsFolder = File("src/main/resources/models")
        modelsFolder.listFiles()?.forEach {
            models[it.name.split(".")[0]] = ResourceParser.parseModel(it)
        }
    }

    private fun loadBlockStates(){
        val blockStatesFolder = File("src/main/resources/blockstates")
        blockStatesFolder.listFiles()?.forEach {
            blockStates[it.name.split(".")[0]] = ResourceParser.parseBlockState(it)
        }
    }

    private fun generateTextureAtlas(){
        val textureFolder = File("src/main/resources/textures/blocks")
        val texturesList = textureFolder.listFiles()?.filter { !it.name.endsWith("_n.png") }
        val singleTextureSize = 128
        texturesList?.let {
            val imagePerRow = ceil(sqrt(texturesList.size.toDouble())).toInt()
            val textureAtlasSize = imagePerRow * singleTextureSize
            val textureAtlas = BufferedImage(textureAtlasSize, textureAtlasSize,BufferedImage.TYPE_INT_ARGB)
            val graphics = textureAtlas.graphics
            texturesList.forEachIndexed{index, file ->
                val texture = ImageIO.read(file)
                val xCoordInTextureAtlas = index%imagePerRow*singleTextureSize
                val yCoordInTextureAtlas = index/imagePerRow*singleTextureSize
                graphics.drawImage(texture,xCoordInTextureAtlas,yCoordInTextureAtlas,null)
                textureAtlasCoordinates[file.name.split(".")[0]] = Vector2f(xCoordInTextureAtlas.toFloat()/textureAtlasSize,yCoordInTextureAtlas.toFloat()/textureAtlasSize)
            }
            ImageIO.write(textureAtlas,"png",File("src/main/resources/textures/atlas.png"))
        }
    }

}