package it.filippocavallari.lwge

import it.filippocavallari.lwge.event.keyboard.KeyEvent
import it.filippocavallari.lwge.event.keyboard.KeyHoldEvent
import it.filippocavallari.lwge.event.keyboard.KeyPressedEvent
import it.filippocavallari.lwge.event.keyboard.KeyReleasedEvent
import it.filippocavallari.lwge.event.window.WindowResizedEvent
import org.joml.Matrix4f
import org.joml.Vector4f
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWErrorCallback.createPrint
import org.lwjgl.glfw.GLFWFramebufferSizeCallback
import org.lwjgl.glfw.GLFWKeyCallback
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11.*
import java.util.*


class Window(
    val title: String,
    var width: Int,
    var height: Int
) {

    private val FOV = Math.toRadians(60.0).toFloat()
    private val Z_NEAR = 0.01f
    private val Z_FAR = 1000f

    var vSync: Boolean = false
    var fullscreen: Boolean = false
    var resized = false
    var debug: Boolean = false
    var clearColor: Vector4f = Vector4f(0f, 0f, 0f, 0f)
    var projectionMatrix: Matrix4f = Matrix4f()
    get() {
        //TODO Ha senso aggiornare ad ogni chiamata? Quanto mi costa?
        val aspectRatio = width.toFloat() / height.toFloat()
        field = field.setPerspective(FOV, aspectRatio, Z_NEAR, Z_FAR)
        return field
    }

    var windowId: Long = -1
        private set

    init {
        createPrint(System.err).set();
        if (!glfwInit()) {
            throw IllegalStateException("Unable to initialize GLFW")
        }
        setupWindowHint()
        windowId = glfwCreateWindow(width, height, title, 0, 0)

        glfwSetFramebufferSizeCallback(windowId, object : GLFWFramebufferSizeCallback() {
            override fun invoke(window: Long, width: Int, height: Int) {
                val windowResizedEvent = WindowResizedEvent(width,height)
                GameEngine.eventBus.dispatchEvent(windowResizedEvent)
                resized = true
            }
        })
        glfwSetKeyCallback(windowId,object : GLFWKeyCallback(){
            override fun invoke(window: Long, key: Int, scancode: Int, action: Int, mods: Int) {
                when (action) {
                    GLFW_PRESS -> GameEngine.eventBus.dispatchEvent(KeyPressedEvent(key))
                    GLFW_RELEASE -> GameEngine.eventBus.dispatchEvent(KeyReleasedEvent(key))
                    GLFW_REPEAT -> GameEngine.eventBus.dispatchEvent(KeyHoldEvent(key))
                    else -> GameEngine.eventBus.dispatchEvent(KeyEvent(key, action))
                }
            }
        })
        glfwMakeContextCurrent(windowId)
        GL.createCapabilities()
        updateClearColor()
    }

    fun enableFullScreen(flag: Boolean) {
        fullscreen = flag
        if (fullscreen) glfwMaximizeWindow(windowId) else {
            val monitor = glfwGetVideoMode(glfwGetPrimaryMonitor())
            Objects.requireNonNull(monitor)
            if (monitor != null) {
                glfwSetWindowPos(
                    windowId,
                    (monitor.width() - width) / 2,
                    (monitor.height() - height) / 2
                )
            }
        }
    }

    fun updateClearColor() {
        glClearColor(clearColor.x, clearColor.y, clearColor.z, clearColor.w)
    }

    fun enableVsync(flag: Boolean) {
        if (flag) glfwSwapInterval(1) else glfwSwapInterval(0)
    }

    fun enableDepthTest(flag: Boolean) {
        if (flag) glEnable(GL_DEPTH_TEST) else glDisable(GL_DEPTH_TEST)
    }

    fun enableBlending(flag: Boolean) {
        if (flag) glEnable(GL_BLEND) else glDisable(GL_BLEND)
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
    }

    fun enableCullFace(flag: Boolean) {
        if (flag) glEnable(GL_CULL_FACE) else glDisable(GL_CULL_FACE)
    }

    fun setCullFace(face: Int) {
        glCullFace(face)
    }

    fun showWindow(flag: Boolean) {
        if (flag) glfwShowWindow(windowId) else glfwHideWindow(windowId)
    }

    fun enableDebugMode(flag: Boolean) {
        debug = flag
        if (debug) glPolygonMode(GL_FRONT_AND_BACK, GL_LINE) else glPolygonMode(GL_FRONT_AND_BACK, GL_FILL)
    }

    fun restoreState() {
        enableDepthTest(true)
        enableBlending(true)
        enableCullFace(true)
        setCullFace(GL_BACK)
    }

    private fun setupWindowHint() {
        glfwDefaultWindowHints()
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE)
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE)
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3)
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2)
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE)
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE)
    }

    fun update() {
        if (resized) {
            glViewport(0, 0, width, height)
            resized = false
        }
        glfwSwapBuffers(windowId)
        glfwPollEvents()
    }

}