#version 330

out vec4 outColor;

// near far / frustum of our camera
const float near = 0.1f;
const float far = 20.0f;

in vec4 colorPosition;
in vec3 normal;
in vec2 textureCoords;
in float textureScale;

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

// Linearizes the depth value from the depth buffer
float linearizeDepth(float depth){
    return (2.0 * near * far) / (far + near - (depth * 2.0 - 1.0) * (far - near));
}

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
    vec2 scaledTextureCoord = textureCoords * textureScale;


    // Decide which shaderMode to use - render textures, xyz location, normals.. etc.
    switch(shaderMode) {
        // default - complete lighting + texture
        case 0:
            outColor = texture(basicTexture, scaledTextureCoord) * getLightColor();
            break;
        // distance from light
        case 1:
            outColor = (vec4(1.0f));
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