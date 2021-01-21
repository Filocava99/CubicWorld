package event

import java.util.*
import kotlin.collections.HashMap
import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation

class EventBus {

    private val map = HashMap<KClass<Event>, PriorityQueue<EventQueueElement>>()

    fun dispatchEvent(event: Event){
        val list = map[event::class]
        if(!list.isNullOrEmpty()){
            list.forEach { element ->
                val listener = element.listener
                element.method.call(listener,  event)
            }
        }
    }

    fun registerListener(eventListener: Listener){
        eventListener::class.members.forEach{method ->
            if(method.hasAnnotation<EventHandler>() && method.parameters.size == 2){
                val priority = method.findAnnotation<EventHandler>()?.priority ?: EventPriority.NORMAL
                @Suppress("UNCHECKED_CAST")
                val eventClass = method.parameters[1].type.classifier as KClass<Event>
                    var list = map[eventClass]
                    if(list.isNullOrEmpty()){
                        list = PriorityQueue<EventQueueElement>()
                    }
                    list.add(EventQueueElement(eventListener, priority, method))
                    map[eventClass] = list
            }
        }
    }

    private class EventQueueElement(val listener: Listener, val priority: EventPriority = EventPriority.NORMAL, val method: KCallable<*>) : Comparable<EventQueueElement>{
        override fun compareTo(other: EventQueueElement): Int {
            return priority.compareTo(other.priority)
        }
    }

}