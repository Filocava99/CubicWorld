#version 330

in vec2 outTexCoord;
in vec3 mvVertexNormal;
in vec3 mvVertexPos;
in mat4 outModelViewMatrix;
in mat3 TBN;

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

uniform sampler2D textureSampler;
uniform sampler2D normalMap;
uniform vec3 ambientLight;
uniform float specularPower;
uniform Material material;
uniform PointLight pointLight;
uniform DirectionalLight directionalLight;
uniform vec3 cameraPos;

vec4 ambientColor;
vec4 diffuseColor;
vec4 specularColor;

void setupColors(Material material, vec2 textCoord){
    if(material.hasTexture == 1){
        ambientColor = texture(textureSampler, textCoord);
        diffuseColor = ambientColor;
        specularColor = specularColor;
    }else{
        ambientColor = material.ambient;
        diffuseColor = material.diffuse;
        specularColor = material.specular;
    }
}

vec4 calcLightColour(vec3 lightColor, float lightIntensity, vec3 position, vec3 toLightDir, vec3 normal){
    vec4 calulatedDiffuseColor = vec4(0, 0, 0, 0);
    vec4 calculatedSpecularColor = vec4(0, 0, 0, 0);
    // Diffuse Light
    float diffuseFactor = max(dot(normal, toLightDir), 0.0);
    calulatedDiffuseColor = diffuseColor * vec4(lightColor, 1.0) * lightIntensity * diffuseFactor;
    // Specular Light
    vec3 cameraDirection = normalize(-position);
    vec3 fromLightDir = -toLightDir;
    vec3 reflectedLight = normalize(reflect(fromLightDir , normal));
    float specularFactor = max( dot(cameraDirection, reflectedLight), 0.0);
    specularFactor = pow(specularFactor, specularPower);
    calculatedSpecularColor = specularColor * lightIntensity  * specularFactor * material.reflectance * vec4(lightColor, 1.0);

    return (calulatedDiffuseColor + calculatedSpecularColor);
}

vec4 calcPointLight(PointLight light, vec3 position, vec3 normal){
    vec3 lightDirection = light.position - position;
    vec3 toLightDirection  = normalize(lightDirection);
    vec4 lightColor = calcLightColour(light.color, light.intensity, position, toLightDirection, normal);
    // Apply Attenuation
    float distance = length(lightDirection);
    float attenuationInv = light.attenuation.constant + light.attenuation.linear * distance +
    light.attenuation.exponent * distance * distance;
    return lightColor / attenuationInv;
}

vec4 calcDirectionalLight(DirectionalLight light, vec3 position, vec3 normal){
    return calcLightColour(light.color, light.intensity, position, normalize(light.direction), normal);
}

vec3 calcNormal(Material material, vec3 normal, vec2 text_coord, mat4 modelViewMatrix)
{
    vec3 newNormal = normal;
    if ( material.hasNormalMap == 1 ){
        newNormal = texture(normalMap, text_coord).rgb;
        newNormal = newNormal * 2 - 1;
        newNormal = normalize(TBN*newNormal);
    }
    return newNormal;
}

void main(){
    setupColors(material,outTexCoord);
    vec3 newNormal = calcNormal(material, mvVertexNormal, outTexCoord, outModelViewMatrix);
    vec4 diffuseSpecularComponent = calcDirectionalLight(directionalLight, mvVertexPos, newNormal);
    diffuseSpecularComponent += calcPointLight(pointLight,mvVertexPos, newNormal);
    fragColor = ambientColor * vec4(ambientLight,1) + diffuseSpecularComponent;
}
