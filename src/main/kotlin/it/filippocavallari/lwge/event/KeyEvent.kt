package it.filippocavallari.lwge.event

import org.lwjgl.glfw.GLFW

open class KeyEvent(open val key: Int, val action: Int) : Event()

class KeyPressedEvent(override val key: Int) : KeyEvent(key,GLFW.GLFW_PRESS)

class KeyReleasedEvent(override val key: Int) : KeyEvent(key,GLFW.GLFW_RELEASE)