package event

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class EventHandler(val priority: EventPriority = EventPriority.NORMAL)
