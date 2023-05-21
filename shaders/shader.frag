#version 330

out vec4 outColor;

in vec4 colorPosition;
in vec3 normal;

struct DirectionalLight
{
    vec3 color;
    float ambientIntensity;
    vec3 direction;
    float diffuseIntensity;
};

uniform DirectionalLight directionalLight;

void main() {
    // struct color * ambient intensity
    vec4 ambientColor = vec4(directionalLight.color, 1.0f) * directionalLight.ambientIntensity;
    // calculate the diffuse factor -> cos angle normal * direction
    // A.B =´|A||B|cos(angle) -> when we normalize |A|  and |B| = 1
    // max returns the greater of 2 values
    float diffuseFactor = max(dot(normalize(normal), normalize(directionalLight.direction)), 0.f);

    vec4 diffuseColor = vec4(directionalLight.color, 1.0f) * directionalLight.diffuseIntensity * diffuseFactor;



    // position
    //outColor = vec4(colorPosition.xyz, 1.0);
    // normals
    //outColor = vec4(normal, 1.0);

    outColor = vec4(0.6,0.6,0.6,1.0) * (ambientColor + diffuseColor);
}