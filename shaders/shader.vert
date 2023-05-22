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
    return sin(2f * coords.x * coords.x + 0.25 * coords.y);
}

// Normals
vec3 getNormal(vec2 xyPosition) {
    vec2 xVecPos = vec2(xyPosition.x + deltaDif, xyPosition.y);
    vec2 xVecNeg = vec2(xyPosition.x - deltaDif, xyPosition.y);
    vec2 yVecPos = vec2(xyPosition.x, xyPosition.y + deltaDif);
    vec2 yVecNeg = vec2(xyPosition.x, xyPosition.y - deltaDif);

    vec3 deltaX = vec3(xVecPos, bendFunction(xVecPos)) - vec3(xVecNeg, bendFunction(xVecNeg));
    vec3 deltaY = vec3(yVecPos, bendFunction(yVecPos)) - vec3(yVecNeg, bendFunction(yVecNeg));

    return normalize(cross(deltaX, deltaY));
}

// Returns the correspoindg position for the shape based on MeshID
vec4 getPositionById() {
    // Each case is different object & shape
    switch (meshID) {
        case 0:
            return vec4(inPosition.xy, bendFunction(inPosition.xy), 1.0f);
        case 1:
            return vec4(inPosition.xy, bendFunction2(inPosition.xy), 1.0f);
    }
    // default = plane -> shouldn't happen
    return vec4(inPosition.xy, 0.0f, 1.0f);
}


void main() {
    vec4 position = getPositionById();
    // Normal transformation
    normal = transpose(inverse(mat3(model))) * getNormal(position.xy);
    // Color xyz position
    colorPosition = model * position;
    gl_Position = projection * view * model * position;
}

