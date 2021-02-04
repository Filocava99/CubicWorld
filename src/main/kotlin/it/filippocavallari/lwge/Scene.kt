package it.filippocavallari.lwge

import it.filippocavallari.lwge.graphic.DirectionalLight
import it.filippocavallari.lwge.graphic.Mesh
import it.filippocavallari.lwge.graphic.PointLight

class Scene (var gameItems: Map<Mesh,List<GameItem>> = HashMap(), val camera: Camera = Camera(), val pointLight: PointLight, val directionalLight: DirectionalLight){

}