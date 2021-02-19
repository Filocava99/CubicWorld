package it.filippocavallari.cubicworld.data.model

data class Element(
    val from: FloatArray,
    val to: FloatArray,
    val faces: Map<String, Face>
)
