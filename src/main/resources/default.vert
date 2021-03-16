#version 330 core
layout (location = 0) in vec3 position;
layout (location = 1) in vec3 color;

uniform mat4 mvp;

out VertexData {
    vec3 color;
} vs_out;

void main() {
    vs_out.color = color;
    gl_Position = mvp * vec4(position, 1.0);
}