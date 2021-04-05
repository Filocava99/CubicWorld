package it.filippocavallari.cubicworld.listener.keyboard

import it.filippocavallari.lwge.GameEngine
import it.filippocavallari.lwge.event.keyboard.KeyPressedEvent
import it.filippocavallari.lwge.listener.Listener
import org.lwjgl.glfw.GLFW

class DebugModeKeyPressedListener : Listener<KeyPressedEvent> {
    override fun onEvent(event: KeyPressedEvent) {
        if(event.key == GLFW.GLFW_KEY_L){
            GameEngine.toggleDebugMode()
        }
    }
}