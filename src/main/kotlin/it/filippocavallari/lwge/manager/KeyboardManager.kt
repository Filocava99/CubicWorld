package it.filippocavallari.lwge.manager

import org.lwjgl.glfw.GLFW

class KeyboardManager(private val windowId: Long) {

    fun isKeyPressed(keycode: Int): Boolean{
        return GLFW.glfwGetKey(windowId, keycode) == GLFW.GLFW_PRESS
    }

}