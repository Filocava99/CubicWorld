package it.filippocavallari.lwge

import java.io.File
import java.nio.charset.StandardCharsets
import java.util.*

object Util {

    fun loadResource(fileName: String): String {
        var result: String
        File(fileName).inputStream().use {
            Scanner(it, StandardCharsets.UTF_8.name()).use { scanner ->
                result = scanner.useDelimiter("\\A").next()
            }
        }
        return result
    }

}