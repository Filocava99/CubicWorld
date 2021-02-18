package it.filippocavallari.lwge

import it.filippocavallari.lwge.Window
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.lwjgl.opengl.GL11.GL_BACK

internal class WindowTest {

    @Test
    fun setVSync() {
        val window = Window("test",1920,1080)
        assertDoesNotThrow {
            window.vSync = true
        }
    }

    @Test
    fun getFullscreen() {
        val window = Window("test",1920,1080)
        assertDoesNotThrow{
            window.enableFullScreen(true)
        }
        assertEquals(window.fullscreen, true)
    }

    @Test
    fun setFullscreen() {
        val window = Window("test",1920,1080)
        assertDoesNotThrow{
            window.enableFullScreen(true)
        }
    }

    @Test
    fun setDebug() {
        val window = Window("test",1920,1080)
        assertDoesNotThrow{
            window.enableDebugMode(true)
        }
    }

    @org.junit.jupiter.api.Test
    fun getWindowId() {
        val window = Window("test",1920,1080)
        assertNotNull(window.windowId)
    }

    @org.junit.jupiter.api.Test
    fun updateClearColor() {
        val window = Window("test",1920,1080)
        assertDoesNotThrow{
            window.updateClearColor()
        }
    }

    @org.junit.jupiter.api.Test
    fun enableVsync() {
        val window = Window("test",1920,1080)
        assertDoesNotThrow{
            window.enableVsync(true)
        }
    }

    @org.junit.jupiter.api.Test
    fun enableDepthTest() {
        val window = Window("test",1920,1080)
        assertDoesNotThrow{
            window.enableDepthTest(true)
        }
    }

    @org.junit.jupiter.api.Test
    fun enableBlending() {
        val window = Window("test",1920,1080)
        assertDoesNotThrow{
            window.enableBlending(true)
        }
    }

    @org.junit.jupiter.api.Test
    fun enableCullFace() {
        val window = Window("test",1920,1080)
        assertDoesNotThrow{
            window.enableCullFace(true)
        }
    }

    @org.junit.jupiter.api.Test
    fun setCullFace() {
        val window = Window("test",1920,1080)
        assertDoesNotThrow{
            window.setCullFace(GL_BACK)
        }
    }

    @org.junit.jupiter.api.Test
    fun showWindow() {
        val window = Window("test",1920,1080)
        assertDoesNotThrow{
            window.showWindow(true)
        }
    }

    @org.junit.jupiter.api.Test
    fun enableDebugMode() {
        val window = Window("test",1920,1080)
        assertDoesNotThrow{
            window.enableDebugMode(true)
        }
    }

    @org.junit.jupiter.api.Test
    fun restoreState() {
        val window = Window("test",1920,1080)
        assertDoesNotThrow{
            window.restoreState()
        }
    }
}