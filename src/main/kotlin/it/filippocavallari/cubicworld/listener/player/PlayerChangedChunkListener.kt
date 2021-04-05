package it.filippocavallari.cubicworld.listener.player

import it.filippocavallari.cubicworld.event.PlayerChangedChunkEvent
import it.filippocavallari.cubicworld.world.chunk.WorldManager
import it.filippocavallari.lwge.listener.Listener
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class PlayerChangedChunkListener(val worldManager: WorldManager) : Listener<PlayerChangedChunkEvent> {
    override fun onEvent(event: PlayerChangedChunkEvent) {
        GlobalScope.launch {
            worldManager.updateActiveChunks(event.newChunkPosition)
        }
    }
}