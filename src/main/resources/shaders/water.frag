#version 330

in vec4 clipSpace;
in vec2 textureCoords;
in vec3 toCameraVector;

out vec4 outColor;


uniform sampler2D reflectionTexture;
uniform sampler2D refractionTexture;
uniform sampler2D dudvMap;
uniform sampler2D normalMap;
uniform float moveFactor;

const float waveStrength = 0.003;

void main() {

    vec2 distortedTexCoords = texture(dudvMap, vec2(textureCoords.x + moveFactor, textureCoords.y)).rg*0.1;
    distortedTexCoords = textureCoords + vec2(distortedTexCoords.x, distortedTexCoords.y+moveFactor);
    vec2 totalDistortion = (texture(dudvMap, distortedTexCoords).rg * 2.0 - 1.0) * waveStrength;

    vec2 normalizedDeviceSpace = (clipSpace.xy/clipSpace.w)/2.0+0.5;
    vec2 refractTexCoords = vec2(normalizedDeviceSpace.x, normalizedDeviceSpace.y) + totalDistortion;
    refractTexCoords = clamp(refractTexCoords, 0.001, 0.999);
    vec2 reflectTexCoords = vec2(normalizedDeviceSpace.x, -normalizedDeviceSpace.y) + totalDistortion; //Y is inverted because it's the reflection
    reflectTexCoords.x = clamp(reflectTexCoords.x, 0.001, 0.999);
    reflectTexCoords.y = clamp(reflectTexCoords.y, -0.999, -0.001);
    vec4 reflectColor = texture(reflectionTexture, reflectTexCoords);
    vec4 refractColor = texture(refractionTexture, refractTexCoords);

    vec3 viewVector = normalize(toCameraVector);
    float refractiveFactor = dot(viewVector, vec3(0.0, 1.0, 0.0));
    refractiveFactor = pow(refractiveFactor, 3);

    vec4 normalmapColor = texture(normalMap, distortedTexCoords);
    vec3 normal = vec3(normalmapColor.r * 2 -1, normalmapColor.b, normalmapColor.g*2-1);
    normal = normalize(normal);
    outColor = mix(reflectColor,refractColor, refractiveFactor);
    outColor = mix(outColor, vec4(0.0, 0.3, 0.5, 1.0), 0.2);
    outColor = mix(outColor, vec4(normal,1), 0.00001);
}
