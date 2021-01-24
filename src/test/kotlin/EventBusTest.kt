import it.filippocavallari.lwge.event.*
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class EventBusTest {

    internal class MyEventListener : Listener<Event> {
        override fun onEvent(event: Event) {
            println("One")
        }
    }

    internal class MyEventListenerTwo : Listener<Event> {
        override fun onEvent(event: Event) {
            println("Two")
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
        val event = Event(1)
        eventBus.register(myListener)
        eventBus.register(myListenerTwo, EventPriority.HIGH)
        assertDoesNotThrow {
            eventBus.dispatchEvent(event)
        }
    }
}