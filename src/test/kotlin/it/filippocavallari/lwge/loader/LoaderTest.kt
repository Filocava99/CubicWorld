package it.filippocavallari.lwge.loader

import it.filippocavallari.lwge.Window
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.assertDoesNotThrow

internal class LoaderTest {

    @Test
    fun getVBO() {
        val window = Window("test",100,100)
        window.showWindow(false)
        assert(Loader.getVBO().id > 0)
    }

    @Test
    fun getVAO() {
        val window = Window("test",100,100)
        window.showWindow(false)
        assert(Loader.getVAO().id > 0)
    }

    @Test
    fun createVAOs() {
        val window = Window("test",100,100)
        window.showWindow(false)
        assertDoesNotThrow {
            Loader.createVAOs(10)
        }
    }

    @Test
    fun createVBOs() {
        val window = Window("test",100,100)
        window.showWindow(false)
        assertDoesNotThrow {
            Loader.createVBOs(10)
        }
    }

    @Test
    fun loadVerticesInVbo() {
        val window = Window("test",100,100)
        window.showWindow(false)
        val vertices = floatArrayOf(
            //FRONT FACE
            0f,1f,0f, //0
            0f,0f,0f, //1
            1f,0f,0f, //2
            1f,1f,0f, //3
            //RIGHT FACE
            1f,1f,0f, //4
            1f,0f,0f, //5
            1f,0f,1f, //6
            1f,1f,1f, //7
            //LEFT FACE
            0f,1f,1f, //8
            0f,0f,1f, //9
            0f,0f,0f, //10
            0f,1f,0f, //11
            //TOP FACE
            0f,1f,1f, //12
            0f,1f,0f, //13
            1f,1f,0f, //14
            1f,1f,1f, //15
            //BOTTOM FACE
            1f,0f,1f, //16
            1f,0f,0f, //17
            0f,0f,0f, //18
            0f,0f,1f, //19
            //BACK FACE
            1f,1f,1f, //20
            1f,0f,1f, //21
            0f,0f,1f, //22
            0f,1f,1f //23
        )
        assertDoesNotThrow {
            Loader.loadVerticesInVbo(Loader.getVBO(),vertices)
        }
    }

    @Test
    fun loadIndicesInVbo() {
        val window = Window("test",100,100)
        window.showWindow(false)
        val indices = intArrayOf(
            //front
            0,1,3,3,1,2,
            //right
            4,5,7,7,5,6,
            //left
            8,9,11,11,9,10,
            //top
            12,13,15,15,13,14,
            //bottom
            16,17,19,19,17,18,
            //back
            20,21,23,23,21,22
        )
        assertDoesNotThrow { Loader.loadIndicesInVbo(Loader.getVBO(),indices) }
    }

    @Test
    fun loadUVsInVbo() {
        val window = Window("test",100,100)
        window.showWindow(false)
        val uvs = floatArrayOf(
            0f,0f,0f,1f,1f,1f,1f,0f,
            0f,0f,0f,1f,1f,1f,1f,0f,
            0f,0f,0f,1f,1f,1f,1f,0f,
            0f,0f,0f,1f,1f,1f,1f,0f,
            0f,0f,0f,1f,1f,1f,1f,0f,
            0f,0f,0f,1f,1f,1f,1f,0f,
        )
        assertDoesNotThrow {
            Loader.loadUVsInVbo(Loader.getVBO(), uvs)
        }
    }

    @Test
    fun loadNormalsInVbo() {
        val window = Window("test",100,100)
        window.showWindow(false)
        val normals = floatArrayOf(
            //FRONT
            0f,0f,-1f,
            0f,0f,-1f,
            0f,0f,-1f,
            0f,0f,-1f,
            //RIGHT
            1f,0f,0f,
            1f,0f,0f,
            1f,0f,0f,
            1f,0f,0f,
            //LEFT
            -1f,0f,0f,
            -1f,0f,0f,
            -1f,0f,0f,
            -1f,0f,0f,
            //TOP
            0f,1f,0f,
            0f,1f,0f,
            0f,1f,0f,
            0f,1f,0f,
            //BOTTOM
            0f,-1f,0f,
            0f,-1f,0f,
            0f,-1f,0f,
            0f,-1f,0f,
            //BACK

            0f,0f,1f,
            0f,0f,1f,
            0f,0f,1f,
            0f,0f,1f,
        )
        Loader.loadNormalsInVbo(Loader.getVBO(),normals)
    }

}