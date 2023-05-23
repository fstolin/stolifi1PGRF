#version 330

out vec4 outColor;

in vec4 colorPosition;
in vec3 normal;
in vec2 textureCoords;

struct DirectionalLight
{
    vec3 color;
    float ambientIntensity;
    vec3 direction;
    float diffuseIntensity;
};

uniform DirectionalLight directionalLight;
uniform int shaderMode;

uniform sampler2D basicTexture;

vec4 getLightColor() {
    // ### AMBIENT ###
    // struct color * ambient intensity
    vec4 ambientColor = vec4(directionalLight.color, 1.0f) * directionalLight.ambientIntensity;
    // ### DIFFUSE COLOR ###
    // calculate the diffuse factor -> cos angle normal * direction
    // A.B =´|A||B|cos(angle) -> when we normalize |A|  and |B| = 1
    // max returns the greater of 2 values
    float diffuseFactor = max(dot(normalize(normal), normalize(directionalLight.direction)), 0.f);
    vec4 diffuseColor = vec4(directionalLight.color, 1.0f) * directionalLight.diffuseIntensity * diffuseFactor;

    // Return the value
    return (ambientColor + diffuseColor);
}

void main() {
    // Decide which shaderMode to use - render textures, xyz location, normals.. etc.
    switch(shaderMode) {
        // default
        case 0:
            outColor = vec4(1.0f, 0.0f, 0.0f, 1.0f);
            break;
        // position
        case 1:
            outColor = vec4(colorPosition.xyz, 1.0);
            break;
        // normals
        case 2:
            outColor = vec4(normal, 1.0);
            break;
        // lighting
        case 3:
            outColor = vec4(0.6,0.6,0.6,1.0) * (getLightColor());
            break;
        case 4:
            outColor = texture(basicTexture, textureCoords);
        break;
    }
}