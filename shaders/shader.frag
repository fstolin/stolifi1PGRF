#version 330

out vec4 outColor;

// near far / frustum of our camera
const float near = 0.1f;
const float far = 20.0f;

vec3 fragToEye;
float distanceFromLight;

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
uniform PointLight pointLight;

uniform Material material;
uniform int shaderMode;
uniform vec3 eyePosition;

uniform sampler2D basicTexture;

// Linearizes the depth value from the depth buffer
float linearizeDepth(float depth){
    return (2.0 * near * far) / (far + near - (depth * 2.0 - 1.0) * (far - near));
}

// Calculates directional factor for lights
vec4 calcLightByDirection(Light light, vec3 direction) {
    // ### AMBIENT ###
    // struct color * ambient intensity
    vec4 ambientColor = vec4(light.color, 1.0f) * light.ambientIntensity;
    // ### DIFFUSE COLOR ###
    // calculate the diffuse factor -> cos angle normal * direction
    // A.B =´|A||B|cos(angle) -> when we normalize |A|  and |B| = 1
    // max returns the greater of 2 values
    float diffuseFactor = max(dot(normalize(normal), normalize(direction)), 0.f);
    vec4 diffuseColor = vec4(light.color, 1.0f) * light.diffuseIntensity * diffuseFactor;
    // ### SPECULAR COLOR ###
    // Specular color
    vec4 specularColor = vec4(0.0f, 0.0f, 0.0f, 0.0f);
    // If the object isn't hit with diffuse lighting, it won't be with specular
    if (diffuseFactor > 0.0f)
    {
        // Eye vector
        vec3 fragToEye = normalize(fragPos - eyePosition);
        // Light ray reflection around the normal - 1st argument what to reflect, 2nd around what
        vec3 reflectedVertex = normalize(reflect(direction, normalize(normal)));
        float specularFactor =dot(fragToEye, reflectedVertex);
        // check for shininess
        if (specularFactor > 0.0f)
        {
            specularFactor = pow(specularFactor, material.shininess);
            specularColor = vec4(light.color, 1.0f) * material.specularIntensity * specularFactor;
        }
    }
    // Return the value
    return (ambientColor + diffuseColor + specularColor);
}

// Calculates directional light
vec4 calcDirectionalLight() {
    return calcLightByDirection(directionalLight.base, directionalLight.direction);
}

// Calculates the point light
vec4 calcPointLight() {
    // Get the direction from fragment to light
    vec3 direction = fragPos - pointLight.position;
    // Distance between light & fragment - calculate before normalizing
    distanceFromLight = length(direction);
    direction = normalize(direction);

    vec4 plColor = calcLightByDirection(pointLight.base, direction);
    // attenuation
    float attenuation = pointLight.exponent * distanceFromLight * distanceFromLight + pointLight.linear * distanceFromLight + pointLight.constant;
    // color = plColor / attenuation
    if (attenuation != 0) {
        return plColor / attenuation;
    } else {
        return plColor;
    }
}

// Returns the final color of light - to multiply the texture.
vec4 getFinalLightColor(){
    return calcDirectionalLight() + calcPointLight();
}

void main() {
    vec2 scaledTextureCoord = textureCoords * textureScale;

    // Decide which shaderMode to use - render textures, xyz location, normals.. etc.
    switch(shaderMode) {
        // default - complete lighting + texture
        case 0:
            outColor = texture(basicTexture, scaledTextureCoord) * getFinalLightColor();
            break;
        // distance from light
        case 1:
            //vec4 light = getLightColor();
            outColor = (vec4(vec3(distanceFromLight), 1.0f));
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
            outColor = vec4(0.6,0.6,0.6,1.0) * getFinalLightColor();
            break;
    }
}