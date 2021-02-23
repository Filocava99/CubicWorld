package it.filippocavallari.cubicworld.json

import com.google.gson.Gson
import it.filippocavallari.cubicworld.data.blockstate.BlockState
import it.filippocavallari.cubicworld.data.model.Model
import java.nio.file.Files
import java.nio.file.Path

object ResourceParser {
    private val gson = Gson()

    fun parseBlockState(name: String): BlockState {
        val path = Path.of("src/main/resources/blocksstates/",name)
        return gson.fromJson(Files.readString(path),BlockState::class.java)
    }

    fun parseModel(name: String): Model {
        val path = Path.of("src/main/resources/models/", name)
        return gson.fromJson(Files.readString(path), Model::class.java)
    }
}