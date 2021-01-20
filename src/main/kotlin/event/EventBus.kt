package event

import java.util.*
import kotlin.collections.HashMap
import kotlin.reflect.KClass

class EventBus {

    private val map = HashMap<KClass<Event>, PriorityQueue<EventQueueElement>>()

    fun dispatchEvent(event: Event){
        val list = map[event::class]
        if(!list.isNullOrEmpty()){
            list.forEach {
                it.listener.onEvent(event)
            }
        }
    }

    fun registerListener(eventListener: Listener, priority: EventPriority){
        val method = eventListener::class.members.first{it.name == "onEvent"}
        @Suppress("UNCHECKED_CAST")
        val eventClass = method.parameters[1].type.classifier as KClass<Event>
        var list = map[eventClass]
        if(list.isNullOrEmpty()){
            list = PriorityQueue<EventQueueElement>()
        }
        list.add(EventQueueElement(eventListener, priority))
        map[eventClass] = list
    }

    private class EventQueueElement(val listener: Listener, val priority: EventPriority = EventPriority.NORMAL) : Comparable<EventQueueElement>{
        override fun compareTo(other: EventQueueElement): Int {
            return priority.compareTo(other.priority)
        }
    }

}