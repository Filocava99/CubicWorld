package event

import java.util.*
import kotlin.collections.HashMap
import kotlin.reflect.KFunction

typealias EventFunction = (Event)->Unit

class EventBus {

    private val map = HashMap<Class<Event>, PriorityQueue<EventQueueElement>>()

    fun dispatchEvent(event: Event){
        val list = map[event::class.java]
        if(!list.isNullOrEmpty()){
            list.forEach {
                println(it.function.parameters)
                println(it.function.name)
                it.function.call(it.listener,it.priority)
            }
        }
    }

    fun registerListener(eventListener: Listener){
        eventListener::class.java.methods.forEach { method ->
            if(method.isAnnotationPresent(EventHandler::class.java)){
                val parameterTypes = method.parameterTypes
                val methodName = method.name
                if(parameterTypes.isNotEmpty()){
                    val eventClass = parameterTypes[0]
                    var list = map[eventClass as Class<Event>]
                    if(list.isNullOrEmpty()){
                        list = PriorityQueue<EventQueueElement>()
                    }
                    val priority = method.getAnnotation(EventHandler::class.java).priority
                    val function = eventListener::class.members.find{it.name == methodName}!! as KFunction<EventFunction>
                    val eventQueueElement = EventQueueElement(priority, function, eventListener)
                    eventQueueElement.function.call(eventQueueElement.listener, eventQueueElement.priority)
                    list.add(eventQueueElement)
                    map[eventClass] = list
                }
            }
        }
    }

    private class EventQueueElement(val priority: EventPriority, val function: KFunction<EventFunction>, val listener: Listener) : Comparable<EventQueueElement>{
        override fun compareTo(other: EventQueueElement): Int {
            return priority.compareTo(other.priority)
        }
    }

}