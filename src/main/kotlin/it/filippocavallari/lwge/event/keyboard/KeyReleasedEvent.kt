package it.filippocavallari.lwge.event.keyboard

import org.lwjgl.glfw.GLFW

class KeyReleasedEvent(override val key: Int) : KeyEvent(key, GLFW.GLFW_RELEASE)