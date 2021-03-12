package it.filippocavallari.cubicworld.manager

import it.filippocavallari.cubicworld.data.block.BakedMesh
import it.filippocavallari.cubicworld.data.block.Data
import it.filippocavallari.cubicworld.data.block.FaceDirection
import it.filippocavallari.cubicworld.graphic.mesh.BlockMaterial
import it.filippocavallari.cubicworld.json.ResourceParser
import it.filippocavallari.lwge.data.VBOsContainer
import it.filippocavallari.lwge.graphic.Material
import it.filippocavallari.lwge.graphic.Mesh
import it.filippocavallari.lwge.loader.OBJLoader
import it.filippocavallari.lwge.loader.TextureLoader
import it.filippocavallari.lwge.math.Math
import org.joml.Vector2f
import org.joml.Vector3f
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import kotlin.math.ceil
import kotlin.math.sqrt

class ResourceManager {

    val datas = HashMap<String, Data>()
    val textureAtlasCoordinates = HashMap<String, Vector2f>()
    val backedMeshes = HashMap<BlockMaterial, BakedMesh>()
    val material: Material

    init {
        generateTextureAtlas("src/main/resources/textures/atlas.png",Regex(".*[^_][^.]\\.png"), "src/main/resources/textures/blocks")
        generateTextureAtlas("src/main/resources/textures/normalAtlas.png",Regex(".*_n\\.png"),"src/main/resources/textures/blocks")
        generateTextureAtlas("src/main/resources/textures/depthAtlas.png",Regex(".*_h\\.png"),"src/main/resources/textures/blocks")
        val texture = TextureLoader.createTexture("src/main/resources/textures/atlas.png")
        val normalMap = TextureLoader.createTexture("src/main/resources/textures/normalAtlas.png")
        val depthMap = TextureLoader.createTexture("src/main/resources/textures/depthAtlas.png")
        material = Material(texture,normalMap,null,reflectance = 0f)
        loadData()
    }

    private fun loadData() {
        val dataFolder = File("src/main/resources/data/blocks")
        dataFolder.listFiles()?.forEach {
            val data = ResourceParser.parseDataFile(it)
            val mesh = OBJLoader.loadMesh("src/main/resources/models/${data.model}.obj",material)
            val offset = textureAtlasCoordinates[data.texture]
            offset?.let {
                mesh.uvs?.forEachIndexed { index, fl ->
                    mesh.uvs[index] = fl+if(index%2==0) it.x else it.y
                }
            }
            bakeMesh(mesh)
        }
    }

    private fun generateTextureAtlas(fileName: String, regex: Regex, textureFolderPath: String) {
        val textureFolder = File(textureFolderPath)
        val texturesList = textureFolder.listFiles()?.filter { it.name.matches(regex) }
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
            ImageIO.write(textureAtlas, "png", File(fileName))
        }
    }

    fun bakeMesh(mesh: Mesh): BakedMesh{
        var i = 0
        val indices = mesh.indices
        val vertices = mesh.vertices
        val normals = mesh.normals
        val uvs = mesh.uvs
        val map = HashMap<FaceDirection, VBOsContainer>()
        while(i < mesh.indices.size){
            val v1 = Vector3f(vertices[indices[i*3]],vertices[indices[i*3+1]],vertices[indices[i*3+2]])
            val v2 = Vector3f(vertices[indices[(i+1)*3]],vertices[indices[(i+1)*3+1]],vertices[indices[(i+1)*3+2]])
            val v3 = Vector3f(vertices[indices[(i+2)*3]],vertices[indices[(i+2)*3+1]],vertices[indices[(i+2)*3+2]])
            val normal = Math.calculateSurfaceNormal(v1,v2,v3)
            var face = FaceDirection.NORTH
            var prevDotProduct = normal.dot(FaceDirection.NORTH.normal)
            var dotProduct = normal.dot(FaceDirection.SOUTH.normal)
            if(dotProduct>prevDotProduct){
                prevDotProduct = dotProduct
                face = FaceDirection.SOUTH
            }
            dotProduct = normal.dot(FaceDirection.WEST.normal)
            if(dotProduct>prevDotProduct){
                prevDotProduct = dotProduct
                face = FaceDirection.WEST
            }
            dotProduct = normal.dot(FaceDirection.EAST.normal)
            if(dotProduct>prevDotProduct){
                prevDotProduct = dotProduct
                face = FaceDirection.EAST
            }
            dotProduct = normal.dot(FaceDirection.UP.normal)
            if(dotProduct>prevDotProduct){
                prevDotProduct = dotProduct
                face = FaceDirection.UP
            }
            dotProduct = normal.dot(FaceDirection.DOWN.normal)
            if(dotProduct>prevDotProduct){
                face = FaceDirection.DOWN
            }
            val vboContainer = map.getOrPut(face,{VBOsContainer()})
            vboContainer.vertices.addAll(vertices.copyOfRange(i*3,(i+2)*3+3).toList())
            vboContainer.indices.addAll(indices.copyOfRange(i,i+3).toList())
            vboContainer.normals.addAll(normals.copyOfRange(i*3,(i+2)*3+3).toList())
            uvs?.let {
                vboContainer.uvs.addAll(it.copyOfRange(i*2,(i+1)*2+2).toList())
            }
            i+=3
        }
        println(map[FaceDirection.NORTH]?.vertices?.size)
        return BakedMesh(map)
    }

}