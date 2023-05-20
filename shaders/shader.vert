#version 330

in vec2 inPosition;
out vec3 colorPosition;

uniform mat4 view;
uniform mat4 projection;

float bendFunction(vec2 coords) {
    coords = (coords * 2) - 1;
    return 0.5f * cos(sqrt(20 * coords.x * coords.x + 20 * coords.y * coords.y));
}

void main() {
    vec4 position = vec4(inPosition, bendFunction(inPosition.xy), 1.0f);

    colorPosition = position.xyz;
    gl_Position = projection * view * position;
}

