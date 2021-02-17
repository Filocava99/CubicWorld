package it.filippocavallari.lwge.event

import it.filippocavallari.lwge.listener.Listener
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.full.superclasses

class EventBus {

    val eventHandlers = mutableMapOf<KClass<*>, PriorityQueue<QueueElement<*>>>()

    inline fun <reified T : Event> register(listener: Listener<T>, priority: EventPriority = EventPriority.NORMAL) {
        eventHandlers[T::class]?.add(QueueElement(listener, priority)) ?: run {
            eventHandlers[T::class] = PriorityQueue(mutableListOf(QueueElement(listener, priority)))
        }
    }

    inline fun <reified T : Event> dispatchEvent(event: T) {
        eventHandlers[T::class]?.forEach {
            @Suppress("UNCHECKED_CAST")
            ((it.listener) as? Listener<T>)?.onEvent(event)
        }
    }

    class QueueElement<T : Event>(val listener: Listener<T>, private val priority: EventPriority) :
        Comparable<QueueElement<*>> {
        override fun compareTo(other: QueueElement<*>): Int {
            return priority.compareTo(other.priority)
        }
    }
}