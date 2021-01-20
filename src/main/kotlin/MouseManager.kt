import org.joml.Vector2f
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWCursorEnterCallback
import org.lwjgl.glfw.GLFWCursorPosCallback
import org.lwjgl.glfw.GLFWMouseButtonCallback

class MouseManager(val window: Long) {
    private val previousPosition: Vector2f = Vector2f(-1f,-1f)
    private val currentPosition: Vector2f = Vector2f(0f,0f)
    private val displacementVector: Vector2f = Vector2f()
    private val pointerInWindow = false
    private val leftButtonPressed = false
    private val rightButtonPressed = false

    private val mouseSensitivity = 0.5f

    init {
        glfwSetCursorEnterCallback(window, object : GLFWCursorEnterCallback(){
            override fun invoke(window: Long, entered: Boolean) {
                TODO("Not yet implemented")
            }
        })
        glfwSetCursorPosCallback(window, object : GLFWCursorPosCallback(){
            override fun invoke(window: Long, xpos: Double, ypos: Double) {
                TODO("Not yet implemented")
            }
        })
        glfwSetMouseButtonCallback(window, object : GLFWMouseButtonCallback(){
            override fun invoke(window: Long, button: Int, action: Int, mods: Int) {
                TODO("Not yet implemented")
            }
        })
    }

}