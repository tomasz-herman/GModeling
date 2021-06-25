#version 410 core

layout (triangles) in;
layout (line_strip, max_vertices = 3) out;

in TE_OUT {
    vec2 uv;
    float id;
} gs_in[];

out GS_OUT {
    vec2 uv;
    float id;
} gs_out;

void main() {
    for (int i = 0; i < 3; i++) {
        gl_Position = gl_in[i].gl_Position;
        gs_out.uv = gs_in[i].uv;
        gs_out.id = gs_in[i].id;
        EmitVertex();
    }
}