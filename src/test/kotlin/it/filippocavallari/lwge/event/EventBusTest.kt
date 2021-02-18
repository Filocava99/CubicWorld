package it.filippocavallari.lwge.event

import it.filippocavallari.lwge.listener.Listener
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class EventBusTest {

    internal class MyEvent : Event(), Cancellable{
        override var cancelled: Boolean = false
    }

    internal class MyEventListener : Listener<MyEvent> {
        override fun onEvent(event: MyEvent) {
            println("Primo")
            if(!event.cancelled){
                throw Exception("Event should have been cancelled by the previous listener")
            }
        }
    }

    internal class MyEventListenerTwo : Listener<MyEvent> {
        override fun onEvent(event: MyEvent) {
            println("Secondo")
            event.cancelled = true
        }
    }

    @Test
    fun registerListener(){
        val myListener = MyEventListener()
        val eventBus = EventBus()
        assertDoesNotThrow {
            eventBus.register(myListener)
        }
    }

    @Test
    fun dispatchEvent(){
        val myListener = MyEventListener()
        val myListenerTwo = MyEventListenerTwo()
        val eventBus = EventBus()
        eventBus.register(myListener)
        eventBus.register(myListenerTwo, EventPriority.HIGH)
        assertDoesNotThrow {
            eventBus.dispatchEvent(MyEvent())
        }
    }
}