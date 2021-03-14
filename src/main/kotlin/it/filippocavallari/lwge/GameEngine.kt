package it.filippocavallari.lwge

import it.filippocavallari.lwge.event.EventBus
import it.filippocavallari.lwge.manager.KeyboardManager
import it.filippocavallari.lwge.manager.MouseManager
import org.joml.Matrix4f
import org.joml.Vector4f
import org.lwjgl.glfw.GLFW.glfwHideWindow
import org.lwjgl.glfw.GLFW.glfwWindowShouldClose
import org.lwjgl.opengl.GL11C.GL_BACK
import kotlin.system.exitProcess


class GameEngine(val gameLogic: GameLogic) {
    private val TARGET_FPS = 75 //frames per second
    private val TARGET_UPS = 30 //updates per second


    private val timer: Timer = Timer()
    private var shouldClose = false

    init{
        window = Window("CubicWorld",3840,2160)
//        window.setCullFace(GL_BACK)
//        window.enableCullFace(true)
        window.run {
            clearColor = Vector4f(255f,0f,0f,0f)
            enableDepthTest(true)
            showWindow(true)
//            enableDebugMode(true)
        }
        mouseManager = MouseManager(window.windowId)
        keyboardManager = KeyboardManager(window.windowId)
        projectionMatrix = window.projectionMatrix
        gameLogic.init()
    }

    fun run() {
        try {
            gameLoop()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            cleanUp()
        }
    }

    fun stop(){
        shouldClose = true
    }

    private fun gameLoop() {
        var rendering = 0
        var elapsedTime: Float
        var accumulator = 0f
        val interval = 1f / TARGET_UPS
        val running = true
        var fpsTime: Float = 0f
        while (running && !glfwWindowShouldClose(window.windowId)) {//&& !window.windowShouldClose()) {
            if((System.currentTimeMillis()/1000-fpsTime)>=1){
                fpsTime = (System.currentTimeMillis()/1000).toFloat()
                println(rendering)
                rendering = 0
            }
            elapsedTime = timer.elapsedTime
            accumulator += elapsedTime
            input()
            while (accumulator >= interval) {
                update(interval)
                accumulator -= interval
            }
            render()
            rendering++
            if (!window.vSync) {
                sync()
            }
        }
    }

    private fun sync() {
        val loopSlot = 1f / TARGET_FPS
        val endTime = timer.lastLoopTime + loopSlot
        while (timer.time < endTime) {
            try {
                Thread.sleep(1)
            } catch (ignored: InterruptedException) {
            } finally {
                //cleanUp()
            }
        }
    }

    private fun input() {
        gameLogic.input()
        mouseManager.input()
    }

    private fun update(interval: Float) {
        gameLogic.update()
    }

    private fun render() {
        gameLogic.render()
        window.update()
    }

    private fun cleanUp() {
        gameLogic.cleanUp()
        glfwHideWindow(window.windowId)
        //exitProcess(0)
    }

    companion object{
        private lateinit var window: Window
        lateinit var projectionMatrix: Matrix4f
        val eventBus = EventBus()
        lateinit var mouseManager: MouseManager
        lateinit var keyboardManager: KeyboardManager

        fun enableDebugMode(flag: Boolean){
            window.enableDebugMode(flag)
        }

        fun toggleDebugMode(){
            window.enableDebugMode(!window.debug)
        }
    }
}