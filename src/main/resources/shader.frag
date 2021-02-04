#version 330

in vec2 outTexCoord;
in vec3 mvVertexNormal;
in vec3 mvVertexPos;
in mat4 outModelViewMatrix;

out vec4 fragColor;

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
    float reflectance;
};

uniform sampler2D texture_sampler;
uniform sampler2D normalMap;
uniform vec3 ambientLight;
uniform float specularPower;
uniform Material material;
uniform PointLight pointLight;
uniform DirectionalLight;

vec4 ambientColor;
vec4 diffuseColor;
vec4 specularColor;

void setupColors(Material material, vec2 textCoord){
    if(material.hasTexture == 1){
        ambientColor = texture(texture_sampler, textCoord);
        diffuseColor = ambientColor;
        specularColor = specularColor;
    }else{
        ambientColor = material.ambient;
        diffuseColor = material.diffuse;
        specularColor = material.specular;
    }
}

vec4 calcPointLight(PointLight light, vec3 position, vec3 normal){
    vec4 calculatedDiffuseColor = vec4(0,0,0,0);
    vec4 calculatedSpecularColor = vec4(0,0,0,0);
    //Diffuse light calculation
    vec3 lightDirection = light.position - position;
    vec3 toLightSource = normalize(lightDirection);
    float diffuseFactor = max(dot(normal,toLightSource),0.0);
    calculatedDiffuseColor = diffuseColor * vec4(light.color,1.0) * light.intensity * diffuseFactor;
    //Specular light
    vec3 cameraDirection = normalize(-position);
    vec3 fromLightSource = -toLightSource;
    vec3 reflectedLight = normalize(reflect(fromLightSource,normal));
    float specularFactor = max(dot(cameraDirection,reflectedLight),0.0);
    specularFactor = pow(specularFactor, specularPower);
    calculatedSpecularColor = specularColor * specularFactor * material.reflectance * vec4(light.color,1.0);
    //Attenuation
    float distance = length(lightDirection);
    float attenuationInv = light.attenuation.constant + light.attenuation.linear * distance + light.attenuation.exponent * distance * distance;

    return (calculatedDiffuseColor+calculatedSpecularColor)/attenuationInv;
}

vec3 calcNormal(Material material, vec3 normal, vec2 text_coord, mat4 modelViewMatrix)
{
    vec3 newNormal = normal;
    if ( material.hasNormalMap == 1 )
    {
        newNormal = texture(normalMap, text_coord).rgb;
        newNormal = normalize(newNormal * 2 - 1);
        newNormal = normalize(modelViewMatrix * vec4(newNormal, 0.0)).xyz;
    }
    return newNormal;
}

void main(){
    setupColors(material,outTexCoord);
    vec3 newNormal = calcNormal(material, mvVertexNormal, outTexCoord, outModelViewMatrix);
    vec4 diffuseSpecularComponent = calcPointLight(pointLight,mvVertexPos, newNormal);
    fragColor = ambientColor * vec4(ambientLight,1) + diffuseSpecularComponent;
}
