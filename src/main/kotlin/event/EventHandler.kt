package event

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class EventHandler(val priority: EventPriority = EventPriority.NORMAL)
