#version 330

out vec4 outColor;
in vec3 colorPosition;

void main() {
    outColor = vec4(colorPosition, 1.0);
}