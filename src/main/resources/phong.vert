#version 330 core
layout (location = 0) in vec3 position;
layout (location = 1) in vec3 color;
layout (location = 2) in vec3 normal;
layout (location = 3) in vec2 texture;

uniform mat4 mvp;

out VertexData {
    vec3 normal;
} vs_out;

void main() {
    vs_out.normal = normal;
    gl_Position = mvp * vec4(position, 1.0);
}