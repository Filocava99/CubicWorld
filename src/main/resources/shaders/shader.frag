#version 330

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
    int hasDepthMap;
    float reflectance;
};

struct Fog
{
    int enabled;
    vec3 color;
    float density;
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

uniform Fog fog;
uniform sampler2D textureSampler;
uniform sampler2D normalMap;
uniform sampler2D depthMap;
uniform vec3 ambientLight;
uniform float specularPower;
uniform Material material;
uniform vec3 cameraPos;

in VertexOutput vertexOutput;

vec4 ambientColor;
vec4 diffuseColor;
vec4 specularColor;
float heightScale = 0.5;

void setupColors(Material material, vec2 textCoord){
    if (material.hasTexture == 1){
        ambientColor = texture(textureSampler, textCoord);
        diffuseColor = ambientColor;
        specularColor = specularColor;
    } else {
        ambientColor = material.ambient;
        diffuseColor = material.diffuse;
        specularColor = material.specular;
    }
}

vec4 calcLightColor(vec3 lightColor, float lightIntensity, vec3 position, vec3 toLightDir, vec3 normal){
    vec4 calulatedDiffuseColor = vec4(0, 0, 0, 0);
    vec4 calculatedSpecularColor = vec4(0, 0, 0, 0);
    // Diffuse Light
    float diffuseFactor = max(dot(normal, toLightDir), 0.0);
    calulatedDiffuseColor = diffuseColor * vec4(lightColor, 1.0) * lightIntensity * diffuseFactor;
    // Specular Light
    vec3 cameraDirection = normalize(-position);
    vec3 fromLightDir = -toLightDir;
    vec3 reflectedLight = normalize(reflect(fromLightDir, normal));
    float specularFactor = max(dot(cameraDirection, reflectedLight), 0.0);
    specularFactor = pow(specularFactor, specularPower);
    calculatedSpecularColor = specularColor * lightIntensity  * specularFactor * material.reflectance * vec4(lightColor, 1.0);

    return (calulatedDiffuseColor + calculatedSpecularColor);
}

vec4 calcPointLight(PointLight light, vec3 position, vec3 normal){
    vec3 lightDirection = light.position - position;
    vec3 toLightDirection = normalize(lightDirection);
    vec4 lightColor = calcLightColor(light.color, light.intensity, position, toLightDirection, normal);
    // Apply Attenuation
    float distance = length(lightDirection);
    float attenuationInv = light.attenuation.constant + light.attenuation.linear * distance +
    light.attenuation.exponent * distance * distance;
    return lightColor / attenuationInv;
}

vec4 calcDirectionalLight(DirectionalLight light, vec3 position, vec3 normal){
    return calcLightColor(light.color, light.intensity, position, normalize(light.direction), normal);
}

vec4 calcFog(vec3 pos, vec4 color, Fog fog, vec3 ambientLight, DirectionalLight dirLight){
    vec3 fogColor = fog.color * (ambientLight + dirLight.color * dirLight.intensity);
    float distance = length(pos);
    float fogFactor = 1.0 / exp((distance * fog.density)* (distance * fog.density));
    fogFactor = clamp(fogFactor, 0.0, 1.0);

    vec3 resultColor = mix(fogColor, color.xyz, fogFactor);
    return vec4(resultColor.xyz, color.w);
}

vec3 calcNormal(Material material, vec3 normal, vec2 text_coord, mat4 modelViewMatrix){
    vec3 newNormal = normal;
    if (material.hasNormalMap == 1){
        newNormal = texture(normalMap, text_coord).rgb;
        newNormal = normalize(newNormal * 2 - 1);
    }
    return newNormal;
}

vec2 parallaxMapping(vec2 texCoords, vec3 viewDir){
    // number of depth layers
    const float minLayers = 8;
    const float maxLayers = 32;
    float numLayers = mix(maxLayers, minLayers, abs(dot(vec3(0.0, 0.0, 1.0), viewDir)));
    // calculate the size of each layer
    float layerDepth = 1.0 / numLayers;
    // depth of current layer
    float currentLayerDepth = 0.0;
    // the amount to shift the texture coordinates per layer (from vector P)
    vec2 P = viewDir.xy / viewDir.z * heightScale;
    vec2 deltaTexCoords = P / numLayers;

    // get initial values
    vec2  currentTexCoords     = texCoords;
    float currentDepthMapValue = texture(depthMap, currentTexCoords).r;

    while (currentLayerDepth < currentDepthMapValue)
    {
        // shift texture coordinates along direction of P
        currentTexCoords -= deltaTexCoords;
        // get depthmap value at current texture coordinates
        currentDepthMapValue = texture(depthMap, currentTexCoords).r;
        // get depth of next layer
        currentLayerDepth += layerDepth;
    }

    // get texture coordinates before collision (reverse operations)
    vec2 prevTexCoords = currentTexCoords + deltaTexCoords;

    // get depth after and before collision for linear interpolation
    float afterDepth  = currentDepthMapValue - currentLayerDepth;
    float beforeDepth = texture(depthMap, prevTexCoords).r - currentLayerDepth + layerDepth;

    // interpolation of texture coordinates
    float weight = afterDepth / (afterDepth - beforeDepth);
    vec2 finalTexCoords = prevTexCoords * weight + currentTexCoords * (1.0 - weight);

    return finalTexCoords;
}

void main(){
    vec2 texCoord = vertexOutput.texCoord;
    if (material.hasDepthMap == 1){
        texCoord = parallaxMapping(vertexOutput.texCoord, normalize(-vertexOutput.vertexPos));
        if (texCoord.x > 1.0 || texCoord.y > 1.0 || texCoord.x < 0.0 || texCoord.y < 0.0) discard;
    }
    setupColors(material, texCoord);
    vec3 newNormal = calcNormal(material, vertexOutput.vertexNormal, texCoord, vertexOutput.modelViewMatrix);
    vec4 diffuseSpecularComponent = calcDirectionalLight(vertexOutput.directionalLight, vertexOutput.vertexPos, newNormal);
    diffuseSpecularComponent += calcPointLight(vertexOutput.pointLight, vertexOutput.vertexPos, newNormal);
    fragColor = ambientColor * vec4(ambientLight, 1) + diffuseSpecularComponent;
    if (fog.enabled == 1){
        fragColor = calcFog(vertexOutput.vertexPos, fragColor, fog, ambientLight, vertexOutput.directionalLight);
    }
}
