package it.filippocavallari.cubicworld

import it.filippocavallari.cubicworld.graphic.mesh.BlockMaterial
import it.filippocavallari.cubicworld.graphic.mesh.ChunkMesh
import it.filippocavallari.cubicworld.graphic.renderer.SkyBoxRenderer
import it.filippocavallari.cubicworld.graphic.renderer.WaterRenderer
import it.filippocavallari.cubicworld.graphic.renderer.WorldRenderer
import it.filippocavallari.cubicworld.graphic.shader.BasicShader
import it.filippocavallari.cubicworld.graphic.shader.SkyBoxShader
import it.filippocavallari.cubicworld.graphic.shader.WaterShader
import it.filippocavallari.cubicworld.listener.keyboard.DebugModeKeyPressedListener
import it.filippocavallari.cubicworld.listener.mouse.MouseClickListener
import it.filippocavallari.cubicworld.listener.player.PlayerChangedChunkListener
import it.filippocavallari.cubicworld.manager.ResourceManager
import it.filippocavallari.cubicworld.world.chunk.ChunkGenerator
import it.filippocavallari.cubicworld.world.chunk.WorldManager
import it.filippocavallari.lwge.GameEngine
import it.filippocavallari.lwge.GameLogic
import it.filippocavallari.lwge.Scene
import it.filippocavallari.lwge.graphic.Fog
import it.filippocavallari.lwge.graphic.Material
import it.filippocavallari.lwge.graphic.Mesh
import it.filippocavallari.lwge.graphic.SkyBox
import it.filippocavallari.lwge.graphic.entity.Entity
import it.filippocavallari.lwge.graphic.entity.component.FrustumFilter
import it.filippocavallari.lwge.graphic.light.DirectionalLight
import it.filippocavallari.lwge.graphic.light.PointLight
import it.filippocavallari.lwge.graphic.shader.ShaderProgram
import it.filippocavallari.lwge.graphic.water.WaterFrameBuffers
import it.filippocavallari.lwge.loader.Loader
import it.filippocavallari.lwge.loader.TextureLoader
import it.filippocavallari.lwge.math.FrustumCullingFilter
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.joml.Vector2i
import org.joml.Vector3f
import org.joml.Vector3i
import org.joml.Vector4f
import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.GL11C
import org.lwjgl.opengl.GL30C
import org.lwjgl.opengl.WGL
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.collections.HashMap
import kotlin.collections.HashSet

class CubicWorld : GameLogic {

    lateinit var waterFrameBuffers: WaterFrameBuffers
    lateinit var shaderProgram: ShaderProgram
    lateinit var scene: Scene
    lateinit var worldRenderer: WorldRenderer
    lateinit var waterRenderer: WaterRenderer
    lateinit var resourceManager: ResourceManager
    lateinit var skyBoxRenderer: SkyBoxRenderer
    lateinit var material: Material

    var chunksMeshes = HashMap<Vector2i, ChunkMesh>()
    var chunkMeshesToBeLoaded = ConcurrentLinkedQueue<ChunkMesh>()
    var interval = 0f

    private val entitiesInsideFrustum = HashSet<Entity>()
    private val worldManager = WorldManager()

    override fun init() {
        Loader.createVAOs(300)
        Loader.createVBOs(1000)
        shaderProgram = BasicShader()
        resourceManager = ResourceManager()
        val texture = TextureLoader.createTexture("src/main/resources/textures/blocks/atlas.png")
        val normal = TextureLoader.createTexture("src/main/resources/textures/blocks/atlas_n.png")
        material = Material(texture, normal, null, Vector4f(1f, 1f, 1f, 1f), reflectance = 0f)
        worldManager.updateActiveChunks(Vector2i(0,0))
        val pointLight = PointLight(Vector3f(1f, 1f, 1f), Vector3f(10000f, 0f, 10f), 1f)
        val directionalLight = DirectionalLight(Vector3f(1f, 1f, 1f), Vector3f(0f, 1f, 0f), 1.5f)
        GameEngine.eventBus.register(DebugModeKeyPressedListener())
        val skyBox = createSkyBox()
        scene = Scene(
            skyBox = skyBox,
            ambientLight = Vector3f(0.3f, 0.3f, 0.3f),
            pointLight = pointLight,
            directionalLight = directionalLight,
            fog = Fog(true, Vector3f(0.5f, 0.5f, 0.5f), 0.0006f),
            shaderProgram = shaderProgram
        )
        scene.camera.setPosition(1f, 200f, 1f)
        worldRenderer = WorldRenderer(scene)
        skyBoxRenderer = SkyBoxRenderer(scene)
        waterFrameBuffers = WaterFrameBuffers()
        waterRenderer = WaterRenderer(WaterShader(), scene, waterFrameBuffers)
        GameEngine.eventBus.register(MouseClickListener(worldManager))
        GameEngine.eventBus.register(PlayerChangedChunkListener(worldManager))
    }

    override fun input() {
        val camera = scene.camera
        val walkSpeed = 1f
        val flySpeed = 5f
        if (GameEngine.keyboardManager.isKeyPressed(GLFW.GLFW_KEY_W)) {
            camera.prepareMovement(0f, 0f, -1f * walkSpeed)
        }
        if (GameEngine.keyboardManager.isKeyPressed(GLFW.GLFW_KEY_S)) {
            camera.prepareMovement(0f, 0f, 1f * walkSpeed)
        }
        if (GameEngine.keyboardManager.isKeyPressed(GLFW.GLFW_KEY_A)) {
            camera.prepareMovement(-1f * walkSpeed, 0f, 0f)
        }
        if (GameEngine.keyboardManager.isKeyPressed(GLFW.GLFW_KEY_D)) {
            camera.prepareMovement(1f * walkSpeed, 0f, 0f)
        }
        if (GameEngine.keyboardManager.isKeyPressed(GLFW.GLFW_KEY_SPACE)) {
            camera.prepareMovement(0f, 1f * flySpeed, 0f)
        }
        if (GameEngine.keyboardManager.isKeyPressed(GLFW.GLFW_KEY_LEFT_SHIFT)) {
            camera.prepareMovement(0f, -1f * flySpeed, 0f)
        }
        val newDestination = Vector3f(scene.camera.position).add(scene.camera.preparedMovement).add(0f,-2f,0f)
        if(worldManager.getBlock(newDestination) != BlockMaterial.AIR.id){
           scene.camera.preparedMovement.zero()
        }
    }

    override fun update(interval: Float) {
        val camera = scene.camera
        this.interval = interval
        camera.update()
        worldManager.updateSelectedBlock(camera)
        while (worldManager.recentlyRemovedChunks.isNotEmpty()){
            val chunk = worldManager.recentlyRemovedChunks.poll()
            val chunkMesh = chunksMeshes.remove(chunk.position)
            scene.entities.remove(chunkMesh?.chunkMesh)
            scene.waterEntities.remove(chunkMesh?.waterMesh)
        }
        while(worldManager.recentModifiedChunks.isNotEmpty()){
            val chunk = worldManager.recentModifiedChunks.poll()
            chunk?.let {
                val chunkPosition = it.position
                var chunkMesh = chunksMeshes.remove(chunkPosition)
                scene.entities.remove(chunkMesh?.chunkMesh)
                scene.waterEntities.remove(chunkMesh?.chunkMesh)
                chunkMesh?.chunkMesh?.clear()
                chunkMesh?.waterMesh?.clear()
                chunkMesh = ChunkMesh(it,material,resourceManager)
                chunksMeshes[chunkPosition] = chunkMesh
                GlobalScope.launch {
                    chunkMesh.buildMesh()
                    chunkMeshesToBeLoaded.add(chunkMesh)
                }
            }
        }
        if(chunkMeshesToBeLoaded.isNotEmpty()){
            val chunkMesh = chunkMeshesToBeLoaded.poll()
            val chunkPosition = chunkMesh.chunk.position
            Loader.loadMesh(chunkMesh.chunkMesh)
            val anchors = LinkedList<Vector3f>()
            for (i in 0..30) {
                anchors.add(Vector3f(chunkPosition.x * 16f + 8f, 8f * i + 8f, chunkPosition.y * 16 + 8f))
            }
            val frustumFilter = FrustumFilter(anchors, 10f)
            val entity = Entity(chunkMesh.chunkMesh, frustumFilter)
            entity.transformation.setPosition(chunkPosition.x*16f,0f,chunkPosition.y*16f)
            scene.entities[chunkMesh.chunkMesh] = listOf(entity)
            val waterMesh = chunkMesh.waterMesh
            Loader.loadMesh(waterMesh)
            val waterEntity = Entity(waterMesh, frustumFilter)
            waterEntity.transformation.setPosition(chunkPosition.x * 16f, 0f, chunkPosition.y * 16f)
            scene.waterEntities[waterMesh] = listOf(waterEntity)
        }
        val blockBelowPosition = Vector3f(camera.position).add(0f,-2f,0f)
        val blockBelow = worldManager.getBlock(blockBelowPosition)
        if(blockBelow == BlockMaterial.AIR.id){
            val movement = Vector3f(0f,-2.5f,0f)
            val predictedY = camera.position.y-movement.y
            if(predictedY < blockBelowPosition.y+2){
                movement.y = -(camera.position.y-blockBelowPosition.y+2)
            }
            camera.prepareMovement(movement.x,movement.y,movement.z)
        }
    }

    override fun render() {
        updateFrustum()
        val waterHeight = 0f
        //worldRenderer.clippingPlane = Vector4f(0f,1f,0f,-waterHeight)
        worldRenderer.clippingPlane = Vector4f(0f, 0f, 0f, 1500000f)
        waterRenderer.interval = interval
        waterFrameBuffers.bindReflectionFrameBuffer()
        val distance = 2 * (scene.camera.position.y - waterHeight)
        scene.camera.position.y -= distance
        scene.camera.invertPitch()
        worldRenderer.render()
        skyBoxRenderer.render()
        //worldRenderer.clippingPlane = Vector4f(0f,-1f,0f,waterHeight)
        worldRenderer.clippingPlane = Vector4f(0f, 0f, 0f, 1500000f)
        scene.camera.position.y += distance
        scene.camera.invertPitch()
        waterFrameBuffers.bindRefractionFrameBuffer()
        worldRenderer.render()
        skyBoxRenderer.render()
        waterFrameBuffers.unbindCurrentFrameBuffer()
        GL11C.glDisable(GL30C.GL_CLIP_DISTANCE0)
        worldRenderer.clippingPlane = Vector4f(0f, 0f, 0f, 0f)
        worldRenderer.render()
        waterRenderer.render()
        skyBoxRenderer.render()
    }

    private fun updateFrustum() {
        FrustumCullingFilter.updateFrustum(GameEngine.projectionMatrix, scene.camera.viewMatrix)
        scene.entities.forEach { entry ->
            entry.value.forEach { entity ->
                val frustumFilter = entity.frustumFilter
                if (!frustumFilter.ignoreFrustum) {
                    for (position in frustumFilter.anchors) {
                        frustumFilter.insideFrustum =
                            FrustumCullingFilter.insideFrustum(position.x, position.y, position.z, frustumFilter.radius)
                        if (frustumFilter.insideFrustum) {
                            entitiesInsideFrustum.add(entity)
                            break
                        }
                    }
                    if (!frustumFilter.insideFrustum) {
                        entitiesInsideFrustum.remove(entity)
                    }
                } else {
                    entitiesInsideFrustum.add(entity)
                }
            }

        }
    }

    override fun cleanUp() {
    }

    private fun createSkyBox(): SkyBox {
        val texture = TextureLoader.createTexture("src/main/resources/textures/skybox/skybox.png")
        val material = Material(texture, null, null, reflectance = 0f)
        val vertices = floatArrayOf(
            //FRONT FACE
            -1f, 1f, -1f, //0
            -1f, -1f, -1f, //1
            1f, -1f, -1f, //2
            1f, 1f, -1f, //3
            //RIGHT FACE
            1f, 1f, -1f, //4
            1f, -1f, -1f, //5
            1f, -1f, 1f, //6
            1f, 1f, 1f, //7
            //LEFT FACE
            -1f, 1f, 1f, //8
            -1f, -1f, 1f, //9
            -1f, -1f, -1f, //10
            -1f, 1f, -1f, //11
            //TOP FACE
            -1f, 1f, 1f, //12
            -1f, 1f, -1f, //13
            1f, 1f, -1f, //14
            1f, 1f, 1f, //15
            //BOTTOM FACE
            -1f, -1f, -1f, //16
            -1f, -1f, 1f, //17
            1f, -1f, 1f, //18
            1f, -1f, -1f, //19
            //BACK FACE
            1f, 1f, 1f, //20
            1f, -1f, 1f, //21
            -1f, -1f, 1f, //22
            -1f, 1f, 1f //23
        )
        val indices = intArrayOf(
            //front
            0, 1, 3, 3, 1, 2,
            //right
            4, 5, 7, 7, 5, 6,
            //left
            8, 9, 11, 11, 9, 10,
            //top
            12, 13, 15, 15, 13, 14,
            //bottom
            16, 17, 19, 19, 17, 18,
            //back
            20, 21, 23, 23, 21, 22
        )
        val normals = floatArrayOf(
            //FRONT
            0f, 0f, -1f,
            0f, 0f, -1f,
            0f, 0f, -1f,
            0f, 0f, -1f,
            //RIGHT
            1f, 0f, 0f,
            1f, 0f, 0f,
            1f, 0f, 0f,
            1f, 0f, 0f,
            //LEFT
            -1f, 0f, 0f,
            -1f, 0f, 0f,
            -1f, 0f, 0f,
            -1f, 0f, 0f,
            //TOP
            0f, 1f, 0f,
            0f, 1f, 0f,
            0f, 1f, 0f,
            0f, 1f, 0f,
            //BOTTOM
            0f, -1f, 0f,
            0f, -1f, 0f,
            0f, -1f, 0f,
            0f, -1f, 0f,
            //BACK
            0f, 0f, 1f,
            0f, 0f, 1f,
            0f, 0f, 1f,
            0f, 0f, 1f,
        )
        val uvs = floatArrayOf(
            //FRONT
            0f, 0f, 0f, 0.33f, 0.33f, 0.33f, 0.33f, 0f,
            //RIGHT
            0f, 0.33f, 0f, 0.66f, 0.33f, 0.66f, 0.33f, 0.33f,
            //LEFT
            0.66f, 0f, 0.66f, 0.33f, 1f, 0.33f, 1f, 0f,
            //TOP
            0.33f, 0.33f, 0.33f, 0.66f, 0.66f, 0.66f, 0.66f, 0.33f,
            //BOTTOM
            0.66f, 0.33f, 0.66f, 0.66f, 1f, 0.66f, 1f, 0.33f,
            //BACK
            0.33f, 0f, 0.33f, 0.33f, 0.66f, 0.33f, 0.66f, 0f,
        )
        val mesh = Mesh(vertices, indices, normals, uvs, null, material)
        Loader.loadMesh(mesh)
        val skyBox = SkyBox(mesh, SkyBoxShader())
        skyBox.transformation.scale = 100f
        return skyBox
    }

}