package event

import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.HashSet
import kotlin.reflect.KClass
import kotlin.reflect.full.hasAnnotation

class EventBus {

    private val map = HashMap<KClass<Event>, PriorityQueue<EventQueueElement>>()

    fun dispatchEvent(event: Event){
        val list = map[event::class]
        if(!list.isNullOrEmpty()){
            list.forEach { element ->
                val listener = element.listener
                listener::class.members.forEach{ method ->
                    if(method.hasAnnotation<EventHandler>() && method.parameters.size == 2){
                        @Suppress("UNCHECKED_CAST")
                        if(method.parameters[1].type.classifier as KClass<Event> == event::class){
                            method.call(listener, event)
                        }
                    }
                }
            }
        }
    }

    fun registerListener(eventListener: Listener, priority: EventPriority){
        val hashSet = HashSet<KClass<Event>>()
        eventListener::class.members.forEach{method ->
            if(method.hasAnnotation<EventHandler>() && method.parameters.size == 2){
                @Suppress("UNCHECKED_CAST")
                val eventClass = method.parameters[1].type.classifier as KClass<Event>
                if(!hashSet.contains(eventClass)) {
                    hashSet.add(eventClass)
                    var list = map[eventClass]
                    if(list.isNullOrEmpty()){
                        list = PriorityQueue<EventQueueElement>()
                    }
                    list.add(EventQueueElement(eventListener, priority))
                    map[eventClass] = list
                }
            }
        }
    }

    private class EventQueueElement(val listener: Listener, val priority: EventPriority = EventPriority.NORMAL) : Comparable<EventQueueElement>{
        override fun compareTo(other: EventQueueElement): Int {
            return priority.compareTo(other.priority)
        }
    }

}