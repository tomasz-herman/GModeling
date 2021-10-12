#version 330 core
layout (location = 0) in vec3 position;
layout (location = 1) in vec3 color;
layout (location = 2) in vec3 normal;
layout (location = 3) in vec2 texture;

uniform mat4 mvp;
uniform mat4 model;

out VertexData {
    vec3 normal;
    vec3 position;
} vs_out;

void main() {
    vs_out.normal = normal;
    vs_out.position = vec3(model * vec4(position, 1.0));
    gl_Position = mvp * vec4(position, 1.0);
}