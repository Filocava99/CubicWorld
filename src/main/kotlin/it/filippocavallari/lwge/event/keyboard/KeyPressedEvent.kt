package it.filippocavallari.lwge.event.keyboard

import org.lwjgl.glfw.GLFW

class KeyPressedEvent(override val key: Int) : KeyEvent(key, GLFW.GLFW_PRESS)