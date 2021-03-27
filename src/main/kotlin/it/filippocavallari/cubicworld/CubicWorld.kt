package it.filippocavallari.cubicworld

import it.filippocavallari.cubicworld.graphic.mesh.ChunkMesh
import it.filippocavallari.cubicworld.graphic.renderer.SkyBoxRenderer
import it.filippocavallari.cubicworld.graphic.renderer.WaterRenderer
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
import it.filippocavallari.cubicworld.graphic.shader.WaterShader
import it.filippocavallari.lwge.graphic.Material
import it.filippocavallari.lwge.graphic.SkyBox
import it.filippocavallari.lwge.graphic.water.WaterFrameBuffers
import it.filippocavallari.lwge.loader.Loader
import it.filippocavallari.lwge.loader.TextureLoader
import org.joml.Vector3f
import org.joml.Vector4f
import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.GL11C
import org.lwjgl.opengl.GL30C
import java.util.*
import kotlin.collections.HashMap
import kotlin.random.Random


class CubicWorld : GameLogic {

    lateinit var waterFrameBuffers: WaterFrameBuffers
    lateinit var shaderProgram: ShaderProgram
    lateinit var scene: Scene
    lateinit var worldRenderer: WorldRenderer
    lateinit var waterRenderer: WaterRenderer
    lateinit var resourceManager: ResourceManager
    lateinit var skyBoxRenderer: SkyBoxRenderer

    var interval = 0f

    override fun init() {
        shaderProgram = BasicShader()
        resourceManager = ResourceManager()
        val texture = TextureLoader.createTexture("src/main/resources/textures/blocks/atlas.png")
        val normal = TextureLoader.createTexture("src/main/resources/textures/blocks/atlas_n.png")
        val material = Material(texture,normal,null, Vector4f(1f,1f,1f,1f),reflectance = 0f)
        val map = HashMap<Mesh,List<Entity>>()
        val chunkGenerator = ChunkGenerator(Random.Default.nextInt())
        val size = 25
        val waterEntities = HashMap<Mesh,List<Entity>>()
        for(x in 0..size){
            for(z in 0..size){
                val chunk = ChunkGenerator(Random.Default.nextInt()).generateChunk(x,z)
                val chunkMesh = ChunkMesh(chunk, material, resourceManager)
                chunkMesh.buildMesh()
                val mesh = chunkMesh.chunkMesh
                Loader.loadMesh(mesh)
                val entity = Entity(mesh)
                entity.transformation.setPosition(x*16f,0f,z*16f)
                map[mesh] = listOf(entity)
                val waterMesh = chunkMesh.waterMesh
                Loader.loadMesh(waterMesh)
                val waterEntity = Entity(waterMesh)
                waterEntity.transformation.setPosition(x*16f,0f,z*16f)
                waterEntities[waterMesh] = listOf(waterEntity)
            }
        }


//        val chunk = chunkGenerator.generateChunk(0,0)
//        chunk.setBlock(0,0,0,1)
//        chunk.setBlock(1,0,0,1)
//        val chunkMesh = ChunkMesh(chunk, resourceManager)
//        val mesh = chunkMesh.buildMesh()
//        map[mesh] = listOf(Entity(mesh))

//        val texture = TextureLoader.createTexture("src/main/resources/textures/blocks/atlas.png")
//        val normal = TextureLoader.createTexture("src/main/resources/textures/blocks/dirt_n.png")
//        val material = Material(texture,null,null, Vector4f(1f,1f,1f,1f),reflectance = 0f)
//        val mesh = OBJLoader.loadMesh("src/main/resources/models/blocks/cube.obj",material)
//        Loader.loadMesh(mesh)
//        map[mesh] = listOf(Entity(mesh))
        val pointLight = PointLight(Vector3f(1f,1f,1f),Vector3f(10000f,0f,10f),1f)
        val directionalLight = DirectionalLight(Vector3f(1f,1f,1f), Vector3f(0f, 1f,0f),1.5f)
        GameEngine.eventBus.register(KeyPressedListener())
        val skyBox = createSkyBox()
        scene = Scene(map,skyBox = skyBox,ambientLight = Vector3f(0.3f, 0.3f, 0.3f), pointLight = pointLight, directionalLight = directionalLight, fog = Fog(true, Vector3f(0.5f,0.5f,0.5f),0.0006f), shaderProgram = shaderProgram)
        scene.camera.setPosition(0f,0f,0f)
        worldRenderer = WorldRenderer(scene)
        skyBoxRenderer = SkyBoxRenderer(scene)
        waterFrameBuffers = WaterFrameBuffers()
        waterRenderer = WaterRenderer(WaterShader(),waterEntities,scene.camera,waterFrameBuffers)
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

    override fun update(interval: Float) {
        this.interval = interval
        scene.camera.update()
    }

    override fun render() {
        val waterHeight = 0f
        //worldRenderer.clippingPlane = Vector4f(0f,1f,0f,-waterHeight)
        worldRenderer.clippingPlane = Vector4f(0f,0f,0f,1500000f)
        waterRenderer.interval = interval
        waterFrameBuffers.bindReflectionFrameBuffer()
        val distance = 2 * (scene.camera.position.y - waterHeight)
        scene.camera.position.y -= distance
        scene.camera.invertPitch()
        worldRenderer.render()
        skyBoxRenderer.render()
        //worldRenderer.clippingPlane = Vector4f(0f,-1f,0f,waterHeight)
        worldRenderer.clippingPlane = Vector4f(0f,0f,0f,1500000f)
        scene.camera.position.y += distance
        scene.camera.invertPitch()
        waterFrameBuffers.bindRefractionFrameBuffer()
        worldRenderer.render()
        skyBoxRenderer.render()
        waterFrameBuffers.unbindCurrentFrameBuffer()
        GL11C.glDisable(GL30C.GL_CLIP_DISTANCE0)
        worldRenderer.clippingPlane = Vector4f(0f,0f,0f,0f)
        worldRenderer.render()
        waterRenderer.render()
        skyBoxRenderer.render()
    }

    override fun cleanUp() {
    }


    private fun createSkyBox(): SkyBox{
        val texture = TextureLoader.createTexture("src/main/resources/textures/skybox/skybox.png")
        val material = Material(texture,null, null,reflectance = 0f)
        val vertices = floatArrayOf(
            //FRONT FACE
            -1f,1f,-1f, //0
            -1f,-1f,-1f, //1
            1f,-1f,-1f, //2
            1f,1f,-1f, //3
            //RIGHT FACE
            1f,1f,-1f, //4
            1f,-1f,-1f, //5
            1f,-1f,1f, //6
            1f,1f,1f, //7
            //LEFT FACE
            -1f,1f,1f, //8
            -1f,-1f,1f, //9
            -1f,-1f,-1f, //10
            -1f,1f,-1f, //11
            //TOP FACE
            -1f,1f,1f, //12
            -1f,1f,-1f, //13
            1f,1f,-1f, //14
            1f,1f,1f, //15
            //BOTTOM FACE
            -1f,-1f,-1f, //16
            -1f,-1f,1f, //17
            1f,-1f,1f, //18
            1f,-1f,-1f, //19
            //BACK FACE
            1f,1f,1f, //20
            1f,-1f,1f, //21
            -1f,-1f,1f, //22
            -1f,1f,1f //23
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
        skyBox.transformation.scale = 100f
        return skyBox
    }
}