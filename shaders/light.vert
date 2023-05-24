#version 330
#define PI 3.1415926538

in vec2 inPosition;

uniform mat4 view;
uniform mat4 projection;
uniform mat4 model;

vec3 sphere(vec2 position) {
    // Zenith - 0 - 1pi
    float zen = PI * position.x;
    // Azimuth - 0 - 2pi
    float azi = 2.0f * PI * position.y;
    // Radius
    float rad = 1.2f;

    // to Cartesian
    // x = rad * sin(zenith) * cos (azimuth)
    float x = rad * sin(zen) * cos(azi);
    // y = rad * sin(zenith) * sin (azimuth)
    float y = rad * sin(zen) * sin(azi);
    // z = rad * cos(zenith)
    float z = rad * cos(zen);

    return vec3(x, y, z);
}

void main() {
    gl_Position = projection * view * model * vec4(sphere(inPosition), 1.0f);
}