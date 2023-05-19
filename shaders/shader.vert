#version 330

in vec2 inPosition;

uniform mat4 view;
uniform mat4 projection;

void main() {
    vec4 position = vec4(inPosition, 0.0f, 1.0f);

    gl_Position = projection * view * position;
}