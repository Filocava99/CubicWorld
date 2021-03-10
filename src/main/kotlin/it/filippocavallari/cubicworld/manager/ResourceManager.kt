package it.filippocavallari.cubicworld.manager

import it.filippocavallari.cubicworld.data.block.BakedModel
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
    val textureAtlasCoordinates = HashMap<String, Vector2f>()
    val backedModels = HashMap<String, BakedModel>()

    init {
        loadModels()
        loadBlockStates()
        generateTextureAtlas()
    }

    private fun loadModels() {
        val modelsFolder = File("src/main/resources/models")
        modelsFolder.listFiles()?.forEach {
            models[it.name.split(".")[0]] = ResourceParser.parseModel(it)
        }
        models.forEach { (name, model) ->
            loadBackedModel(name, model)
        }
    }

    private fun loadBackedModel(modelName: String, model: Model) {
        val bakedModel = BakedModel()
        if (model.parent != null) {
            models[model.parent]?.let { loadBackedModel(model.parent, it) }
        }
        val vertices = HashMap<String, List<Float>>()
        val indices  = HashMap<String, List<Int>>()
        val normals =  HashMap<String, List<Float>>()
        model.elements.forEach { element ->
            element.faces.keys.forEach { face ->
                val verticesList = addFaceVertices(element.from, element.to, face)
                val finalVerticesList = vertices[face]
                if(finalVerticesList != null){
                    finalVerticesList.plus(verticesList)
                }else{
                    vertices[face] = verticesList
                }
                val indicesList = addFaceIndices(vertices[face]!!.size)
                val finalIndicesList = indices[face]
                if(finalIndicesList !=  null){
                    finalIndicesList.plus(indicesList)
                }else{
                    indices[face] = indicesList
                }
                val normalsList = addFaceNormals(face)
                val finalNormalsList = normals[face]
                if(finalNormalsList != null){
                    finalNormalsList.plus(normalsList)
                }else{
                    normals[face] = normalsList
                }
            }
        }
    }

    private fun addFaceVertices(from: FloatArray, to: FloatArray, face: String): List<Float> {
        val verticesList = ArrayList<Float>()
        when (face) {
            "down" -> { //Y is constant. X and Z change
                //v1
                verticesList.add(from[0])//x
                verticesList.add(from[1])//y
                verticesList.add(from[2])//z
                //v2
                verticesList.add(from[0])//x
                verticesList.add(from[1])//y
                verticesList.add(to[2])//z
                //v3
                verticesList.add(to[0])
                verticesList.add(from[1])
                verticesList.add(to[2])
                //v4
                verticesList.add(to[0])
                verticesList.add(from[1])
                verticesList.add(from[2])

            }
            "up" -> { //Y is constant. X and Z change
                //v1
                verticesList.add(from[0])//x
                verticesList.add(to[1])//y
                verticesList.add(to[2])//z
                //v2
                verticesList.add(from[0])//x
                verticesList.add(to[1])//y
                verticesList.add(from[2])//z
                //v3
                verticesList.add(to[0])
                verticesList.add(to[1])
                verticesList.add(from[2])
                //v4
                verticesList.add(to[0])
                verticesList.add(to[1])
                verticesList.add(to[2])
            }
            "north" -> { //Z is constant. X and Y change
                //v1
                verticesList.add(to[0])//x
                verticesList.add(to[1])//y
                verticesList.add(to[2])//z
                //v2
                verticesList.add(to[0])//x
                verticesList.add(from[1])//y
                verticesList.add(to[2])//z
                //v3
                verticesList.add(from[0])
                verticesList.add(from[1])
                verticesList.add(to[2])
                //v4
                verticesList.add(from[0])
                verticesList.add(to[1])
                verticesList.add(to[2])
            }
            "south" -> { //Z is constant. X and y change
                //v1
                verticesList.add(from[0])//x
                verticesList.add(to[1])//y
                verticesList.add(from[2])//z
                //v2
                verticesList.add(from[0])//x
                verticesList.add(from[1])//y
                verticesList.add(from[2])//z
                //v3
                verticesList.add(to[0])
                verticesList.add(from[1])
                verticesList.add(from[2])
                //v4
                verticesList.add(to[0])
                verticesList.add(to[1])
                verticesList.add(from[2])
            }
            "west" -> { //X is constant. Y and Z change
                //v1
                verticesList.add(from[0])//x
                verticesList.add(to[1])//y
                verticesList.add(to[2])//z
                //v2
                verticesList.add(from[0])//x
                verticesList.add(from[1])//y
                verticesList.add(to[2])//z
                //v3
                verticesList.add(from[0])
                verticesList.add(from[1])
                verticesList.add(from[2])
                //v4
                verticesList.add(from[0])
                verticesList.add(to[1])
                verticesList.add(from[2])
            }
            "east" -> { //X is constant. Y and Z change
                //v1
                verticesList.add(to[0])//x
                verticesList.add(to[1])//y
                verticesList.add(from[2])//z
                //v2
                verticesList.add(to[0])//x
                verticesList.add(from[1])//y
                verticesList.add(from[2])//z
                //v3
                verticesList.add(to[0])
                verticesList.add(from[1])
                verticesList.add(to[2])
                //v4
                verticesList.add(to[0])
                verticesList.add(to[1])
                verticesList.add(to[2])
            }
        }
        return verticesList;
    }

    private fun addFaceIndices(offset: Int): List<Int> {
        return listOf(offset, offset + 3, offset + 1, offset + 3, offset + 2, offset + 1)
    }

    private fun addFaceNormals(face: String):List<Float>{
        val normalsList = ArrayList<Float>()
        when (face) {
            "down" -> { //Y is constant. X and Z change
                //v1
                normalsList.add(0f)//x
                normalsList.add(-1f)//y
                normalsList.add(0f)//z
                //v2
                normalsList.add(0f)//x
                normalsList.add(-1f)//y
                normalsList.add(0f)//z
                //v3
                normalsList.add(0f)//x
                normalsList.add(-1f)//y
                normalsList.add(0f)//z
                //v4
                normalsList.add(0f)//x
                normalsList.add(-1f)//y
                normalsList.add(0f)//z
            }
            "up" -> { //Y is constant. X and Z change
                //v1
                normalsList.add(0f)//x
                normalsList.add(1f)//y
                normalsList.add(0f)//z
                //v2
                normalsList.add(0f)//x
                normalsList.add(1f)//y
                normalsList.add(0f)//z
                //v3
                normalsList.add(0f)//x
                normalsList.add(1f)//y
                normalsList.add(0f)//z
                //v4
                normalsList.add(0f)//x
                normalsList.add(1f)//y
                normalsList.add(0f)//z
            }
            "north" -> { //Z is constant. X and Y change
                //v1
                normalsList.add(0f)//x
                normalsList.add(0f)//y
                normalsList.add(1f)//z
                //v2
                normalsList.add(0f)//x
                normalsList.add(0f)//y
                normalsList.add(1f)//z
                //v3
                normalsList.add(0f)//x
                normalsList.add(0f)//y
                normalsList.add(1f)//z
                //v4
                normalsList.add(0f)//x
                normalsList.add(0f)//y
                normalsList.add(1f)//z
            }
            "south" -> { //Z is constant. X and y change
                //v1
                normalsList.add(0f)//x
                normalsList.add(0f)//y
                normalsList.add(-1f)//z
                //v2
                normalsList.add(0f)//x
                normalsList.add(0f)//y
                normalsList.add(-1f)//z
                //v3
                normalsList.add(0f)//x
                normalsList.add(0f)//y
                normalsList.add(-1f)//z
                //v4
                normalsList.add(0f)//x
                normalsList.add(0f)//y
                normalsList.add(-1f)//z
            }
            "west" -> { //X is constant. Y and Z change
                //v1
                normalsList.add(-1f)//x
                normalsList.add(0f)//y
                normalsList.add(0f)//z
                //v2
                normalsList.add(-1f)//x
                normalsList.add(0f)//y
                normalsList.add(0f)//z
                //v3
                normalsList.add(-1f)//x
                normalsList.add(0f)//y
                normalsList.add(0f)//z
                //v4
                normalsList.add(-1f)//x
                normalsList.add(0f)//y
                normalsList.add(0f)//z
            }
            "east" -> { //X is constant. Y and Z change
                //v1
                normalsList.add(1f)//x
                normalsList.add(0f)//y
                normalsList.add(0f)//z
                //v2
                normalsList.add(1f)//x
                normalsList.add(0f)//y
                normalsList.add(0f)//z
                //v3
                normalsList.add(1f)//x
                normalsList.add(0f)//y
                normalsList.add(0f)//z
                //v4
                normalsList.add(1f)//x
                normalsList.add(0f)//y
                normalsList.add(0f)//z
            }
        }
        return normalsList
    }

    private fun loadBlockStates() {
        val blockStatesFolder = File("src/main/resources/blockstates")
        blockStatesFolder.listFiles()?.forEach {
            blockStates[it.name.split(".")[0]] = ResourceParser.parseBlockState(it)
        }
    }

    private fun generateTextureAtlas() {
        val textureFolder = File("src/main/resources/textures/blocks")
        val texturesList = textureFolder.listFiles()?.filter { !it.name.endsWith("_n.png") }
        val singleTextureSize = 128
        texturesList?.let {
            val imagePerRow = ceil(sqrt(texturesList.size.toDouble())).toInt()
            val textureAtlasSize = imagePerRow * singleTextureSize
            val textureAtlas = BufferedImage(textureAtlasSize, textureAtlasSize, BufferedImage.TYPE_INT_ARGB)
            val graphics = textureAtlas.graphics
            texturesList.forEachIndexed { index, file ->
                val texture = ImageIO.read(file)
                val xCoordInTextureAtlas = index % imagePerRow * singleTextureSize
                val yCoordInTextureAtlas = index / imagePerRow * singleTextureSize
                graphics.drawImage(texture, xCoordInTextureAtlas, yCoordInTextureAtlas, null)
                textureAtlasCoordinates[file.name.split(".")[0]] = Vector2f(
                    xCoordInTextureAtlas.toFloat() / textureAtlasSize,
                    yCoordInTextureAtlas.toFloat() / textureAtlasSize
                )
            }
            ImageIO.write(textureAtlas, "png", File("src/main/resources/textures/atlas.png"))
        }
    }

}