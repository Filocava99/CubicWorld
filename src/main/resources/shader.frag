#version 330

in vec2 outTexCoord;

out vec4 fragColor;

uniform sampler2D texture_sampler;
/*
struct Material
{
    vec4 ambient;
    vec4 diffuse;
    vec4 specular;
    int hasTexture;
    float reflectance;
};
*/
void main(){
    fragColor = texture(texture_sampler, outTexCoord);
}