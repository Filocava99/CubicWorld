package it.filippocavallari.cubicworld.listener.mouse

import it.filippocavallari.cubicworld.data.block.FaceDirection
import it.filippocavallari.cubicworld.data.block.Material
import it.filippocavallari.cubicworld.world.chunk.WorldManager
import it.filippocavallari.lwge.event.mouse.MouseButtonClickedEvent
import it.filippocavallari.lwge.graphic.entity.Camera
import it.filippocavallari.lwge.listener.Listener
import org.joml.Vector2f
import org.joml.Vector3f
import org.joml.Vector3i
import org.joml.primitives.Intersectionf
import org.lwjgl.glfw.GLFW

class MouseClickListener(val camera: Camera, val worldManager: WorldManager) : Listener<MouseButtonClickedEvent> {
    override fun onEvent(event: MouseButtonClickedEvent) {
        if(event.key == GLFW.GLFW_MOUSE_BUTTON_1){
            val blockPosition = worldManager.selectedBlock
            blockPosition?.let { position ->
                worldManager.selectedChunk?.let { chunk ->
                    chunk.setBlock(position.x,position.y,position.z, Material.AIR.id)
                    worldManager.recentlyModifiedChunks.add(chunk)
                }
            }
        }
        if(event.key == GLFW.GLFW_MOUSE_BUTTON_2){
            val blockPosition = worldManager.selectedBlock
            blockPosition?.let { position ->
                worldManager.selectedChunk?.let { chunk ->
                    val offSet = Vector3f()
                    var faceDirection = FaceDirection.UP
                    val cameraVec = Vector3f(camera.position)
                    var dir = Vector3f()
                    dir = camera.viewMatrix.positiveZ(dir).negate()
                    val  min = Vector3f()
                    val max = Vector3f()
                    val  nearFar = Vector2f()
                    val blockCoordinates =
                        WorldManager.chunkCoordinatesToWorldCoordinates(blockPosition, chunk.position)
                    var closestDistance = Float.POSITIVE_INFINITY
                    if(chunk.getBlock(blockPosition.x,blockPosition.y,blockPosition.z-1) == Material.AIR.id){
                        //south
                        min.set(
                            Vector3f(
                                blockCoordinates.x - 0.5f,
                                blockCoordinates.y - 0.5f,
                                blockCoordinates.z - 0.5f
                            )
                        )
                        max.set(
                            Vector3f(
                                blockCoordinates.x + 0.5f,
                                blockCoordinates.y + 0.5f,
                                blockCoordinates.z -0.4f
                            )
                        )
                        if (Intersectionf.intersectRayAab(
                                cameraVec,
                                dir,
                                min,
                                max,
                                nearFar
                            ) && nearFar.x < closestDistance
                        ) {
                            closestDistance = nearFar.x
                            faceDirection = FaceDirection.SOUTH
                        }
                    }
                    if(chunk.getBlock(blockPosition.x,blockPosition.y,blockPosition.z+1) == Material.AIR.id){
                        //north
                        min.set(
                            Vector3f(
                                blockCoordinates.x - 0.5f,
                                blockCoordinates.y - 0.5f,
                                blockCoordinates.z + 0.4f
                            )
                        )
                        max.set(
                            Vector3f(
                                blockCoordinates.x + 0.5f,
                                blockCoordinates.y + 0.5f,
                                blockCoordinates.z + 0.5f
                            )
                        )
                        if (Intersectionf.intersectRayAab(
                                cameraVec,
                                dir,
                                min,
                                max,
                                nearFar
                            ) && nearFar.x < closestDistance
                        ) {
                            closestDistance = nearFar.x
                            faceDirection = FaceDirection.NORTH
                        }
                    }
                    if(chunk.getBlock(blockPosition.x,blockPosition.y+1,blockPosition.z) == Material.AIR.id){
                        //up
                        min.set(
                            Vector3f(
                                blockCoordinates.x - 0.5f,
                                blockCoordinates.y + 0.4f,
                                blockCoordinates.z - 0.5f
                            )
                        )
                        max.set(
                            Vector3f(
                                blockCoordinates.x + 0.5f,
                                blockCoordinates.y + 0.5f,
                                blockCoordinates.z + 0.5f
                            )
                        )
                        if (Intersectionf.intersectRayAab(
                                cameraVec,
                                dir,
                                min,
                                max,
                                nearFar
                            ) && nearFar.x < closestDistance
                        ) {
                            closestDistance = nearFar.x
                            faceDirection = FaceDirection.UP
                        }
                    }
                    if(chunk.getBlock(blockPosition.x,blockPosition.y-1,blockPosition.z) == Material.AIR.id){
                        //down
                        min.set(
                            Vector3f(
                                blockCoordinates.x - 0.5f,
                                blockCoordinates.y - 0.5f,
                                blockCoordinates.z - 0.5f
                            )
                        )
                        max.set(
                            Vector3f(
                                blockCoordinates.x + 0.5f,
                                blockCoordinates.y - 0.4f,
                                blockCoordinates.z + 0.5f
                            )
                        )
                        if (Intersectionf.intersectRayAab(
                                cameraVec,
                                dir,
                                min,
                                max,
                                nearFar
                            ) && nearFar.x < closestDistance
                        ) {
                            closestDistance = nearFar.x
                            faceDirection = FaceDirection.DOWN
                        }
                    }
                    if(chunk.getBlock(blockPosition.x-1,blockPosition.y,blockPosition.z) == Material.AIR.id){
                        //west
                        min.set(
                            Vector3f(
                                blockCoordinates.x - 0.5f,
                                blockCoordinates.y - 0.5f,
                                blockCoordinates.z - 0.5f
                            )
                        )
                        max.set(
                            Vector3f(
                                blockCoordinates.x - 0.4f,
                                blockCoordinates.y + 0.5f,
                                blockCoordinates.z + 0.5f
                            )
                        )
                        if (Intersectionf.intersectRayAab(
                                cameraVec,
                                dir,
                                min,
                                max,
                                nearFar
                            ) && nearFar.x < closestDistance
                        ) {
                            closestDistance = nearFar.x
                            faceDirection = FaceDirection.WEST
                        }
                    }
                    if(chunk.getBlock(blockPosition.x+1,blockPosition.y,blockPosition.z) == Material.AIR.id){
                        //east
                        min.set(
                            Vector3f(
                                blockCoordinates.x + 0.4f,
                                blockCoordinates.y - 0.5f,
                                blockCoordinates.z - 0.5f
                            )
                        )
                        max.set(
                            Vector3f(
                                blockCoordinates.x + 0.5f,
                                blockCoordinates.y + 0.5f,
                                blockCoordinates.z + 0.5f
                            )
                        )
                        if (Intersectionf.intersectRayAab(
                                cameraVec,
                                dir,
                                min,
                                max,
                                nearFar
                            ) && nearFar.x < closestDistance
                        ) {
                            faceDirection = FaceDirection.EAST
                        }
                    }
                    when(faceDirection){
                        FaceDirection.SOUTH -> offSet.set(0f,0f,-1f)
                        FaceDirection.NORTH -> offSet.set(0f,0f,1f)
                        FaceDirection.UP -> offSet.set(0f,1f,0f)
                        FaceDirection.DOWN -> offSet.set(0f,-1f,0f)
                        FaceDirection.WEST -> offSet.set(-1f,0f,0f)
                        FaceDirection.EAST -> offSet.set(1f,0f,0f)
                    }
                    val blockCoord = Vector3f(position).add(offSet)
                    chunk.setBlock(blockCoord.x.toInt(), blockCoord.y.toInt(), blockCoord.z.toInt(), Material.STONE.id)
                    worldManager.recentlyModifiedChunks.add(chunk)
                }
            }
        }
    }
}