package it.filippocavallari.lwge

interface GameLogic {

    fun init()

    fun input()

    fun update(interval: Float)

    fun render()

    fun cleanUp()

}