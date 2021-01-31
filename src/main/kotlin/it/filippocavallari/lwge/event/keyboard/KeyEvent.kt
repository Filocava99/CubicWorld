package it.filippocavallari.lwge.event.keyboard

import it.filippocavallari.lwge.event.Event

open class KeyEvent(open val key: Int, val action: Int) : Event()