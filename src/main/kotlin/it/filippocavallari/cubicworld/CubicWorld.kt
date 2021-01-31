package it.filippocavallari.cubicworld

import it.filippocavallari.lwge.Renderer
import it.filippocavallari.lwge.*
import it.filippocavallari.lwge.graphic.Material
import it.filippocavallari.lwge.graphic.Mesh
import it.filippocavallari.lwge.loader.Loader
import it.filippocavallari.lwge.loader.TextureLoader
import org.lwjgl.opengl.GL30C.glBindVertexArray

class CubicWorld : GameLogic {

    lateinit var shaderProgram: ShaderProgram
    lateinit var scene: Scene
    lateinit var renderer: Renderer

    override fun init() {
        shaderProgram = ShaderProgram()
        shaderProgram.createVertexShader(Util.loadResource("src/main/resources/shader.vert"))
        shaderProgram.createFragmentShader(Util.loadResource("src/main/resources/shader.frag"))
        shaderProgram.link()
        shaderProgram.validateProgram()
        shaderProgram.createUniform("texture_sampler")
        shaderProgram.createUniform("modelViewMatrix")
        shaderProgram.createUniform("projectionMatrix")
        val texture = TextureLoader.createTexture("src/main/resources/dirt.png")
        val material = Material(texture, reflectance = 1f)
        val vao = Loader.getVAO()
        val vertexVbo = Loader.getVBO()
        val indicesVbo = Loader.getVBO()
        val textureVbo = Loader.getVBO()
        val normalVbo = Loader.getVBO()
//        val vertices = floatArrayOf(
//            //FRONT FACE
//            0f,1f,0f, //0
//            0f,0f,0f, //1
//            1f,0f,0f, //2
//            1f,1f,0f, //3
//            //RIGHT FACE
//            1f,1f,0f, //4
//            1f,0f,0f, //5
//            1f,0f,1f, //6
//            1f,1f,1f, //7
//            //LEFT FACE
//            0f,1f,1f, //8
//            0f,0f,1f, //9
//            0f,0f,0f, //10
//            0f,1f,0f, //11
//            //TOP FACE
//            0f,1f,1f, //12
//            0f,1f,0f, //13
//            1f,1f,0f, //14
//            1f,1f,1f, //15
//            //BOTTOM FACE
//            1f,0f,1f, //16
//            1f,0f,0f, //17
//            0f,0f,0f, //18
//            0f,0f,1f, //19
//            //BACK FACE
//            1f,1f,1f, //20
//            1f,0f,1f, //21
//            0f,0f,1f, //22
//            0f,1f,1f //23
//        )
//        val indices = intArrayOf(
//            //front
//            0,1,3,3,1,2,
//            //right
//            4,5,7,7,5,6,
//            //left
//            8,9,11,11,9,10,
//            //top
//            12,13,15,15,13,14,
//            //bottom
//            16,17,19,19,17,18,
//            //back
//            20,21,23,23,21,22
//        )
//        val uvs = floatArrayOf(
//            0f,0f,0f,1f,1f,1f,1f,0f,
//            0f,0f,0f,1f,1f,1f,1f,0f,
//            0f,0f,0f,1f,1f,1f,1f,0f,
//            0f,0f,0f,1f,1f,1f,1f,0f,
//            0f,0f,0f,1f,1f,1f,1f,0f,
//            0f,0f,0f,1f,1f,1f,1f,0f,
//        )
        val vertices = floatArrayOf(
            // V0
            -0.5f, 0.5f, 0.5f,
            // V1
            -0.5f, -0.5f, 0.5f,
            // V2
            0.5f, -0.5f, 0.5f,
            // V3
            0.5f, 0.5f, 0.5f,
            // V4
            -0.5f, 0.5f, -0.5f,
            // V5
            0.5f, 0.5f, -0.5f,
            // V6
            -0.5f, -0.5f, -0.5f,
            // V7
            0.5f, -0.5f, -0.5f,

            // For text coords in top face
            // V8: V4 repeated
            -0.5f, 0.5f, -0.5f,
            // V9: V5 repeated
            0.5f, 0.5f, -0.5f,
            // V10: V0 repeated
            -0.5f, 0.5f, 0.5f,
            // V11: V3 repeated
            0.5f, 0.5f, 0.5f,

            // For text coords in right face
            // V12: V3 repeated
            0.5f, 0.5f, 0.5f,
            // V13: V2 repeated
            0.5f, -0.5f, 0.5f,

            // For text coords in left face
            // V14: V0 repeated
            -0.5f, 0.5f, 0.5f,
            // V15: V1 repeated
            -0.5f, -0.5f, 0.5f,

            // For text coords in bottom face
            // V16: V6 repeated
            -0.5f, -0.5f, -0.5f,
            // V17: V7 repeated
            0.5f, -0.5f, -0.5f,
            // V18: V1 repeated
            -0.5f, -0.5f, 0.5f,
            // V19: V2 repeated
            0.5f, -0.5f, 0.5f
        )
        val indices = intArrayOf(
            // Front face
            0, 1, 3, 3, 1, 2,
            // Top Face
            8, 10, 11, 9, 8, 11,
            // Right face
            12, 13, 7, 5, 12, 7,
            // Left face
            14, 15, 6, 4, 14, 6,
            // Bottom face
            16, 18, 19, 17, 16, 19,
            // Back face
            4, 6, 7, 5, 4, 7,
        )
        val uvs = floatArrayOf(
            0.0f, 0.0f,
            0.0f, 0.5f,
            0.5f, 0.5f,
            0.5f, 0.0f,

            0.0f, 0.0f,
            0.5f, 0.0f,
            0.0f, 0.5f,
            0.5f, 0.5f,

            // For text coords in top face
            0.0f, 0.5f,
            0.5f, 0.5f,
            0.0f, 1.0f,
            0.5f, 1.0f,

            // For text coords in right face
            0.0f, 0.0f,
            0.0f, 0.5f,

            // For text coords in left face
            0.5f, 0.0f,
            0.5f, 0.5f,

            // For text coords in bottom face
            0.5f, 0.0f,
            1.0f, 0.0f,
            0.5f, 0.5f,
            1.0f, 0.5f,
        )
        glBindVertexArray(vao.id)
        Loader.loadVerticesInVbo(vertexVbo, vertices)
        Loader.loadIndicesInVbo(indicesVbo, indices)
        Loader.loadUVsInVbo(textureVbo, uvs)
        glBindVertexArray(0)
        var mesh = Mesh(
            material,
            vao,
            mutableSetOf(vertexVbo, indicesVbo, normalVbo),
            mutableSetOf(textureVbo),
            indices.size,
            shaderProgram
        )
        val gameItem = GameItem()
        gameItem.position.z += -4
        scene = Scene(mapOf(Pair(mesh, listOf(gameItem))))
        renderer = Renderer(scene)
    }

    override fun update() {
        scene.gameItems.values.first().first().rotation.y += 1.5f
        scene.gameItems.values.first().first().rotation.x += 1.5f
        scene.gameItems.values.first().first().rotation.z += 1.5f
    }

    override fun render() {
        renderer.render()
    }

    override fun cleanUp() {
    }
}