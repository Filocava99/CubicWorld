import event.*
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class EventBusTest {

    internal class EventOne : Event(){

    }

    internal class EventTwo : Event(){

    }

    internal class MyEventListener : Listener {

        @EventHandler
        fun onEvent(event: EventOne) {
            println("Event one")
        }

        @EventHandler(priority = EventPriority.HIGHEST)
        fun onEventTwo(event: EventTwo){
            println("First")
        }

        @EventHandler
        fun onEventThree(event: EventTwo){
            println("Second")
        }
    }

    @Test
    fun registerListener(){
        val myListener = MyEventListener()
        val eventBus = EventBus()
        assertDoesNotThrow {
            eventBus.registerListener(myListener)
        }
    }

    @Test
    fun dispatchEvent(){
        val myListener = MyEventListener()
        val eventBus = EventBus()
        eventBus.registerListener(myListener)
        val event = Event()
        assertDoesNotThrow {
            eventBus.dispatchEvent(event)
        }
    }
}