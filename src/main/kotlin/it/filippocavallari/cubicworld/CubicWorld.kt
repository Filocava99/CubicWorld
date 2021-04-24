package it.filippocavallari.cubicworld

import it.filippocavallari.cubicworld.graphic.mesh.BlockMaterial
import it.filippocavallari.cubicworld.graphic.mesh.ChunkMesh
import it.filippocavallari.cubicworld.graphic.renderer.GuiRenderer
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
import it.filippocavallari.cubicworld.world.chunk.WorldManager
import it.filippocavallari.lwge.GameEngine
import it.filippocavallari.lwge.GameLogic
import it.filippocavallari.lwge.Scene
import it.filippocavallari.lwge.graphic.Fog
import it.filippocavallari.lwge.graphic.Material
import it.filippocavallari.lwge.graphic.SkyBox
import it.filippocavallari.lwge.graphic.entity.Entity
import it.filippocavallari.lwge.graphic.entity.component.FrustumFilter
import it.filippocavallari.lwge.graphic.gui.GuiEntity
import it.filippocavallari.lwge.graphic.gui.GuiScene
import it.filippocavallari.lwge.graphic.light.DirectionalLight
import it.filippocavallari.lwge.graphic.light.PointLight
import it.filippocavallari.lwge.graphic.shader.ShaderProgram
import it.filippocavallari.lwge.graphic.water.WaterFrameBuffers
import it.filippocavallari.lwge.loader.Loader
import it.filippocavallari.lwge.loader.OBJLoader
import it.filippocavallari.lwge.loader.TextureLoader
import it.filippocavallari.lwge.math.FrustumCullingFilter
import org.joml.Vector2i
import org.joml.Vector3f
import org.joml.Vector4f
import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.GL11C
import org.lwjgl.opengl.GL11C.*
import org.lwjgl.opengl.GL30C
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue

class CubicWorld : GameLogic {

    lateinit var waterFrameBuffers: WaterFrameBuffers
    lateinit var shaderProgram: ShaderProgram
    lateinit var scene: Scene
    lateinit var guiScene: GuiScene
    lateinit var worldRenderer: WorldRenderer
    lateinit var waterRenderer: WaterRenderer
    lateinit var guiRenderer: GuiRenderer
    lateinit var resourceManager: ResourceManager
    lateinit var skyBoxRenderer: SkyBoxRenderer
    lateinit var material: Material

    var chunksMeshes = HashMap<Vector2i, ChunkMesh>()
    var chunkMeshesToBeLoaded = ConcurrentLinkedQueue<ChunkMesh>()
    var chunkMeshesToBeRendered = ConcurrentLinkedQueue<ChunkMesh>()
    var chunkMeshesToBeUnloaded = ConcurrentLinkedQueue<ChunkMesh>()
    var interval = 0f

    private val entitiesInsideFrustum = HashSet<Entity>()
    private val worldManager = WorldManager()

    override fun init() {
        println(glGetString(GL_VENDOR))
        println(glGetString(GL_RENDERER))
        runThread()
        Loader.createVAOs(2000)
        Loader.createVBOs(2000)
        shaderProgram = BasicShader()
        resourceManager = ResourceManager()
        val texture = TextureLoader.createTexture("src/main/resources/textures/blocks/atlas.png")
        val normal = TextureLoader.createTexture("src/main/resources/textures/blocks/atlas_n.png")
        material = Material(texture, normal, null, Vector4f(1f, 1f, 1f, 1f), reflectance = 0f)
        worldManager.updateActiveChunks(Vector2i(0, 0))
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
        guiScene = createGuiScene()
        worldRenderer = WorldRenderer(scene)
        skyBoxRenderer = SkyBoxRenderer(scene)
        waterFrameBuffers = WaterFrameBuffers()
        waterRenderer = WaterRenderer(WaterShader(), scene, waterFrameBuffers)
        guiRenderer = GuiRenderer(guiScene)
        GameEngine.eventBus.register(MouseClickListener(scene.camera, worldManager))
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
//        val newDestination = Vector3f(scene.camera.position).add(scene.camera.preparedMovement).add(0f, -2f, 0f).floor()
//        if (worldManager.getBlock(newDestination) != BlockMaterial.AIR.id) {
//            scene.camera.preparedMovement.zero()
//        }
    }

    override fun update(interval: Float) {
        val camera = scene.camera
        this.interval = interval
        camera.update()
        worldManager.updateSelectedBlock(camera)
        updateChunkMeshes()
        val blockBelowPosition = Vector3f(camera.position).floor().add(0f, -2f, 0f)
        val blockBelow = worldManager.getBlock(blockBelowPosition)
        if (blockBelow == BlockMaterial.AIR.id) {
            val movement = Vector3f(0f, -2.5f, 0f)
            val predictedY = camera.position.y - movement.y
            if (predictedY < blockBelowPosition.y + 2) {
                movement.y = -(camera.position.y - blockBelowPosition.y + 2)
            }
            camera.prepareMovement(movement.x, movement.y, movement.z)
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
        guiRenderer.render()
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
        val mesh = OBJLoader.loadMesh("src/main/resources/models/skybox/skybox.obj",material)
        Loader.loadMesh(mesh)
        val skyBox = SkyBox(mesh, SkyBoxShader())
        skyBox.transformation.scale = 100f
        return skyBox
    }

    private fun updateChunkMeshes(){
        while (worldManager.recentlyRemovedChunks.isNotEmpty()) {
            val chunk = worldManager.recentlyRemovedChunks.poll()
            val chunkMesh = chunksMeshes.remove(chunk.position)
            chunkMesh?.chunkMesh?.vao?.let {
                scene.entities.remove(chunkMesh.chunkMesh)
                scene.waterEntities.remove(chunkMesh.waterMesh)
                chunkMeshesToBeUnloaded.add(chunkMesh)
            }
        }
        while (worldManager.recentlyModifiedChunks.isNotEmpty()) {
            val chunk = worldManager.recentlyModifiedChunks.poll()
            chunk?.let {
                val chunkPosition = it.position
                if (chunksMeshes.contains(chunkPosition)) {
                    val chunkMesh = chunksMeshes.remove(chunkPosition)
                    chunkMeshesToBeUnloaded.add(chunkMesh)
                    scene.entities.remove(chunkMesh?.chunkMesh)
                    scene.waterEntities.remove(chunkMesh?.chunkMesh)
                }
                val chunkMesh = ChunkMesh(it, material, resourceManager)
                chunkMesh.buildMesh()
                Loader.loadMesh(chunkMesh.chunkMesh!!)
                Loader.loadMesh(chunkMesh.waterMesh!!)
                val anchors = LinkedList<Vector3f>()
                for (i in 0..30) {
                    anchors.add(Vector3f(chunkPosition.x * 16f + 8f, 8f * i + 8f, chunkPosition.y * 16 + 8f))
                }
                val frustumFilter = FrustumFilter(anchors, 10f)
                var entity = Entity(chunkMesh.chunkMesh!!, frustumFilter)
                entity.transformation.setPosition(chunkPosition.x * 16f, 0f, chunkPosition.y * 16f)
                scene.entities[chunkMesh.chunkMesh!!] = listOf(entity)
                entity = Entity(chunkMesh.waterMesh!!, frustumFilter)
                entity.transformation.setPosition(chunkPosition.x * 16f, 0f, chunkPosition.y * 16f)
                scene.waterEntities[chunkMesh.chunkMesh!!] = listOf(entity)
                chunksMeshes[chunkPosition] = chunkMesh
            }
        }
        if(worldManager.recentlyLoadedChunks.isNotEmpty()){
            val chunk = worldManager.recentlyLoadedChunks.poll()
            chunk?.let {
                val chunkPosition = it.position
                if (chunksMeshes.contains(chunkPosition)) {
                    val chunkMesh = chunksMeshes.remove(chunkPosition)
                    chunkMeshesToBeUnloaded.add(chunkMesh)
                    scene.entities.remove(chunkMesh?.chunkMesh)
                    scene.waterEntities.remove(chunkMesh?.chunkMesh)
                }
                val chunkMesh = ChunkMesh(it, material, resourceManager)
                chunkMeshesToBeLoaded.add(chunkMesh)
            }
        }
        if (chunkMeshesToBeRendered.isNotEmpty()) {
            val chunkMesh = chunkMeshesToBeRendered.poll()
            val chunkPosition = chunkMesh.chunk.position
            var mesh = chunkMesh.chunkMesh!!
            mesh.vao = Loader.getVAO()
            Loader.bindVAO(mesh.vao)
            mesh.indicesVbo = Loader.getVBO()
            Loader.loadIndicesInVbo(mesh.indicesVbo, mesh.indices)
            Loader.loadVBOinVAO(mesh.vao,mesh.verticesVbo,0, 3)
            Loader.loadVBOinVAO(mesh.vao,mesh.normalsVbo,2, 3)
            mesh.uvsVbo?.let {
                Loader.loadVBOinVAO(mesh.vao, it,1, 2)
            }
            mesh.tangentsVbo?.let {
                Loader.loadVBOinVAO(mesh.vao,it,3, 3)
            }
            val anchors = LinkedList<Vector3f>()
            for (i in 0..30) {
                anchors.add(Vector3f(chunkPosition.x * 16f + 8f, 8f * i + 8f, chunkPosition.y * 16 + 8f))
            }
            val frustumFilter = FrustumFilter(anchors, 10f)
            val entity = Entity(chunkMesh.chunkMesh!!, frustumFilter)
            entity.transformation.setPosition(chunkPosition.x * 16f, 0f, chunkPosition.y * 16f)
            scene.entities[chunkMesh.chunkMesh!!] = listOf(entity)
            mesh = chunkMesh.waterMesh!!
            mesh.vao = Loader.getVAO()
            Loader.bindVAO(mesh.vao)
            mesh.indicesVbo = Loader.getVBO()
            Loader.loadIndicesInVbo(mesh.indicesVbo, mesh.indices)
            Loader.loadVBOinVAO(mesh.vao,mesh.verticesVbo,0, 3)
            Loader.loadVBOinVAO(mesh.vao,mesh.normalsVbo,2, 3)
            mesh.uvsVbo?.let {
                Loader.loadVBOinVAO(mesh.vao, it,1, 2)
            }
            mesh.tangentsVbo?.let {
                Loader.loadVBOinVAO(mesh.vao,it,3, 3)
            }
            val waterEntity = Entity(mesh, frustumFilter)
            waterEntity.transformation.setPosition(chunkPosition.x * 16f, 0f, chunkPosition.y * 16f)
            scene.waterEntities[mesh] = listOf(waterEntity)
            chunksMeshes[chunkPosition] = chunkMesh
        }
    }

    private fun runThread() {
        Thread() {
            val window = GameEngine.getSecondContext()
            window.makeContextCurrent()
            while (true) {
                if (chunkMeshesToBeLoaded.isNotEmpty()) {
                    val chunkMesh = chunkMeshesToBeLoaded.poll()
                    chunkMesh.buildMesh()
                    chunkMesh.chunkMesh?.let { Loader.loadMeshInVBOs(it) }
                    chunkMesh.waterMesh?.let { Loader.loadMeshInVBOs(it) }
                    glFinish()
                    chunkMeshesToBeRendered.add(chunkMesh)
                }
                if (chunkMeshesToBeUnloaded.isNotEmpty()) {
                    val chunkMesh = chunkMeshesToBeUnloaded.poll()
                    chunkMesh.chunkMesh?.clear()
                    chunkMesh.waterMesh?.clear()
                }
            }
        }.start()
    }

    private fun createGuiScene(): GuiScene {
        val crossair = createCrossair()
        crossair.transformation.scale = 20f
        return GuiScene(listOf(crossair))
    }

    private fun createCrossair(): GuiEntity {
        val verticesArray = floatArrayOf(
            -0.5f,0.5f,0f,
            0.5f,0.5f,0f,
            0.5f,-0.5f,0f
            -0.5f,-0.5f,0f
        )
        val indicesArray = intArrayOf(
            0,3,1,
            1,3,2
        )
        val uvsArray = floatArrayOf(
            0f,1f,
            1f,1f,
            1f,0f,
            0f,0f
        )
        val texture = TextureLoader.createTexture("src/main/resources/textures/gui/crossair.png")
        val crossair = GuiEntity(verticesArray, indicesArray, uvsArray, texture!!)
        Loader.loadGuiEntity(crossair)
        return crossair
    }
}