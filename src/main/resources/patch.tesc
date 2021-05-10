#version 410 core

layout(vertices = 16)  out;

void main() {
    gl_out[ gl_InvocationID ].gl_Position = gl_in[ gl_InvocationID ].gl_Position;
    gl_TessLevelOuter[0] = gl_TessLevelOuter[2] = 8;
    gl_TessLevelOuter[1] = gl_TessLevelOuter[3] = 8;
    gl_TessLevelInner[0] = 8;
    gl_TessLevelInner[1] = 8;
}
