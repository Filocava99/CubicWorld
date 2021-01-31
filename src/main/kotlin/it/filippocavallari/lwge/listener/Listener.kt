package it.filippocavallari.lwge.listener

import it.filippocavallari.lwge.event.Event

fun interface Listener<T: Event> {
    fun onEvent(event: T)
}
