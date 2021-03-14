package it.filippocavallari.cubicworld

import it.filippocavallari.cubicworld.graphic.mesh.ChunkMesh
import it.filippocavallari.cubicworld.graphic.renderer.SkyBoxRenderer
import it.filippocavallari.cubicworld.graphic.shader.BasicShader
import it.filippocavallari.cubicworld.listener.keyboard.KeyPressedListener
import it.filippocavallari.cubicworld.manager.ResourceManager
import it.filippocavallari.cubicworld.world.chunk.ChunkGenerator
import it.filippocavallari.lwge.GameEngine
import it.filippocavallari.lwge.GameLogic
import it.filippocavallari.lwge.Scene
import it.filippocavallari.lwge.graphic.Fog
import it.filippocavallari.lwge.graphic.Mesh
import it.filippocavallari.lwge.graphic.entity.Entity
import it.filippocavallari.lwge.graphic.light.DirectionalLight
import it.filippocavallari.lwge.graphic.light.PointLight
import it.filippocavallari.lwge.graphic.shader.ShaderProgram
import it.filippocavallari.cubicworld.graphic.renderer.WorldRenderer
import it.filippocavallari.cubicworld.graphic.shader.SkyBoxShader
import it.filippocavallari.lwge.graphic.Material
import it.filippocavallari.lwge.graphic.SkyBox
import it.filippocavallari.lwge.loader.Loader
import it.filippocavallari.lwge.loader.TextureLoader
import org.joml.Vector3f
import org.joml.Vector4f
import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.GL30C
import kotlin.random.Random


class CubicWorld : GameLogic {

    lateinit var shaderProgram: ShaderProgram
    lateinit var scene: Scene
    lateinit var worldRenderer: WorldRenderer
    lateinit var resourceManager: ResourceManager
    lateinit var skyBoxRenderer: SkyBoxRenderer

    override fun init() {
        shaderProgram = BasicShader()
        resourceManager = ResourceManager()
        val map = HashMap<Mesh,List<Entity>>()
        val chunkGenerator = ChunkGenerator(Random.Default.nextInt())
        val size = 1
        for(x in 0..size){
            for(z in 0..size){
                val chunk = ChunkGenerator(Random.Default.nextInt()).generateChunk(x,z)
                val chunkMesh = ChunkMesh(chunk, resourceManager)
                val mesh = chunkMesh.buildMesh()
                val entity = Entity(mesh)
                entity.transformation.setPosition(x*16f,0f,z*16f)
                map[mesh] = listOf(entity)
            }
        }
//        val chunk = chunkGenerator.generateChunk(0,0)
//        chunk.setBlock(0,0,0,1)
//        val chunkMesh = ChunkMesh(chunk, resourceManager)
//        val mesh = chunkMesh.buildMesh()
//        map[mesh] = listOf(Entity())
        val pointLight = PointLight(Vector3f(1f,1f,1f),Vector3f(10000f,0f,10f),1f)
        val directionalLight = DirectionalLight(Vector3f(1f,1f,1f), Vector3f(0.5f, 1f,0.3f),1.5f)
        GameEngine.eventBus.register(KeyPressedListener())
        val skyBox = createSkyBox()
        scene = Scene(map,skyBox = skyBox,ambientLight = Vector3f(0.3f, 0.3f, 0.3f), pointLight = pointLight, directionalLight = directionalLight, fog = Fog(true, Vector3f(0.5f,0.5f,0.5f),0.006f), shaderProgram = shaderProgram)
        scene.camera.setPosition(0f,0f,0f)
        worldRenderer = WorldRenderer(scene)
        skyBoxRenderer = SkyBoxRenderer(scene)
    }

    override fun input() {
        val camera =  scene.camera
        val speed = 5f
        if(GameEngine.keyboardManager.isKeyPressed(GLFW.GLFW_KEY_W)){
            camera.prepareMovement(0f,0f,-1f*speed)
        }
        if(GameEngine.keyboardManager.isKeyPressed(GLFW.GLFW_KEY_S)){
            camera.prepareMovement(0f,0f,1f*speed)
        }
        if(GameEngine.keyboardManager.isKeyPressed(GLFW.GLFW_KEY_A)){
            camera.prepareMovement(-1f*speed,0f,0f)
        }
        if(GameEngine.keyboardManager.isKeyPressed(GLFW.GLFW_KEY_D)){
            camera.prepareMovement(1f*speed,0f,0f)
        }
        if(GameEngine.keyboardManager.isKeyPressed(GLFW.GLFW_KEY_SPACE)){
            camera.prepareMovement(0f,1f*speed,0f)
        }
        if(GameEngine.keyboardManager.isKeyPressed(GLFW.GLFW_KEY_LEFT_SHIFT)){
            camera.prepareMovement(0f,-1f*speed,0f)
        }
    }

    override fun update() {
        scene.camera.update()
    }

    override fun render() {
        worldRenderer.render()
        //skyBoxRenderer.render()
    }

    override fun cleanUp() {
    }


    private fun createSkyBox(): SkyBox{
        val texture = TextureLoader.createTexture("src/main/resources/textures/skybox/skybox.png")
        val material = Material(texture,null, null,reflectance = 0f)
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
            0f,0f,-0.5f,
            0f,0f,-0.5f,
            0f,0f,-0.5f,
            0f,0f,-0.5f,
            //RIGHT
            0.5f,0f,0f,
            0.5f,0f,0f,
            0.5f,0f,0f,
            0.5f,0f,0f,
            //LEFT
            -0.5f,0f,0f,
            -0.5f,0f,0f,
            -0.5f,0f,0f,
            -0.5f,0f,0f,
            //TOP
            0f,0.5f,0f,
            0f,0.5f,0f,
            0f,0.5f,0f,
            0f,0.5f,0f,
            //BOTTOM
            0f,-0.5f,0f,
            0f,-0.5f,0f,
            0f,-0.5f,0f,
            0f,-0.5f,0f,
            //BACK
            0f,0f,0.5f,
            0f,0f,0.5f,
            0f,0f,0.5f,
            0f,0f,0.5f,
        )
        val uvs = floatArrayOf(
            //FRONT
            0f,0f,0f,0.33f,0.33f,0.33f,0.33f,0f,
            //RIGHT
            0f,0.33f,0f,0.66f,0.33f,0.66f,0.33f,0.33f,
            //LEFT
            0.66f,0f,0.66f,0.33f,1f,0.33f,1f,0f,
            //TOP
            0.33f,0.33f,0.33f,0.66f,0.66f,0.66f,0.66f,0.33f,
            //BOTTOM
            0.66f,0.33f,0.66f,0.66f,1f,0.66f,1f,0.33f,
            //BACK
            0.33f,0f,0.33f,0.33f,0.66f,0.33f,0.66f,0f,
        )
        val mesh = Mesh(vertices,indices,normals,uvs,null,material)
        Loader.loadMesh(mesh)
        val skyBox = SkyBox(mesh,SkyBoxShader())
        skyBox.transformation.scale = 30f
        skyBox.transformation.setPosition(0f,0f,0f)
        return skyBox
    }
}