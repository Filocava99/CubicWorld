package it.filippocavallari.lwge.loader

import it.filippocavallari.lwge.graphic.Texture
import org.lwjgl.opengl.GL11C.*
import org.lwjgl.opengl.GL30C.glGenerateMipmap
import org.lwjgl.stb.STBImage.*
import org.lwjgl.system.MemoryStack
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.OutputStream
import java.nio.ByteBuffer


object TextureLoader {
    private var height = 0
    private var width = 0
    private var byteBuffer: ByteBuffer? = null

    fun createTexture(filename: String): Texture? {
        var texture: Texture? = null
        try {

            MemoryStack.stackPush().use { stack ->
                val w = stack.mallocInt(1)
                val h = stack.mallocInt(1)
                val channels = stack.mallocInt(1)
                val outputStream: OutputStream = FileOutputStream("temp.png")
                val inputStream =
                    Thread.currentThread().contextClassLoader.getResourceAsStream(filename)!!
                inputStream.transferTo(outputStream)
                inputStream.close()
                outputStream.close()
                byteBuffer = stbi_load("temp.png", w, h, channels, 4)
                if (byteBuffer == null) {
                    throw FileNotFoundException("Texture file [" + filename + "] not loaded. Reason: " + stbi_failure_reason())
                }
                File("temp.png").delete()
                //Get width and height of image
                width = w.get()
                height = h.get()
                val textureID = generateTexture()
                generateMipMap()
                clean()
                texture = Texture(textureID, width, height)
            }
        } catch (e: Exception) {
        }
        return texture
    }

    //For scaled textures
    private fun generateMipMap() {
        glGenerateMipmap(GL_TEXTURE_2D)
    }

    private fun generateTexture(): Int {
        val textureId: Int = glGenTextures()
        // Bind the texture
        glBindTexture(GL_TEXTURE_2D, textureId)
        //Tell OpenGL how to unpack RGBA. 1 byte for pixel
        glPixelStorei(GL_UNPACK_ALIGNMENT, 1)
        /*Args:
              1. Type of texture;
              2. Number of colour components in the texture;
              3. Colour components in texture;
              4. Texture width;
              5. Texture height;
              6. Texture border size;
              7. Format of the pixel data (RGBA);
              8. Each pixel is represented by an unsigned int;
              9. Data to load is stored in a ByteBuffer
         */
        glTexImage2D(
            GL_TEXTURE_2D, 0, GL_RGBA, width, height,
            0, GL_RGBA, GL_UNSIGNED_BYTE, byteBuffer
        )
        return textureId
    }

    private fun clean() {
        //Free ByteBuffer
        byteBuffer?.let { stbi_image_free(it) }
    }
}

private inline fun MemoryStack.use(block: (MemoryStack) -> Unit) {
    block(this)
}
