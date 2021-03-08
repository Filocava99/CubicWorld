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
    PointLight pointLight;
    DirectionalLight directionalLight;
    mat3 TBN;
    mat4 modelViewMatrix;
    vec2 texCoord;
};

uniform mat4 projectionMatrix;
uniform mat4 modelViewMatrix;
uniform PointLight pointLight;
uniform DirectionalLight directionalLight;
uniform Material material;

out VertexOutput vertexOutput;

void main(){
    vec4 modelViewPos = modelViewMatrix * vec4(position, 1.0);
    gl_Position = projectionMatrix * modelViewPos;
    vertexOutput.texCoord = texCoord;
    vertexOutput.vertexNormal = normalize(modelViewMatrix * vec4(vertexNormal, 0.0)).xyz;
    vertexOutput.vertexPos = modelViewPos.xyz;
   vertexOutput.modelViewMatrix = modelViewMatrix;
    vertexOutput.directionalLight = directionalLight;
    vertexOutput.pointLight = pointLight;
    if(material.hasNormalMap == 1){
        vec3 N = normalize(vec3(modelViewMatrix * vec4(vertexNormal, 0.0)));
        vec3 T = normalize(vec3(modelViewMatrix * vec4(tangent, 0.0)));
        vec3 B = cross(N, T);
        mat3 TBN = transpose(mat3(T, B, N));
        vertexOutput.TBN = TBN;
        vertexOutput.directionalLight.direction = TBN * vertexOutput.directionalLight.direction;
        vertexOutput.pointLight.position = TBN * vertexOutput.pointLight.position;
        vertexOutput.vertexPos = TBN * modelViewPos.xyz;
    }
}