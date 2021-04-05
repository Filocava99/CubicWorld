package it.filippocavallari.lwge.manager

import it.filippocavallari.lwge.GameEngine
import it.filippocavallari.lwge.event.cursor.CursorEnteredWindowEvent
import it.filippocavallari.lwge.event.cursor.CursorLeftWindowEvent
import it.filippocavallari.lwge.event.cursor.CursorMovedEvent
import it.filippocavallari.lwge.event.mouse.MouseButtonClickedEvent
import it.filippocavallari.lwge.event.mouse.MouseButtonEvent
import it.filippocavallari.lwge.event.mouse.MouseButtonReleasedEventEvent
import org.joml.Vector2d
import org.joml.Vector2f
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWCursorEnterCallback
import org.lwjgl.glfw.GLFWCursorPosCallback
import org.lwjgl.glfw.GLFWMouseButtonCallback

class MouseManager(val windowId: Long) {
    var cursorInsideWindows = false
    var leftMouseButton = false
    var rightMouseButton = false
    val displacementVector = Vector2d()

    private val currentPos = Vector2d()
    private val previousPosition = Vector2d(-1.0,-1.0)

    init {
        glfwSetCursorPosCallback(windowId) { _: Long, xpos: Double, ypos: Double ->
            currentPos.x = xpos
            currentPos.y = ypos
        }
        glfwSetCursorEnterCallback(windowId) { _: Long, entered: Boolean -> cursorInsideWindows = entered }
        glfwSetMouseButtonCallback(windowId) { _: Long, button: Int, action: Int, mode: Int ->
            leftMouseButton = button == GLFW_MOUSE_BUTTON_1 && action == GLFW_PRESS
            rightMouseButton = button == GLFW_MOUSE_BUTTON_2 && action == GLFW_PRESS
            if(action == GLFW_PRESS){
                GameEngine.eventBus.dispatchEvent(MouseButtonClickedEvent(button))
            }
        }
    }

    fun input() {
        displacementVector.x = 0.0
        displacementVector.y = 0.0
        if (previousPosition.x > 0 && previousPosition.y > 0 && cursorInsideWindows) {
            val deltaX: Double = currentPos.x - previousPosition.x
            val deltaY: Double = currentPos.y - previousPosition.y
            val rotateX = deltaX != 0.0
            val rotateY = deltaY != 0.0
            if (rotateX) {
                displacementVector.y = deltaX
            }
            if (rotateY) {
                displacementVector.x = deltaY
            }
        }
        previousPosition.x = currentPos.x
        previousPosition.y = currentPos.y
    }

    fun resetDisplacementVector() {
        displacementVector.x = 0.0
        displacementVector.y = 0.0
    }

    fun lockAndHideCursor(){
        glfwSetCursorPos(windowId, 1920.0, 1080.0)
        glfwSetInputMode(windowId, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
    }

}