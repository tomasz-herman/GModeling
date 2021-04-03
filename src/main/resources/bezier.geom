#version 330 core

layout (lines_adjacency) in;
layout (line_strip, max_vertices = 101) out;

void main() {
    float dt = 1.0 / float(100);
    float t = 0.0;
    for (int i = 0; i <= 100; i++) {
        float omt = 1.0 - t;
        float omt2 = omt * omt;
        float omt3 = omt * omt2;
        float t2 = t * t;
        float t3 = t * t2;
        vec4 xyzw =
            omt3 * gl_in[0].gl_Position.xyzw +
            3.0 * t * omt2 * gl_in[1].gl_Position.xyzw +
            3.0 * t2 * omt * gl_in[2].gl_Position.xyzw +
            t3 * gl_in[3].gl_Position.xyzw;
        gl_Position = xyzw;
        EmitVertex();
        t += dt;
    }
}