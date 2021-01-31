package it.filippocavallari.cubicworld

import it.filippocavallari.lwge.GameEngine

fun main(args:Array<String>) {
    val gameEngine = GameEngine(CubicWorld())
    gameEngine.run()
}