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

const float deltaDif = 0.0001f;

// Cartesian #1
vec3 bendFunction(vec2 coords) {
    coords = (coords * 2) - 1;
    float x = coords.x;
    float y = coords.y;
    float z = 0.5f * cos(sqrt(waveFloat * coords.x * coords.x + 20 * coords.y * coords.y));
    return vec3(x, y, z);
}

// Cartesian #2
vec3 bendFunction2(vec2 coords) {
    coords = (coords * 2) - 1;
    float x = coords.x;
    float y = coords.y;
    float z = sin(0.8f * coords.x * coords.x + 0.25 * coords.y - 2.5f);
    return vec3(x, y, z);
}

// Spherical #1
vec3 objSpehrical1(vec2 position) {
    float zen = position.x * PI;
    float azi = position.y * 2.f * PI;
    float rad = 1.f + cos(2.f * azi);

    // to Cartesian
    // x = rad * sin(zenith) * cos (azimuth)
    float x = rad * sin(zen) * cos(azi);
    // y = rad * sin(zenith) * sin (azimuth)
    float y = rad * sin(zen) * sin(azi);
    // z = rad * cos(zenith)
    float z = rad * cos(zen);

    return vec3(x, y, z);
}

// Sphere testing
vec3 sphereShape(vec2 position) {
    // Zenith - 0 - 1pi
    float zen = PI * position.x;
    // Azimuth - 0 - 2pi
    float azi = 2 * PI * position.y;
    // Radius
    float rad = 0.3f;

    // to Cartesian
    // x = rad * sin(zenith) * cos (azimuth)
    float x = rad * sin(zen) * cos(azi);
    // y = rad * sin(zenith) * sin (azimuth)
    float y = rad * sin(zen) * sin(azi);
    // z = rad * cos(zenith)
    float z = rad * cos(zen);

    return vec3(x, y, z);
}

// returns the Z of the adequate function
vec3 getPositionById(vec2 position) {
    // Each case is different object & shape
    switch (meshID) {
        case 0: return bendFunction(position);
        case 1: return bendFunction2(position);
        case 2: return sphereShape(position);
        case 3: return objSpehrical1(position);
    }
    // default
    return vec3(0.f, 1.f, 2.f);
}

// Normals
vec3 getNormal(vec2 xyPosition) {

    vec2 dx = vec2(deltaDif, 0);
    vec2 dy = vec2(0, deltaDif);

    vec3 deltaX = getPositionById(xyPosition + dx) - getPositionById(xyPosition - dx);
    vec3 deltaY = getPositionById(xyPosition + dy) - getPositionById(xyPosition - dy);


    return normalize(cross(deltaX, deltaY));
}

void main() {
    vec4 position = vec4(getPositionById(inPosition), 1.0f);
    // Normal transformation
    normal = transpose(inverse(mat3(model))) * getNormal(inPosition.xy);

    // Color xyz position
    colorPosition = model * position;
    gl_Position = projection * view * model * position;
}

