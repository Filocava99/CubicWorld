#version 330

layout (location=0) in vec3 position;
layout (location=1) in vec2 texCoord;

uniform mat4 projModelMatrix;

out vec2 outTexCoord;

void main(){
    outTexCoord = texCoord;
    gl_Position = projModelMatrix * vec4(position,1);
}