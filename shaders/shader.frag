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
// spot light
struct SpotLight
{
    PointLight base;
    vec3 direction;
    float edge;
};
// Material
struct Material
{
    float specularIntensity;
    float shininess;
};

uniform DirectionalLight directionalLight;
uniform PointLight pointLight;
uniform SpotLight spotLight;

uniform Material material;
uniform int shaderMode;
uniform int meshID;
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

// Calculates the pointlight
vec4 calcPointLight(PointLight pLight) {
    // Get the direction from fragment to light
    vec3 direction =  pLight.position - fragPos;
    // Distance between light & fragment - calculate before normalizing
    distanceFromLight = length(direction);
    direction = normalize(direction);

    vec4 plColor = calcLightByDirection(pLight.base, direction);
    // attenuation
    float attenuation = pLight.exponent * distanceFromLight * distanceFromLight + pLight.linear * distanceFromLight + pLight.constant;
    // color = plColor / attenuation
    if (attenuation != 0) {
        return plColor / attenuation;
    } else {
        return plColor;
    }
}

vec4 calcSpotLight(SpotLight sLight){
    // direction between fragment & light position
    vec3 rayDirection = normalize(sLight.base.position - fragPos);
    // angle between rayDirection & our spot light direction
    float spotLightFactor = dot(rayDirection, normalize(sLight.direction));
    // check if spotLifghtFactor inside our cone
    if(spotLightFactor > spotLight.edge){
        // Calculate the point light part of spotlight
        vec4 color = calcPointLight(sLight.base);
        return color;
    } else {
        return vec4(0.0f, 0.0f, 0.0f, 0.0f);
    }
}

float calculateDistance() {
    return length(pointLight.position - fragPos);
}

// Returns the final color of light - to multiply the texture.
vec4 getFinalLightColor(){
    return calcDirectionalLight() + calcPointLight(pointLight) + calcSpotLight(spotLight);
}

void main() {
    vec2 scaledTextureCoord = textureCoords * textureScale;
    // Lights object spheres
    if (meshID == 6) {
        outColor = vec4(pointLight.base.color.x, pointLight.base.color.y, pointLight.base.color.z, 1.0f);
        return;
    }
    if (meshID == 7) {
        outColor = vec4(spotLight.base.base.color.x, spotLight.base.base.color.y, spotLight.base.base.color.z, 1.0f);
        return;
    }
    // Decide which shaderMode to use - render textures, xyz location, normals.. etc.
    switch(shaderMode) {
        // default - complete lighting + texture
        case 0:
            outColor = texture(basicTexture, scaledTextureCoord) * getFinalLightColor();
            break;
        // distance from light
        case 1:
            float maxDistance = 10.f;
            float normalizedDistance = 1 - calculateDistance() / maxDistance;
            outColor = vec4(vec3(normalizedDistance), 1.0f);
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