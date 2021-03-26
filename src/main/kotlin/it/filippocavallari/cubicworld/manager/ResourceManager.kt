package it.filippocavallari.cubicworld.manager

import it.filippocavallari.cubicworld.data.block.BakedMesh
import it.filippocavallari.cubicworld.data.block.Block
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
//        generateTextureAtlas("src/main/resources/textures/atlas.png",Regex(".*[^_][^.]\\.png"), "src/main/resources/textures/blocks")
//        generateTextureAtlas("src/main/resources/textures/normalAtlas.png",Regex(".*_n\\.png"),"src/main/resources/textures/blocks")
//        generateTextureAtlas("src/main/resources/textures/depthAtlas.png",Regex(".*_h\\.png"),"src/main/resources/textures/blocks")
//        val texture = TextureLoader.createTexture("src/main/resources/textures/atlas.png")
//        val normalMap = TextureLoader.createTexture("src/main/resources/textures/normalAtlas.png")
//        val depthMap = TextureLoader.createTexture("src/main/resources/textures/depthAtlas.png")
        material = Material(null,null,null,reflectance = 0f)
        loadData()
    }

    private fun loadData() {
        val dataFolder = File("src/main/resources/data/blocks")
        dataFolder.listFiles()?.forEach {
            val data = ResourceParser.parseDataFile(it)
            val mesh = OBJLoader.loadMesh("src/main/resources/models/${data.model}.obj",material)
//            val offset = textureAtlasCoordinates[data.texture]
//            offset?.let {
//                mesh.uvs?.forEachIndexed { index, fl ->
//                    mesh.uvs[index] = fl+if(index%2==0) it.x else it.y
//                }
//            }
            backedMeshes[BlockMaterial.valueOf(data.id)] = bakeMesh(mesh)

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

    private fun bakeMesh(mesh: Mesh): BakedMesh{
        var i = 0
        val indices = mesh.indices
        val vertices = mesh.vertices
        val normals = mesh.normals
        val uvs = mesh.uvs!!
        val map = HashMap<FaceDirection, VBOsContainer>()
        while(i < mesh.indices.size){
            val v1x = indices[i]*3
            val v1y = v1x+1
            val v1z = v1x+2
            val v1 = Vector3f(vertices[v1x],vertices[v1y],vertices[v1z])
            val normal1 = Vector3f(normals[v1x],normals[v1y],normals[v1z])
            val uv1 = Vector2f(uvs[indices[i]*2], uvs[indices[i]*2+1])
            val v2x = indices[i+1]*3
            val v2y = v2x+1
            val v2z = v2x+2
            val v2 = Vector3f(vertices[v2x],vertices[v2y],vertices[v2z])
            val normal2 = Vector3f(normals[v2x],normals[v2y],normals[v2z])
            val uv2 = Vector2f(uvs[indices[i+1]*2], uvs[indices[i+1]*2+1])
            val v3x = indices[i+2]*3
            val v3y = v3x+1
            val v3z = v3x+2
            val v3 = Vector3f(vertices[v3x],vertices[v3y],vertices[v3z])
            val normal3 = Vector3f(normals[v3x],normals[v3y],normals[v3z])
            val uv3 = Vector2f(uvs[indices[i+2]*2], uvs[indices[i+2]*2+1])
            val tangent = Math.calculateNormalTangents(v1,v2,v3,uv1,uv2,uv3)
            val surfaceNormal = Math.calculateSurfaceNormal(v1,v2,v3)
            var face = FaceDirection.NORTH
            var prevDotProduct = surfaceNormal.dot(FaceDirection.NORTH.normal)
            var dotProduct = surfaceNormal.dot(FaceDirection.SOUTH.normal)
            if(dotProduct>prevDotProduct){
                prevDotProduct = dotProduct
                face = FaceDirection.SOUTH
            }
            dotProduct = surfaceNormal.dot(FaceDirection.WEST.normal)
            if(dotProduct>prevDotProduct){
                prevDotProduct = dotProduct
                face = FaceDirection.WEST
            }
            dotProduct = surfaceNormal.dot(FaceDirection.EAST.normal)
            if(dotProduct>prevDotProduct){
                prevDotProduct = dotProduct
                face = FaceDirection.EAST
            }
            dotProduct = surfaceNormal.dot(FaceDirection.UP.normal)
            if(dotProduct>prevDotProduct){
                prevDotProduct = dotProduct
                face = FaceDirection.UP
            }
            dotProduct = surfaceNormal.dot(FaceDirection.DOWN.normal)
            if(dotProduct>prevDotProduct){
                face = FaceDirection.DOWN
            }
            val vboContainer = map.getOrPut(face,{VBOsContainer()})
            val indexOffset = vboContainer.vertices.size/3
            vboContainer.indices.addAll(listOf(indexOffset,indexOffset+1,indexOffset+2))
            vboContainer.vertices.addAll(listOf(v1.x,v1.y,v1.z,v2.x,v2.y,v2.z,v3.x,v3.y,v3.z))
            vboContainer.normals.addAll(listOf(normal1.x,normal1.y,normal1.z,normal2.x,normal2.y,normal2.z,normal3.x,normal3.y,normal3.z))
            vboContainer.uvs.addAll(listOf(uv1.x,uv1.y,uv2.x,uv2.y,uv3.x,uv3.y))
            vboContainer.tangents.addAll(listOf(tangent.x,tangent.y,tangent.z,tangent.x,tangent.y,tangent.z,tangent.x,tangent.y,tangent.z))
            i+=3
        }
        return BakedMesh(map)
    }

}