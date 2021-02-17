package it.filippocavallari.lwge

import it.filippocavallari.lwge.graphic.entity.Entity
import it.filippocavallari.lwge.graphic.light.DirectionalLight
import it.filippocavallari.lwge.graphic.Mesh
import it.filippocavallari.lwge.graphic.light.PointLight
import it.filippocavallari.lwge.graphic.shader.ShaderProgram

class Scene (var gameItems: Map<Mesh,List<Entity>> = HashMap(), val camera: Camera = Camera(), val pointLight: PointLight, val directionalLight: DirectionalLight, val shaderProgram: ShaderProgram){

}