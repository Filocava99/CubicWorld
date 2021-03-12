package it.filippocavallari.cubicworld.json

import com.google.gson.Gson
import it.filippocavallari.cubicworld.data.block.Data
import java.io.File

object ResourceParser {
    private val gson = Gson()

    fun parseDataFile(file: File): Data {
        return gson.fromJson(file.readText(), Data::class.java)
    }
}