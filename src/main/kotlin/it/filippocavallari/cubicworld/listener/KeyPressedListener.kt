package it.filippocavallari.cubicworld.listener

import it.filippocavallari.lwge.Camera
import it.filippocavallari.lwge.event.keyboard.KeyPressedEvent
import it.filippocavallari.lwge.listener.Listener
import org.lwjgl.glfw.GLFW.*

class KeyPressedListener(private val camera: Camera) : Listener<KeyPressedEvent> {
    override fun onEvent(event: KeyPressedEvent) {
        when(event.key){
            GLFW_KEY_W -> camera.movePosition(0f,0f,-1f)
            GLFW_KEY_S -> camera.movePosition(0f,0f,1f)
            GLFW_KEY_A -> camera.movePosition(-1f,0f,0f)
            GLFW_KEY_D -> camera.movePosition(1f,0f,0f)
        }
    }
}