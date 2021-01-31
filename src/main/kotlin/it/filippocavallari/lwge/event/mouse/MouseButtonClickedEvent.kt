package it.filippocavallari.lwge.event.mouse

import org.lwjgl.glfw.GLFW.GLFW_PRESS

class MouseButtonClickedEvent(override val key: Int) : MouseButtonEvent(key, GLFW_PRESS)