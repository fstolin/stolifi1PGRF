#version 330

out vec4 outColor;
in vec4 colorPosition;

void main() {
    outColor = vec4(colorPosition.xyz, 1.0);
}