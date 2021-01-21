import event.*
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class EventBusTest {

    internal class MyEventListener : Listener {

        @EventHandler
        fun onEvent(event: Event) {
        }

        @EventHandler(priority = EventPriority.HIGHEST)
        fun onEventTwo(event: Event){
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