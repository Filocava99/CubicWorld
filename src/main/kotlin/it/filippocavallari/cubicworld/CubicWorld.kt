package it.filippocavallari.cubicworld

import it.filippocavallari.lwge.renderer.Renderer
import it.filippocavallari.lwge.*
import it.filippocavallari.lwge.graphic.entity.Entity
import it.filippocavallari.lwge.graphic.light.DirectionalLight
import it.filippocavallari.lwge.graphic.Material
import it.filippocavallari.lwge.graphic.Mesh
import it.filippocavallari.lwge.graphic.light.PointLight
import it.filippocavallari.lwge.graphic.shader.ShaderProgram
import it.filippocavallari.lwge.loader.Loader
import it.filippocavallari.lwge.loader.TextureLoader
import org.joml.Vector3f
import org.lwjgl.glfw.GLFW
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
        //shaderProgram.createUniform("cameraPos")
        shaderProgram.createUniform("textureSampler")
        shaderProgram.createUniform("normalMap")
        shaderProgram.createUniform("modelViewMatrix")
        shaderProgram.createUniform("projectionMatrix")
        shaderProgram.createMaterialUniform("material")
        shaderProgram.createPointLightUniform("pointLight")
        shaderProgram.createDirectionalLightUniform("directionalLight")
        shaderProgram.createUniform("ambientLight")
        shaderProgram.createUniform("specularPower")
        val texture = TextureLoader.createTexture("src/main/resources/bricks.png")
        val normalMap = TextureLoader.createTexture("src/main/resources/bricks_n.png")
        val material = Material(texture, normalMap, reflectance = 0f)
        val vao = Loader.getVAO()
        val vertexVbo = Loader.getVBO()
        val indicesVbo = Loader.getVBO()
        val textureVbo = Loader.getVBO()
        val normalVbo = Loader.getVBO()
        val vertices = floatArrayOf(
            //FRONT FACE
            0f,1f,0f, //0
            0f,0f,0f, //1
            1f,0f,0f, //2
            1f,1f,0f, //3
            //RIGHT FACE
            1f,1f,0f, //4
            1f,0f,0f, //5
            1f,0f,1f, //6
            1f,1f,1f, //7
            //LEFT FACE
            0f,1f,1f, //8
            0f,0f,1f, //9
            0f,0f,0f, //10
            0f,1f,0f, //11
            //TOP FACE
            0f,1f,1f, //12
            0f,1f,0f, //13
            1f,1f,0f, //14
            1f,1f,1f, //15
            //BOTTOM FACE
            1f,0f,1f, //16
            1f,0f,0f, //17
            0f,0f,0f, //18
            0f,0f,1f, //19
            //BACK FACE
            1f,1f,1f, //20
            1f,0f,1f, //21
            0f,0f,1f, //22
            0f,1f,1f //23
        )
        val indices = intArrayOf(
            //front
            0,1,3,3,1,2,
            //right
            4,5,7,7,5,6,
            //left
            8,9,11,11,9,10,
            //top
            12,13,15,15,13,14,
            //bottom
            16,17,19,19,17,18,
            //back
            20,21,23,23,21,22
        )
        val normals = floatArrayOf(
            //FRONT
            0f,0f,-1f,
            0f,0f,-1f,
            0f,0f,-1f,
            0f,0f,-1f,
            //RIGHT
            1f,0f,0f,
            1f,0f,0f,
            1f,0f,0f,
            1f,0f,0f,
            //LEFT
            -1f,0f,0f,
            -1f,0f,0f,
            -1f,0f,0f,
            -1f,0f,0f,
            //TOP
            0f,1f,0f,
            0f,1f,0f,
            0f,1f,0f,
            0f,1f,0f,
            //BOTTOM
            0f,-1f,0f,
            0f,-1f,0f,
            0f,-1f,0f,
            0f,-1f,0f,
            //BACK

            0f,0f,1f,
            0f,0f,1f,
            0f,0f,1f,
            0f,0f,1f,
        )
        val uvs = floatArrayOf(
            0f,0f,0f,1f,1f,1f,1f,0f,
            0f,0f,0f,1f,1f,1f,1f,0f,
            0f,0f,0f,1f,1f,1f,1f,0f,
            0f,0f,0f,1f,1f,1f,1f,0f,
            0f,0f,0f,1f,1f,1f,1f,0f,
            0f,0f,0f,1f,1f,1f,1f,0f,
        )
        glBindVertexArray(vao.id)
        Loader.loadVerticesInVbo(vertexVbo, vertices)
        Loader.loadIndicesInVbo(indicesVbo, indices)
        Loader.loadUVsInVbo(textureVbo, uvs)
        Loader.loadNormalsInVbo(normalVbo, normals)
        glBindVertexArray(0)
        val mesh = Mesh(
            material,
            vao,
            mutableSetOf(vertexVbo, indicesVbo, normalVbo),
            mutableSetOf(textureVbo),
            indices.size,
        )
        val gameItem = Entity()
        gameItem.position.z += -4
        val pointLight = PointLight(Vector3f(1f,1f,1f),Vector3f(10000f,0f,10f),1f)
        val directionalLight = DirectionalLight(Vector3f(1f,1f,1f), Vector3f(0.5f, -1f,0f),1f)
        scene = Scene(mapOf(Pair(mesh, listOf(gameItem))),pointLight = pointLight, directionalLight = directionalLight, shaderProgram = shaderProgram)
        renderer = Renderer(scene)
    }

    override fun input() {
        val camera =  scene.camera
        if(GameEngine.keyboardManager.isKeyPressed(GLFW.GLFW_KEY_W)){
            camera.prepareMovement(0f,0f,-1f)
        }
        if(GameEngine.keyboardManager.isKeyPressed(GLFW.GLFW_KEY_S)){
            camera.prepareMovement(0f,0f,1f)
        }
        if(GameEngine.keyboardManager.isKeyPressed(GLFW.GLFW_KEY_A)){
            camera.prepareMovement(-1f,0f,0f)
        }
        if(GameEngine.keyboardManager.isKeyPressed(GLFW.GLFW_KEY_D)){
            camera.prepareMovement(1f,0f,0f)
        }
        if(GameEngine.keyboardManager.isKeyPressed(GLFW.GLFW_KEY_SPACE)){
            camera.prepareMovement(0f,1f,0f)
        }
        if(GameEngine.keyboardManager.isKeyPressed(GLFW.GLFW_KEY_LEFT_SHIFT)){
            camera.prepareMovement(0f,-1f,0f)
        }
    }

    override fun update() {
        val camera = scene.camera
        val MOUSE_SENSITIVITY = 0.1f
        camera.movePosition()
        if (GameEngine.mouseManager.rightMouseButton) {
            val rotVec = GameEngine.mouseManager.displacementVector
            camera.rotate(rotVec.x.toFloat() * MOUSE_SENSITIVITY, rotVec.y.toFloat() * MOUSE_SENSITIVITY, 0f)
        }
//        val gameItem = scene.gameItems.values.first().first()
//        val rotation = gameItem.rotation
//        rotation.x += 1.5f
//        if(rotation.x > 360)rotation.x=0f
//        rotation.y += 1.5f
//        if(rotation.y > 360)rotation.y=0f
//        rotation.z += 1.5f
//        if(rotation.z > 360)rotation.z=0f
    }

    override fun render() {
        renderer.render()
    }

    override fun cleanUp() {
    }
}