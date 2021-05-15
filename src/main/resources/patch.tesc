#version 410 core

layout(vertices = 16) out;

uniform int divisions;

void main() {
    gl_out[ gl_InvocationID ].gl_Position = gl_in[ gl_InvocationID ].gl_Position;
    gl_TessLevelOuter[0] = gl_TessLevelOuter[2] = divisions;
    gl_TessLevelOuter[1] = gl_TessLevelOuter[3] = divisions;
    gl_TessLevelInner[0] = divisions;
    gl_TessLevelInner[1] = divisions;
}
