import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import kotlin.math.floor

internal class TimerTest {

    @Test
    fun getLastLoopTime() {
        val timer = Timer()
        val initialTime = timer.time
        Thread.sleep(1000)
        timer.elapsedTime
        assert(timer.lastLoopTime > initialTime)
    }

    @Test
    fun getTime() {
        assertEquals(System.nanoTime()/1000000000,Timer().time.toLong())
    }

    @Test
    fun getElapsedTime() {
        val timer = Timer()
        val sleepTimeInSeconds = 2.0
        Thread.sleep((sleepTimeInSeconds*1000).toLong())
        assertEquals(sleepTimeInSeconds, floor(timer.elapsedTime.toDouble()))
    }
}