# CubicWorld

## Introduction
CubicWorld is a prototype of a Minecraft clone built with LWJGL 3 and Kotlin. It aims to improve both visual effects and texture quality without compromising the performances.
That project was created as a thesis in Computer Graphics for the faculty of Engineering and Computer Science at the Alma Mater Studiorum University of Bologna.

## Current features
* Normal and parallax mapping for adding more depth to the models;
* A new models system, based on obj instead of the Minecraft private format;
* The water rendering is based on the Fresnel effect, which take into consideration both the refraction and the reflection coefficient of the material;
* New water effects created using normal maps and DuDv maps;
* Better rendering process using a second OpenGL context to balance the load.
* Frustum culling
* Mipmapping
* Collision detection
* World interaction (break/place blocks)

## Future implementations
* Raytraced light
* Shadows
* World serialization
* Better collision detection
* Particles
* Sounds

## Screenshots

![](https://i.imgur.com/85hMlPU.png)
![](https://i.imgur.com/hKWJGEO.png)
