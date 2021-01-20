import event.*
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import java.lang.Exception

class MyEventListener : Listener {

    @EventHandler
    fun onEvent(event: Event) {
        println("test")
    }


    @EventHandler
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
            eventBus.registerListener(myListener,EventPriority.NORMAL)
        }
    }

    @Test
    fun dispatchEvent(){
        val myListener = MyEventListener()
        val eventBus = EventBus()
        eventBus.registerListener(myListener,EventPriority.NORMAL)
        val event = Event()
        assertDoesNotThrow {
            eventBus.dispatchEvent(event)
        }
    }
}