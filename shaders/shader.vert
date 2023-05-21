#version 330

in vec2 inPosition;
out vec4 colorPosition;

uniform float waveFloat;
uniform mat4 view;
uniform mat4 projection;
uniform mat4 model;

out vec3 normal;

const float deltaDif = 0.001f;

// Cartesian #1
float bendFunction(vec2 coords) {
    coords = (coords * 2) - 1;
    return 0.5f * cos(sqrt(waveFloat * coords.x * coords.x + 20 * coords.y * coords.y));
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


void main() {
    vec4 position = vec4(inPosition, bendFunction(inPosition.xy), 1.0f);
    normal = transpose(inverse(mat3(model))) * getNormal(position.xy);
    colorPosition = model * position;
    gl_Position = projection * view * model * position;
}

