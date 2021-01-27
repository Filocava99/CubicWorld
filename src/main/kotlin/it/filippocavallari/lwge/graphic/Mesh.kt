package it.filippocavallari.lwge.graphic

import it.filippocavallari.lwge.data.Vao
import it.filippocavallari.lwge.data.Vbo

data class Mesh(val material: Material, val vao: Vao, val vboSet: Set<Vbo>, val textureVboSet: Set<Vbo>, val vertexCount: Int, val boundingRadius: Float)