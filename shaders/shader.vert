#version 330
#define PI 3.1415926538

in vec2 inPosition;
out vec4 colorPosition;

uniform float waveFloat;
uniform mat4 view;
uniform mat4 projection;
uniform mat4 model;
uniform int shaderMode;
uniform int meshID;

out vec3 normal;

const float deltaDif = 0.001f;

// Cartesian #1
float bendFunction(vec2 coords) {
    coords = (coords * 2) - 1;
    return 0.5f * cos(sqrt(waveFloat * coords.x * coords.x + 20 * coords.y * coords.y));
}

// Cartesian #2
float bendFunction2(vec2 coords) {
    coords = (coords * 2) - 1;
    return sin(0.8f * coords.x * coords.x + 0.25 * coords.y - 2.5f);
}

// Spherical #1
vec3 objSpehrical1(vec2 position) {
    float z = position.x * PI;
    float a = position.y * 2.f * PI;
    float r = 1.f + cos(2.f * a);

    return vec3(
    sin(z) * cos(a) * r,
    sin(z) * sin(a) * r,
    cos(z) * r);
}

// returns the Z of the adequate function
vec3 getPositionById(vec2 position) {
    // Each case is different object & shape
    switch (meshID) {
        case 0: return vec3(position.x, position.y, bendFunction(position));
        case 1: return vec3(position.x, position.y, bendFunction2(position));
        case 2: return objSpehrical1(position);
    }
    // default
    return vec3(0.f, 1.f, 2.f);
}

// Normals
vec3 getNormal(vec2 xyPosition) {
    vec2 xVecPos = vec2(xyPosition.x + deltaDif, xyPosition.y);
    vec2 xVecNeg = vec2(xyPosition.x - deltaDif, xyPosition.y);
    vec2 yVecPos = vec2(xyPosition.x, xyPosition.y + deltaDif);
    vec2 yVecNeg = vec2(xyPosition.x, xyPosition.y - deltaDif);

    vec3 deltaX = vec3(getPositionById(xVecPos)) - vec3(getPositionById(xVecNeg));
    vec3 deltaY = vec3(getPositionById(yVecPos)) - vec3(getPositionById(yVecNeg));

    return normalize(cross(deltaX, deltaY));
}

void main() {
    vec4 position = vec4(getPositionById(inPosition), 1.0f);
    // Normal transformation
    normal = transpose(inverse(mat3(model))) * getNormal(position.xy);
    // Color xyz position
    colorPosition = model * position;
    gl_Position = projection * view * model * position;
}

