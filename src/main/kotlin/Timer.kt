class Timer {

    var lastLoopTime = time
        private set

    val time: Double
        get() = System.nanoTime() / 1000000000.0

    val elapsedTime: Float
        get() {
            val time = time
            val elapsedTime = (time - lastLoopTime).toFloat()
            lastLoopTime = time
            return elapsedTime
        }
}