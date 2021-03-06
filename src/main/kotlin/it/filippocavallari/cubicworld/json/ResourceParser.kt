package it.filippocavallari.cubicworld.json

import com.google.gson.Gson
import it.filippocavallari.cubicworld.data.blockstate.BlockState
import it.filippocavallari.cubicworld.data.model.Model
import java.io.File

object ResourceParser {
    private val gson = Gson()

    fun parseBlockState(file: File): BlockState {
        return gson.fromJson(file.readText(),BlockState::class.java)
    }

    fun parseModel(file: File): Model {
        return gson.fromJson(file.readText(), Model::class.java)
    }
}