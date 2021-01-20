import event.*
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import java.lang.Exception

class MyEventListener : Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    fun onEvent(event: Event) {
        print("test")
    }
}

internal class EventBusTest {

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