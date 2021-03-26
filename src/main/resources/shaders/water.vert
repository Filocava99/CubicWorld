#version 330
layout (location=0) in vec3 position;
layout (location=2) in vec3 vertexNormal;
layout (location=3) in vec3 tangent;

struct DirectionalLight{
    vec3 color;
    vec3 direction;
    float intensity;
};

struct Material{
    vec4 ambient;
    vec4 diffuse;
    vec4 specular;
    int hasTexture;
    int hasNormalMap;
    int hasDepthMap;
    float reflectance;
};

struct VertexOutput{
    vec3 vertexPos;
    vec3 vertexNormal;
    DirectionalLight directionalLight;
    mat3 TBN;
    mat4 modelViewMatrix;
    vec2 texCoord;
    vec3 cameraPos;
};

out vec4 clipSpace;
out vec2 textureCoords;
out vec3 toCameraVector;

uniform mat4 projectionMatrix;
uniform mat4 modelViewMatrix;
uniform mat4 modelMatrix;
uniform vec3 cameraPosition;
uniform DirectionalLight directionalLight;

const float tiling = 1.0;
VertexOutput vertexOutput;

void main(){

    toCameraVector = cameraPosition - (modelMatrix * vec4(position, 1.0)).xyz;
    clipSpace = projectionMatrix * modelViewMatrix * vec4(position, 1.0);
    gl_Position = clipSpace;
    textureCoords = vec2(position.x / 2.0 + 0.5, position.y / 2.0 + 0.5) * tiling;

    vec3 N = normalize(vec3(modelViewMatrix * vec4(vertexNormal, 0.0)));
    vec3 T = normalize(vec3(modelViewMatrix * vec4(tangent, 0.0)));
    vec3 B = cross(N, T);
    mat3 TBN = transpose(mat3(T, B, N));

    vertexOutput.vertexNormal = vertexNormal;
    vertexOutput.TBN = TBN;
    vertexOutput.directionalLight.direction = TBN * vertexOutput.directionalLight.direction;
    vertexOutput.modelViewMatrix =  modelViewMatrix;
    vertexOutput.texCoord = textureCoords;
    vertexOutput.vertexPos = TBN * clipSpace.xyz;
    vertexOutput.cameraPos = TBN * cameraPosition;
}