package it.filippocavallari.cubicworld

import it.filippocavallari.cubicworld.graphic.mesh.BlockMaterial
import it.filippocavallari.cubicworld.graphic.mesh.ChunkMesh
import it.filippocavallari.cubicworld.graphic.renderer.SkyBoxRenderer
import it.filippocavallari.cubicworld.graphic.renderer.WaterRenderer
import it.filippocavallari.cubicworld.graphic.renderer.WorldRenderer
import it.filippocavallari.cubicworld.graphic.shader.BasicShader
import it.filippocavallari.cubicworld.graphic.shader.SkyBoxShader
import it.filippocavallari.cubicworld.graphic.shader.WaterShader
import it.filippocavallari.cubicworld.listener.keyboard.KeyPressedListener
import it.filippocavallari.cubicworld.manager.ResourceManager
import it.filippocavallari.cubicworld.world.chunk.Chunk
import it.filippocavallari.cubicworld.world.chunk.ChunkGenerator
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
import org.joml.*
import org.joml.primitives.Intersectionf
import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.GL11C
import org.lwjgl.opengl.GL30C
import java.lang.Integer.min
import java.lang.Math.max
import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.HashSet
import kotlin.random.Random


class CubicWorld : GameLogic {

    lateinit var waterFrameBuffers: WaterFrameBuffers
    lateinit var shaderProgram: ShaderProgram
    lateinit var scene: Scene
    lateinit var worldRenderer: WorldRenderer
    lateinit var waterRenderer: WaterRenderer
    lateinit var resourceManager: ResourceManager
    lateinit var skyBoxRenderer: SkyBoxRenderer

    var chunks = HashMap<Vector2i, Chunk>()
    var chunksMeshes = HashMap<Vector2i, ChunkMesh>()
    var interval = 0f
    val entitiesInsideFrustum = HashSet<Entity>()

    override fun init() {
        shaderProgram = BasicShader()
        resourceManager = ResourceManager()
        val texture = TextureLoader.createTexture("src/main/resources/textures/blocks/atlas.png")
        val normal = TextureLoader.createTexture("src/main/resources/textures/blocks/atlas_n.png")
        val material = Material(texture, normal, null, Vector4f(1f, 1f, 1f, 1f), reflectance = 0f)
        val map = HashMap<Mesh, List<Entity>>()
        val chunkGenerator = ChunkGenerator(Random.Default.nextInt())
        val size = 1
        val waterEntities = HashMap<Mesh, List<Entity>>()
        for (x in 0..size) {
            for (z in 0..size) {
                val chunk = chunkGenerator.generateChunk(x, z)
                chunks[Vector2i(x, z)] = chunk
                val chunkMesh = ChunkMesh(chunk, material, resourceManager)
                chunksMeshes[Vector2i(x, z)] = chunkMesh
                chunkMesh.buildMesh()
                val mesh = chunkMesh.chunkMesh
                Loader.loadMesh(mesh)
                val anchors = LinkedList<Vector3f>()
                for (i in 0..30) {
                    anchors.add(Vector3f(x * 16f + 8f, 8f * i + 8f, z * 16 + 8f))
                }
                val frustumFilter = FrustumFilter(anchors, 10f)
                val entity = Entity(mesh, frustumFilter)
                entity.transformation.setPosition(x * 16f, 0f, z * 16f)
                map[mesh] = listOf(entity)
                val waterMesh = chunkMesh.waterMesh
                Loader.loadMesh(waterMesh)
                val waterEntity = Entity(waterMesh, frustumFilter)
                waterEntity.transformation.setPosition(x * 16f, 0f, z * 16f)
                waterEntities[waterMesh] = listOf(waterEntity)
//                mesh.clearArrays()
//                waterMesh.clearArrays()
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
        val pointLight = PointLight(Vector3f(1f, 1f, 1f), Vector3f(10000f, 0f, 10f), 1f)
        val directionalLight = DirectionalLight(Vector3f(1f, 1f, 1f), Vector3f(0f, 1f, 0f), 1.5f)
        GameEngine.eventBus.register(KeyPressedListener())
        val skyBox = createSkyBox()
        scene = Scene(
            map,
            skyBox = skyBox,
            ambientLight = Vector3f(0.3f, 0.3f, 0.3f),
            pointLight = pointLight,
            directionalLight = directionalLight,
            fog = Fog(true, Vector3f(0.5f, 0.5f, 0.5f), 0.0006f),
            shaderProgram = shaderProgram
        )
        scene.camera.setPosition(0f, 0f, 0f)
        worldRenderer = WorldRenderer(scene)
        skyBoxRenderer = SkyBoxRenderer(scene)
        waterFrameBuffers = WaterFrameBuffers()
        waterRenderer = WaterRenderer(WaterShader(), waterEntities, scene.camera, waterFrameBuffers)
    }

    override fun input() {
        val camera = scene.camera
        val speed = 5f
        if (GameEngine.keyboardManager.isKeyPressed(GLFW.GLFW_KEY_W)) {
            camera.prepareMovement(0f, 0f, -1f * speed)
        }
        if (GameEngine.keyboardManager.isKeyPressed(GLFW.GLFW_KEY_S)) {
            camera.prepareMovement(0f, 0f, 1f * speed)
        }
        if (GameEngine.keyboardManager.isKeyPressed(GLFW.GLFW_KEY_A)) {
            camera.prepareMovement(-1f * speed, 0f, 0f)
        }
        if (GameEngine.keyboardManager.isKeyPressed(GLFW.GLFW_KEY_D)) {
            camera.prepareMovement(1f * speed, 0f, 0f)
        }
        if (GameEngine.keyboardManager.isKeyPressed(GLFW.GLFW_KEY_SPACE)) {
            camera.prepareMovement(0f, 1f * speed, 0f)
        }
        if (GameEngine.keyboardManager.isKeyPressed(GLFW.GLFW_KEY_LEFT_SHIFT)) {
            camera.prepareMovement(0f, -1f * speed, 0f)
        }
    }

    override fun update(interval: Float) {
        this.interval = interval
        scene.camera.update()
        val playerPosition = scene.camera.position
        val playerChunkPosition = Vector2i(playerPosition.x.toInt() / 16, playerPosition.z.toInt() / 16);
        val currentChunk = chunks[playerChunkPosition];
        val chunks = listOf(
            currentChunk,
            chunks[Vector2i(playerChunkPosition.x + 1, playerChunkPosition.y)],
            chunks[Vector2i(playerChunkPosition.x - 1, playerChunkPosition.y)],
            chunks[Vector2i(playerChunkPosition.x, playerChunkPosition.y + 1)],
            chunks[Vector2i(playerChunkPosition.x, playerChunkPosition.y - 1)]
        )
        var dir = Vector3f()
        dir = scene.camera.viewMatrix.positiveZ(dir).negate()
        val min = Vector3f()
        val max = Vector3f()
        var nearFar = Vector2f()
        var closestDistance = Float.POSITIVE_INFINITY
        var selectedChunk: Chunk? = null
        for (chunk in chunks) {
            chunk?.let {
                min.set(Vector3f(chunk.position.x.toFloat()*16,0f,chunk.position.y.toFloat()))
                max.set(Vector3f((1f+chunk.position.x.toFloat())*16,255f,(chunk.position.y.toFloat()+1f)*16))
                if(Intersectionf.intersectRayAab(playerPosition,dir,min,max,nearFar) && nearFar.x < closestDistance){
                    closestDistance = nearFar.x
                    selectedChunk = chunk
                }
            }
        }
        selectedChunk?.let {
            var nearFar = Vector2f()
            var nearestBlock = Vector3i()
            closestDistance = Float.POSITIVE_INFINITY
            for(x in 0..15){
                for(z in 0..15){
                    for (y in max(0,playerPosition.y.toInt()  - 5) .. min(playerPosition.y.toInt() + 5,255)){
                        if(it.getBlock(x,y,z) != BlockMaterial.AIR.id){
                            min.set(Vector3f(it.position.x.toFloat()*16+x-0.5f,y.toFloat()-0.5f,it.position.y.toFloat()+z-0.5f))
                            max.set(Vector3f(it.position.x.toFloat()*16+x+0.5f,y+0.5f,it.position.y.toFloat()*16+z+0.5f))
                            if(Intersectionf.intersectRayAab(playerPosition,dir,min,max,nearFar) && nearFar.x < closestDistance){
                                closestDistance = nearFar.x
                                nearestBlock  = Vector3i(x,y,z)
                            }
                        }
                    }
                }
            }
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
        scene.gameItems.forEach { entry ->
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