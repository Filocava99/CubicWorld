import event.*
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import java.lang.Exception

class MyEventListener : Listener {

    @EventHandler
    fun onEvent(event: Event) {
        println("test")
    }


    @EventHandler(priority = EventPriority.HIGHEST)
    fun onEventTwo(event: Event){
        println("test2")
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