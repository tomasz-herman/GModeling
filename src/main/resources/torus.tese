#version 410 core

layout(quads, equal_spacing, ccw) in;

uniform float r;
uniform float R;
uniform mat4 mvp;

float x(float u, float v) {
    return cos(u) * (R + r * cos(v));
}

float y(float u, float v) {
    return r * sin(v);
}

float z(float u, float v) {
    return sin(u) * (R + r * cos(v));
}
const float PI = 3.1415926;

void main() {
    float u = gl_TessCoord.x * PI * 2;
    float v = gl_TessCoord.y * PI * 2;
    gl_Position = mvp * vec4(x(u, v), y(u, v), z(u, v), 1);
}
