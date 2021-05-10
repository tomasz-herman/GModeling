#version 410 core

layout (triangles) in;
layout (line_strip, max_vertices = 3) out;

void main() {
    for (int i = 0; i <= 3; i++) {
        gl_Position = gl_in[i].gl_Position;
        EmitVertex();
    }
}