package it.filippocavallari.cubicworld.graphic.mesh

import it.filippocavallari.cubicworld.world.chunk.Chunk
import it.filippocavallari.lwge.graphic.Material
import it.filippocavallari.lwge.graphic.Mesh
import it.filippocavallari.lwge.loader.Loader
import it.filippocavallari.lwge.loader.TextureLoader
import org.joml.Vector2f
import org.joml.Vector3f
import org.lwjgl.opengl.GL30C.glBindVertexArray
import java.util.*

class ChunkMesh(val chunk: Chunk) {

    private val verticesList = LinkedList<Vector3f>()
    private val indicesList = LinkedList<Int>()
    private val normalsList = LinkedList<Vector3f>()
    private val uvsList = LinkedList<Vector2f>()

    fun buildMesh(): Mesh {
        for (x in 0..15) {
            for (z in 0..15) {
                for (y in 0..255) {
                    val blockId = chunk.getBlock(x, y, z)
                    if (blockId != 0) {
                        if (isTopFaceVisible(x,y,z)){
                            addTopFace(x,y,z)
                        }
                        if(isBottomFaceVisible(x,y,z)){
                            addBottomFace(x,y,z)
                        }
                        if(isFrontFaceVisible(x,y,z)){
                            addFrontFace(x, y, z)
                        }
                        if(isBackFaceVisible(x,y,z)){
                            addBackFace(x,y,z)
                        }
                        if(isLeftFaceVisible(x,y,z)){
                            addLeftFace(x,y,z)
                        }
                        if(isRightFaceVisible(x,y,z)){
                            addRightFace(x,y,z)
                        }
                    }
                }
            }
        }
        val verticesArray = FloatArray(verticesList.size*3)
        verticesList.forEachIndexed { index, vector3f ->
            verticesArray[index*3] = vector3f.x
            verticesArray[index*3+1] = vector3f.y
            verticesArray[index*3+2] = vector3f.z
        }
//        var i = 0
//        verticesArray.forEach {
//            print(it.toString() + " ")
//            i++
//            if(i==3){
//                i = 0;
//                println()
//            }
//        }
        val indicesArray = IntArray(indicesList.size)
        indicesList.forEachIndexed{index, int -> indicesArray[index] = int}
        val normalsArray = FloatArray(normalsList.size * 3)
        normalsList.forEachIndexed { index, vector3f ->
            normalsArray[index*3] = vector3f.x
            normalsArray[index*3+1] = vector3f.y
            normalsArray[index*3+2] = vector3f.z
        }
        val uvsArray = FloatArray(uvsList.size*2)
        uvsList.forEachIndexed { index, vector2f ->
            uvsArray[index*2] = vector2f.x
            uvsArray[index*2+1] = vector2f.y
        }
        val vao = Loader.getVAO()
        val verticesVbo = Loader.getVBO()
        val indicesVbo = Loader.getVBO()
        val normalsVbo = Loader.getVBO()
        val uvsVbo = Loader.getVBO()
        glBindVertexArray(vao.id)
        Loader.loadVerticesInVbo(verticesVbo,verticesArray)
        Loader.loadIndicesInVbo(indicesVbo, indicesArray)
        Loader.loadNormalsInVbo(normalsVbo, normalsArray)
        Loader.loadUVsInVbo(uvsVbo, uvsArray)
        glBindVertexArray(0)
        val texture = TextureLoader.createTexture("src/main/resources/textures/blocks/dirt.png")
        val normalMap = TextureLoader.createTexture("src/main/resources/textures/blocks/dirt_n.png")
        val material = Material(texture, normalMap, reflectance = 0f)
        val mesh = Mesh(
            material,
            vao,
            mutableSetOf(verticesVbo, indicesVbo, normalsVbo),
            mutableSetOf(uvsVbo),
            indicesArray.size,
        )
        return mesh
    }

    private fun isTopFaceVisible(x: Int, y: Int, z: Int): Boolean {
        return if(y+1>=255){
            true
        } else{
            chunk.getBlock(x, y + 1, z) == 0
        }
    }

    private fun isBottomFaceVisible(x: Int, y: Int, z: Int): Boolean {
        return if(y-1 <0){
            true

        } else{
            chunk.getBlock(x, y - 1, z) == 0
        }
    }

    private fun isLeftFaceVisible(x: Int, y: Int, z: Int): Boolean {
        return if(x-1<0){
            true
        } else{
            chunk.getBlock(x-1, y, z) == 0
        }
    }

    private fun isRightFaceVisible(x: Int, y: Int, z: Int): Boolean {
        return if(x+1>=16){
            true
        }else{
            chunk.getBlock(x+1, y, z) == 0
        }
    }

    private fun isFrontFaceVisible(x: Int, y: Int, z: Int): Boolean {
        return if(z-1<0){
            true

        } else{
            chunk.getBlock(x, y, z-1) == 0
        }
    }

    private fun isBackFaceVisible(x: Int, y: Int, z: Int): Boolean {
        return if(z+1>=16){
            true
        } else{
            chunk.getBlock(x, y, z+1) == 0
        }
    }

    private fun addTopFace(x: Int, y: Int, z: Int){
        val currentVerticesListSize = verticesList.size
        //Vertices
        verticesList.add(Vector3f(-0.5f+x,0.5f+y,0.5f+z))
        verticesList.add(Vector3f(-0.5f+x,0.5f+y,-0.5f+z))
        verticesList.add(Vector3f(0.5f+x,0.5f+y,-0.5f+z))
        verticesList.add(Vector3f(0.5f+x,0.5f+y,0.5f+z))
        //Indices
        indicesList.add(currentVerticesListSize)
        indicesList.add(currentVerticesListSize+1)
        indicesList.add(currentVerticesListSize+3)
        indicesList.add(currentVerticesListSize+3)
        indicesList.add(currentVerticesListSize+1)
        indicesList.add(currentVerticesListSize+2)
        //Normals
        normalsList.add(Vector3f(0f,1f,0f))
        normalsList.add(Vector3f(0f,1f,0f))
        normalsList.add(Vector3f(0f,1f,0f))
        normalsList.add(Vector3f(0f,1f,0f))
        //UVs
        uvsList.add(Vector2f(0f,0f))
        uvsList.add(Vector2f(0f,1f))
        uvsList.add(Vector2f(1f,1f))
        uvsList.add(Vector2f(1f,0f))
    }

    private fun addBottomFace(x: Int, y: Int, z: Int){
        val currentVerticesListSize = verticesList.size
        //Vertices
        verticesList.add(Vector3f(-0.5f+x,-0.5f+y,-0.5f+z))
        verticesList.add(Vector3f(-0.5f+x,-0.5f+y,0.5f+z))
        verticesList.add(Vector3f(0.5f+x,-0.5f+y,0.5f+z))
        verticesList.add(Vector3f(0.5f+x,-0.5f+y,-0.5f+z))
        //Indices
        indicesList.add(currentVerticesListSize)
        indicesList.add(currentVerticesListSize+1)
        indicesList.add(currentVerticesListSize+3)
        indicesList.add(currentVerticesListSize+3)
        indicesList.add(currentVerticesListSize+1)
        indicesList.add(currentVerticesListSize+2)
        //Normals
        normalsList.add(Vector3f(0f,-1f,0f))
        normalsList.add(Vector3f(0f,-1f,0f))
        normalsList.add(Vector3f(0f,-1f,0f))
        normalsList.add(Vector3f(0f,-1f,0f))
        //UVs
        uvsList.add(Vector2f(0f,0f))
        uvsList.add(Vector2f(0f,1f))
        uvsList.add(Vector2f(1f,1f))
        uvsList.add(Vector2f(1f,0f))
    }

    private fun addFrontFace(x: Int, y: Int, z: Int){
        val currentVerticesListSize = verticesList.size
        //Vertices
        verticesList.add(Vector3f(-0.5f+x,0.5f+y,-0.5f+z))
        verticesList.add(Vector3f(-0.5f+x,-0.5f+y,-0.5f+z))
        verticesList.add(Vector3f(0.5f+x,-0.5f+y,-0.5f+z))
        verticesList.add(Vector3f(0.5f+x,0.5f+y,-0.5f+z))
        //Indices
        indicesList.add(currentVerticesListSize)
        indicesList.add(currentVerticesListSize+1)
        indicesList.add(currentVerticesListSize+3)
        indicesList.add(currentVerticesListSize+3)
        indicesList.add(currentVerticesListSize+1)
        indicesList.add(currentVerticesListSize+2)
        //Normals
        normalsList.add(Vector3f(0f,0f,-1f))
        normalsList.add(Vector3f(0f,0f,-1f))
        normalsList.add(Vector3f(0f,0f,-1f))
        normalsList.add(Vector3f(0f,0f,-1f))
        //UVs
        uvsList.add(Vector2f(0f,0f))
        uvsList.add(Vector2f(0f,1f))
        uvsList.add(Vector2f(1f,1f))
        uvsList.add(Vector2f(1f,0f))
    }

    private fun addBackFace(x: Int, y: Int, z: Int){
        val currentVerticesListSize = verticesList.size
        //Vertices
        verticesList.add(Vector3f(0.5f+x,0.5f+y,0.5f+z))
        verticesList.add(Vector3f(0.5f+x,-0.5f+y,0.5f+z))
        verticesList.add(Vector3f(-0.5f+x,-0.5f+y,0.5f+z))
        verticesList.add(Vector3f(-0.5f+x,0.5f+y,0.5f+z))
        //Indices
        indicesList.add(currentVerticesListSize)
        indicesList.add(currentVerticesListSize+1)
        indicesList.add(currentVerticesListSize+3)
        indicesList.add(currentVerticesListSize+3)
        indicesList.add(currentVerticesListSize+1)
        indicesList.add(currentVerticesListSize+2)
        //Normals
        normalsList.add(Vector3f(0f,0f,1f))
        normalsList.add(Vector3f(0f,0f,1f))
        normalsList.add(Vector3f(0f,0f,1f))
        normalsList.add(Vector3f(0f,0f,1f))
        //UVs
        uvsList.add(Vector2f(0f,0f))
        uvsList.add(Vector2f(0f,1f))
        uvsList.add(Vector2f(1f,1f))
        uvsList.add(Vector2f(1f,0f))
    }

    private fun addLeftFace(x: Int, y: Int, z: Int){
        val currentVerticesListSize = verticesList.size
        //Vertices
        verticesList.add(Vector3f(-0.5f+x,0.5f+y,0.5f+z))
        verticesList.add(Vector3f(-0.5f+x,-0.5f+y,0.5f+z))
        verticesList.add(Vector3f(-0.5f+x,-0.5f+y,-0.5f+z))
        verticesList.add(Vector3f(-0.5f+x,0.5f+y,-0.5f+z))
        //Indices
        indicesList.add(currentVerticesListSize)
        indicesList.add(currentVerticesListSize+1)
        indicesList.add(currentVerticesListSize+3)
        indicesList.add(currentVerticesListSize+3)
        indicesList.add(currentVerticesListSize+1)
        indicesList.add(currentVerticesListSize+2)
        //Normals
        normalsList.add(Vector3f(-1f,0f,0f))
        normalsList.add(Vector3f(-1f,0f,0f))
        normalsList.add(Vector3f(-1f,0f,0f))
        normalsList.add(Vector3f(-1f,0f,0f))
        //UVs
        uvsList.add(Vector2f(0f,0f))
        uvsList.add(Vector2f(0f,1f))
        uvsList.add(Vector2f(1f,1f))
        uvsList.add(Vector2f(1f,0f))
    }

    private fun addRightFace(x: Int, y: Int, z: Int){
        val currentVerticesListSize = verticesList.size
        //Vertices
        verticesList.add(Vector3f(0.5f+x,0.5f+y,-0.5f+z))
        verticesList.add(Vector3f(0.5f+x,-0.5f+y,-0.5f+z))
        verticesList.add(Vector3f(0.5f+x,-0.5f+y,0.5f+z))
        verticesList.add(Vector3f(0.5f+x,0.5f+y,0.5f+z))
        //Indices
        indicesList.add(currentVerticesListSize)
        indicesList.add(currentVerticesListSize+1)
        indicesList.add(currentVerticesListSize+3)
        indicesList.add(currentVerticesListSize+3)
        indicesList.add(currentVerticesListSize+1)
        indicesList.add(currentVerticesListSize+2)
        //Normals
        normalsList.add(Vector3f(1f,0f,0f))
        normalsList.add(Vector3f(1f,0f,0f))
        normalsList.add(Vector3f(1f,0f,0f))
        normalsList.add(Vector3f(1f,0f,0f))
        //UVs
        uvsList.add(Vector2f(0f,0f))
        uvsList.add(Vector2f(0f,1f))
        uvsList.add(Vector2f(1f,1f))
        uvsList.add(Vector2f(1f,0f))
    }
}