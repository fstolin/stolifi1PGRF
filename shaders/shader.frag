#version 330

out vec4 outColor;

// near far / frustum of our camera
const float near = 0.1f;
const float far = 20.0f;

vec3 fragToEye;

in vec4 colorPosition;
in vec3 normal;
in vec3 fragPos;
in vec2 textureCoords;
in float textureScale;

// light super
struct Light
{
    vec3 color;
    float ambientIntensity;
    float diffuseIntensity;
};
// directional light
struct DirectionalLight
{
    Light base;
    vec3 direction;
};
// point light
struct PointLight
{
    Light base;
    vec3 position;
    float constant;
    float linear;
    float exponent;
};
// Material
struct Material
{
    float specularIntensity;
    float shininess;
};

uniform DirectionalLight directionalLight;
uniform Material material;
uniform int shaderMode;
uniform vec3 eyePosition;

uniform sampler2D basicTexture;

// Linearizes the depth value from the depth buffer
float linearizeDepth(float depth){
    return (2.0 * near * far) / (far + near - (depth * 2.0 - 1.0) * (far - near));
}

vec4 getLightColor() {
    // ### AMBIENT ###
    // struct color * ambient intensity
    vec4 ambientColor = vec4(directionalLight.base.color, 1.0f) * directionalLight.base.ambientIntensity;
    // ### DIFFUSE COLOR ###
    // calculate the diffuse factor -> cos angle normal * direction
    // A.B =´|A||B|cos(angle) -> when we normalize |A|  and |B| = 1
    // max returns the greater of 2 values
    float diffuseFactor = max(dot(normalize(normal), normalize(directionalLight.direction)), 0.f);
    vec4 diffuseColor = vec4(directionalLight.base.color, 1.0f) * directionalLight.base.diffuseIntensity * diffuseFactor;
    // ### SPECULAR COLOR ###
    // Specular color
    vec4 specularColor = vec4(0.0f, 0.0f, 0.0f, 0.0f);
    // If the object isn't hit with diffuse lighting, it won't be with specular
    if (diffuseFactor > 0.0f)
    {
        // Eye vector
        vec3 fragToEye = normalize(fragPos - eyePosition);
        // Light ray reflection around the normal - 1st argument what to reflect, 2nd around what
        vec3 reflectedVertex = normalize(reflect(directionalLight.direction, normalize(normal)));
        float specularFactor =dot(fragToEye, reflectedVertex);
        // check for shininess
        if (specularFactor > 0.0f)
        {
            specularFactor = pow(specularFactor, material.shininess);
            specularColor = vec4(directionalLight.base.color, 1.0f) * material.specularIntensity * specularFactor;
        }
    }

    // Return the value
    return (ambientColor + diffuseColor + specularColor);
}

void main() {
    vec2 scaledTextureCoord = textureCoords * textureScale;


    // Decide which shaderMode to use - render textures, xyz location, normals.. etc.
    switch(shaderMode) {
        // default - complete lighting + texture
        case 0:
            outColor = texture(basicTexture, scaledTextureCoord) * getLightColor();
            break;
        // distance from light
        case 1:
            vec4 light = getLightColor();
            outColor = (vec4(fragPos, 1.0f));
            break;
        // xyz position
        case 2:
            outColor = vec4(colorPosition.xyz, 1.0);
            break;
        // depth information
        case 3:
            outColor = vec4(vec3(linearizeDepth(gl_FragCoord.z) / far), 1.0f);
            break;
        // normals
        case 4:
            outColor = vec4(normal, 1.0);
            break;
        // texture only
        case 5:
            outColor = texture(basicTexture, scaledTextureCoord);
            break;
        // texture coordinates
        case 6:
            outColor = vec4(textureCoords, 1.0f, 1.0f);
            break;
        // lighting only
        case 7:
            outColor = vec4(0.6,0.6,0.6,1.0) * (getLightColor());
            break;
    }
}