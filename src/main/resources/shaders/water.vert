#version 330
layout (location=0) in vec3 position;

out vec4 clipSpace;
out vec2 textureCoords;
out vec3 toCameraVector;

uniform mat4 projectionMatrix;
uniform mat4 modelViewMatrix;
uniform mat4 modelMatrix;
uniform vec3 cameraPosition;

const float tiling = 1.0;

void main(){
    toCameraVector = cameraPosition - (modelMatrix * vec4(position, 1.0)).xyz;
    clipSpace = projectionMatrix * modelViewMatrix * vec4(position, 1.0);
    gl_Position = clipSpace;
    textureCoords = vec2(position.x / 2.0 + 0.5, position.y / 2.0 + 0.5) * tiling;
}