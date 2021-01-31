package it.filippocavallari.lwge

import it.filippocavallari.lwge.event.cursor.CursorEnteredWindowEvent
import it.filippocavallari.lwge.event.cursor.CursorLeftWindowEvent
import it.filippocavallari.lwge.event.cursor.CursorMovedEvent
import it.filippocavallari.lwge.event.mouse.MouseButtonClickedEvent
import it.filippocavallari.lwge.event.mouse.MouseButtonEvent
import it.filippocavallari.lwge.event.mouse.MouseButtonReleasedEventEvent
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
                val event = if(entered) CursorEnteredWindowEvent() else CursorLeftWindowEvent()
                GameEngine.eventBus.dispatchEvent(event)
            }
        })
        glfwSetCursorPosCallback(window, object : GLFWCursorPosCallback(){
            override fun invoke(window: Long, xpos: Double, ypos: Double) {
                val event = CursorMovedEvent(xpos, ypos)
                GameEngine.eventBus.dispatchEvent(event)
            }
        })
        glfwSetMouseButtonCallback(window, object : GLFWMouseButtonCallback(){
            override fun invoke(window: Long, button: Int, action: Int, mods: Int) {
                val event = when(action) {
                    GLFW_PRESS -> MouseButtonClickedEvent(button)
                    GLFW_RELEASE -> MouseButtonReleasedEventEvent(button)
                    else -> MouseButtonEvent(button, action)
                }
                GameEngine.eventBus.dispatchEvent(event)
            }
        })
    }

}