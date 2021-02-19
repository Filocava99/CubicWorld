package it.filippocavallari.cubicworld.data.model

data class Model(
    val parent: String?,
    val textures: Map<String, String>,
    val display: Map<String, DisplayProperty>,
    val elements: Array<Element>
)


