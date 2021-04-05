package it.filippocavallari.cubicworld

import it.filippocavallari.lwge.GameEngine
import kotlinx.coroutines.runBlocking

fun main(args:Array<String>) {
    runBlocking {
        val gameEngine = GameEngine(CubicWorld())
        gameEngine.run()
    }
}