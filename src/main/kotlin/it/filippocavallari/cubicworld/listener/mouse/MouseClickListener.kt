package it.filippocavallari.cubicworld.listener.mouse

import it.filippocavallari.cubicworld.data.block.Material
import it.filippocavallari.cubicworld.world.chunk.WorldManager
import it.filippocavallari.lwge.event.mouse.MouseButtonClickedEvent
import it.filippocavallari.lwge.listener.Listener
import org.lwjgl.glfw.GLFW

class MouseClickListener(val worldManager: WorldManager) : Listener<MouseButtonClickedEvent> {
    override fun onEvent(event: MouseButtonClickedEvent) {
        if(event.key == GLFW.GLFW_MOUSE_BUTTON_1){
            val blockPosition = worldManager.selectedBlock
            blockPosition?.let { position ->
                worldManager.selectedChunk?.let { chunk ->
                    chunk.setBlock(position.x,position.y,position.z, Material.AIR.id)
                    worldManager.recentModifiedChunks.add(chunk)
                }
            }
        }
    }
}