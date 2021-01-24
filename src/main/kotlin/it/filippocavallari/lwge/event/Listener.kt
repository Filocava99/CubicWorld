package it.filippocavallari.lwge.event

fun interface Listener<T: Event> {
    fun onEvent(event: T)
}
