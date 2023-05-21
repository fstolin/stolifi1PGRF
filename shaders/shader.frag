#version 330

out vec4 outColor;

in vec4 colorPosition;
in vec3 normal;

void main() {
    vec3 normalizedNormal = normalize(normal);
    vec3 color = normalizedNormal * 0.5 + 0.5; // Map [-1, 1] range to [0, 1]
    outColor = vec4(color, 1.0);
}