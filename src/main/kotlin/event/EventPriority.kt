package event

enum class EventPriority(val priority: Int) {
    MONITOR(6),
    HIGHEST(5),
    HIGH(4),
    NORMAL(3),
    LOW(2),
    LOWEST(1)
}