#version 330 core
layout (location = 0) in vec3 position;
layout (location = 1) in vec3 color;

uniform mat4 mvp;
uniform bool grayscale;

out VertexData {
    vec3 color;
} vs_out;

void main() {
    if(grayscale) {
        float gray =
            color.r * 0.299f +
            color.g * 0.587f +
            color.b * 0.114f;
        vs_out.color = vec3(gray);
    } else {
        vs_out.color = color;
    }
    gl_Position = mvp * vec4(position, 1.0);
}