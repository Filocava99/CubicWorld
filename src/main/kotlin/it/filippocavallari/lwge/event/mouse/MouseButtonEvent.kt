package it.filippocavallari.lwge.event.mouse

import it.filippocavallari.lwge.event.Event

open class MouseButtonEvent(open val key: Int, val action: Int) : Event()