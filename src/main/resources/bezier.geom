#version 330 core

layout (lines_adjacency) in;
layout (line_strip, max_vertices = 256) out;
uniform int resolution;

void main() {
    vec2 screen0 = gl_in[0].gl_Position.xy / gl_in[0].gl_Position.w;
    vec2 screen1 = gl_in[1].gl_Position.xy / gl_in[1].gl_Position.w;
    vec2 screen2 = gl_in[2].gl_Position.xy / gl_in[2].gl_Position.w;
    vec2 screen3 = gl_in[3].gl_Position.xy / gl_in[3].gl_Position.w;
    float xmax = max(max(screen0.x, screen1.x), max(screen2.x, screen3.x));
    float xmin = min(min(screen0.x, screen1.x), min(screen2.x, screen3.x));
    float ymax = max(max(screen0.y, screen1.y), max(screen2.y, screen3.y));
    float ymin = min(min(screen0.y, screen1.y), min(screen2.y, screen3.y));
    int w = int(min(255, resolution * max(xmax - xmin, ymax - ymin)));
    float dt = 1.0 / w;
    float t = 0.0;
    for (int i = 0; i <= w; i++, t += dt) {
        float omt = 1.0 - t;
        float omt2 = omt * omt;
        float omt3 = omt * omt2;
        float t2 = t * t;
        float t3 = t * t2;
        vec4 xyzw =
            omt3 * gl_in[0].gl_Position +
            3.0 * t * omt2 * gl_in[1].gl_Position +
            3.0 * t2 * omt * gl_in[2].gl_Position +
            t3 * gl_in[3].gl_Position;
        gl_Position = xyzw;
        EmitVertex();
    }
}