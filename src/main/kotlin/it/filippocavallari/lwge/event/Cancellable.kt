package it.filippocavallari.lwge.event

interface Cancellable {

    fun isCancelled():Boolean

    fun setCancelled(cancelled: Boolean)

}