#version 330
layout (location=0) in vec3 position;
layout (location=1) in vec2 texCoord;
layout (location=2) in vec3 vertexNormal;
layout (location=3) in vec3 tangent;
layout (location=4) in vec3 biTangent;

struct Attenuation{
    float constant;
    float linear;
    float exponent;
};

struct PointLight{
    vec3 color;
    vec3 position;
    float intensity;
    Attenuation attenuation;
};

struct DirectionalLight{
    vec3 color;
    vec3 direction;
    float intensity;
};

struct VertexOutput{
    vec3 tangentVertexPos;
    PointLight pointLight;
    DirectionalLight directionalLight;
    mat3 TBN;
};

uniform mat4 projectionMatrix;
uniform mat4 modelViewMatrix;
uniform PointLight pointLight;
uniform DirectionalLight directionalLight;

out mat4 outModelViewMatrix;
out vec2 outTexCoord;
out vec3 mvVertexNormal;
out vec3 mvVertexPos;
out VertexOutput vertexOutput;

void main(){
    vec4 mvPos = modelViewMatrix * vec4(position, 1.0);
    gl_Position = projectionMatrix * mvPos;
    outTexCoord = texCoord;
    mvVertexNormal = normalize(modelViewMatrix * vec4(vertexNormal, 0.0)).xyz;
    mvVertexPos = mvPos.xyz;
    outModelViewMatrix = modelViewMatrix;
    vec3 N = normalize(vec3(modelViewMatrix * vec4(vertexNormal, 0.0)));
    vec3 T = normalize(vec3(modelViewMatrix * vec4(tangent, 0.0)));
    vec3 B = cross(N, T);
    mat3 TBN = transpose(mat3(T, B, N));
    vertexOutput.TBN = TBN;
    vertexOutput.directionalLight = directionalLight;
    vertexOutput.directionalLight.direction = TBN * vertexOutput.directionalLight.direction;
    vertexOutput.pointLight = pointLight;
    vertexOutput.pointLight.position = TBN * vertexOutput.pointLight.position;
    vertexOutput.tangentVertexPos = TBN * mvVertexPos;
}