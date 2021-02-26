package it.filippocavallari.cubicworld

import it.filippocavallari.cubicworld.graphic.shader.BasicShader
import it.filippocavallari.cubicworld.manager.ResourceManager
import it.filippocavallari.lwge.GameEngine
import it.filippocavallari.lwge.GameLogic
import it.filippocavallari.lwge.Scene
import it.filippocavallari.lwge.graphic.shader.ShaderProgram
import it.filippocavallari.lwge.renderer.Renderer
import org.lwjgl.glfw.GLFW


class CubicWorld : GameLogic {

    lateinit var shaderProgram: ShaderProgram
    lateinit var scene: Scene
    lateinit var renderer: Renderer
    lateinit var resourceManager: ResourceManager

    override fun init() {
        shaderProgram = BasicShader()
        resourceManager = ResourceManager()

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
        scene.camera.update()
    }

    override fun render() {
        renderer.render()
    }

    override fun cleanUp() {
    }
}