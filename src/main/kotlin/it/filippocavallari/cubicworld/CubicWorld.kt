package it.filippocavallari.cubicworld

import it.filippocavallari.cubicworld.graphic.mesh.ChunkMesh
import it.filippocavallari.cubicworld.graphic.shader.BasicShader
import it.filippocavallari.cubicworld.listener.keyboard.KeyPressedListener
import it.filippocavallari.cubicworld.manager.ResourceManager
import it.filippocavallari.cubicworld.world.chunk.Chunk
import it.filippocavallari.cubicworld.world.chunk.ChunkGenerator
import it.filippocavallari.lwge.GameEngine
import it.filippocavallari.lwge.GameLogic
import it.filippocavallari.lwge.Scene
import it.filippocavallari.lwge.graphic.Mesh
import it.filippocavallari.lwge.graphic.entity.Entity
import it.filippocavallari.lwge.graphic.light.DirectionalLight
import it.filippocavallari.lwge.graphic.light.PointLight
import it.filippocavallari.lwge.graphic.shader.ShaderProgram
import it.filippocavallari.lwge.renderer.Renderer
import org.joml.Vector3f
import org.lwjgl.glfw.GLFW
import kotlin.random.Random


class CubicWorld : GameLogic {

    lateinit var shaderProgram: ShaderProgram
    lateinit var scene: Scene
    lateinit var renderer: Renderer
    lateinit var resourceManager: ResourceManager

    override fun init() {
        shaderProgram = BasicShader()
        resourceManager = ResourceManager()
        val map = HashMap<Mesh,List<Entity>>()
        val chunkGenerator = ChunkGenerator(Random.Default.nextInt())
        val size = 10
        for(x in 0..size){
            for(z in 0..size){
                val chunk = ChunkGenerator(Random.Default.nextInt()).generateChunk(x,z)
                val chunkMesh = ChunkMesh(chunk, resourceManager)
                val mesh = chunkMesh.buildMesh()
                val entity = Entity()
                entity.setPosition(x*16f,0f,z*16f)
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
        scene = Scene(map,pointLight = pointLight, directionalLight = directionalLight, shaderProgram = shaderProgram)
        scene.camera.setPosition(10f,110f,10f)
        renderer = Renderer(scene)
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
        renderer.render()
    }

    override fun cleanUp() {
    }
}