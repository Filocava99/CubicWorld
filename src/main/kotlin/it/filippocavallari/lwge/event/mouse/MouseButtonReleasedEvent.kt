package it.filippocavallari.lwge.event.mouse

import org.lwjgl.glfw.GLFW.GLFW_RELEASE

class MouseButtonReleasedEventEvent(override val key: Int) : MouseButtonEvent(key, GLFW_RELEASE)