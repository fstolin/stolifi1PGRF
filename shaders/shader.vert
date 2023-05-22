#version 330

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

// returns the Z of the adequate function
float getZPositionById(vec2 position) {
    // Each case is different object & shape
    switch (meshID) {
        case 0:
        return bendFunction(position);
        case 1:
        return bendFunction2(position);
    }
    // default
    return 0.0f;
}

// Normals
vec3 getNormal(vec2 xyPosition) {
    vec2 xVecPos = vec2(xyPosition.x + deltaDif, xyPosition.y);
    vec2 xVecNeg = vec2(xyPosition.x - deltaDif, xyPosition.y);
    vec2 yVecPos = vec2(xyPosition.x, xyPosition.y + deltaDif);
    vec2 yVecNeg = vec2(xyPosition.x, xyPosition.y - deltaDif);

    vec3 deltaX = vec3(xVecPos, getZPositionById(xVecPos)) - vec3(xVecNeg, getZPositionById(xVecNeg));
    vec3 deltaY = vec3(yVecPos, getZPositionById(yVecPos)) - vec3(yVecNeg, getZPositionById(yVecNeg));

    return normalize(cross(deltaX, deltaY));
}

void main() {
    vec4 position = vec4(inPosition, getZPositionById(inPosition), 1.0f);
    // Normal transformation
    normal = transpose(inverse(mat3(model))) * getNormal(position.xy);
    // Color xyz position
    colorPosition = model * position;
    gl_Position = projection * view * model * position;
}

