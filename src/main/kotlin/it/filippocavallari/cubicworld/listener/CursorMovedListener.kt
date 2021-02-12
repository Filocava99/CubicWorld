package it.filippocavallari.cubicworld.listener

import it.filippocavallari.cubicworld.MouseController
import it.filippocavallari.lwge.event.cursor.CursorMovedEvent
import it.filippocavallari.lwge.listener.Listener

class CursorMovedListener(private val mouseController: MouseController) : Listener<CursorMovedEvent> {
    override fun onEvent(event: CursorMovedEvent) {

    }
}