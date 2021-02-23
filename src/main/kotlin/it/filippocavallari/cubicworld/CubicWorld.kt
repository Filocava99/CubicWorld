package it.filippocavallari.cubicworld

import it.filippocavallari.lwge.GameEngine
import it.filippocavallari.lwge.GameLogic
import it.filippocavallari.lwge.Scene
import it.filippocavallari.lwge.Util
import it.filippocavallari.lwge.graphic.shader.ShaderProgram
import it.filippocavallari.lwge.renderer.Renderer
import org.lwjgl.glfw.GLFW


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
        shaderProgram.createUniform("textureSampler")
        shaderProgram.createUniform("normalMap")
        shaderProgram.createUniform("modelViewMatrix")
        shaderProgram.createUniform("projectionMatrix")
        shaderProgram.createMaterialUniform("material")
        shaderProgram.createPointLightUniform("pointLight")
        shaderProgram.createDirectionalLightUniform("directionalLight")
        shaderProgram.createUniform("ambientLight")
        shaderProgram.createUniform("specularPower")

//        val pointLight = PointLight(Vector3f(1f,1f,1f),Vector3f(10000f,0f,10f),1f)
//        val directionalLight = DirectionalLight(Vector3f(1f,1f,1f), Vector3f(0.5f, -1f,0f),1f)
//        scene = Scene(mapOf(Pair(mesh, listOf(gameItem))),pointLight = pointLight, directionalLight = directionalLight, shaderProgram = shaderProgram)
//        renderer = Renderer(scene)
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
        val gameItem = scene.gameItems.values.first().first()
        val rotation = gameItem.rotation
        rotation.x += 1.5f
        if(rotation.x > 360)rotation.x=0f
        rotation.y += 1.5f
        if(rotation.y > 360)rotation.y=0f
        rotation.z += 1.5f
        if(rotation.z > 360)rotation.z=0f
    }

    override fun render() {
        renderer.render()
    }

    override fun cleanUp() {
    }
}