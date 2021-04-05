package it.filippocavallari.lwge

import it.filippocavallari.lwge.graphic.Fog
import it.filippocavallari.lwge.graphic.entity.Entity
import it.filippocavallari.lwge.graphic.light.DirectionalLight
import it.filippocavallari.lwge.graphic.Mesh
import it.filippocavallari.lwge.graphic.SkyBox
import it.filippocavallari.lwge.graphic.entity.Camera
import it.filippocavallari.lwge.graphic.light.PointLight
import it.filippocavallari.lwge.graphic.shader.ShaderProgram
import org.joml.Vector3f

class Scene (var entities: MutableMap<Mesh,List<Entity>> = HashMap(), var waterEntities: MutableMap<Mesh,List<Entity>> = HashMap(), val camera: Camera = Camera(), val ambientLight: Vector3f, val pointLight: PointLight, val directionalLight: DirectionalLight, val fog: Fog, val skyBox: SkyBox, val shaderProgram: ShaderProgram){

}