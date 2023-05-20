#version 330

in vec2 inPosition;
out vec4 colorPosition;

uniform float waveFloat;
uniform mat4 view;
uniform mat4 projection;
uniform mat4 model;

float bendFunction(vec2 coords) {
    coords = (coords * 2) - 1;
    return 0.5f * cos(sqrt(waveFloat * coords.x * coords.x + 20 * coords.y * coords.y));
}

void main() {
    vec4 position = vec4(inPosition, bendFunction(inPosition.xy), 1.0f);

    colorPosition = model * position;
    gl_Position = projection * view * model * position;
}

