#version 410 core

layout(vertices = 20) out;

uniform int divisionsU;
uniform int divisionsV;

void main() {
    gl_out[ gl_InvocationID ].gl_Position = gl_in[ gl_InvocationID ].gl_Position;
    gl_TessLevelOuter[0] = gl_TessLevelOuter[2] = divisionsU;
    gl_TessLevelOuter[1] = gl_TessLevelOuter[3] = divisionsV;
    gl_TessLevelInner[0] = divisionsV;
    gl_TessLevelInner[1] = divisionsU;
}
