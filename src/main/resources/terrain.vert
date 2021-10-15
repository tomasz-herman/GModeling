#version 400
layout (location = 0) in vec3 position;
layout (location = 1) in vec3 color;
layout (location = 2) in vec3 normal;
layout (location = 3) in vec2 texture;

uniform sampler2D heights;
uniform mat4 mvp;
uniform mat4 model;

out VertexData {
    vec3 position;
    vec2 texture;
} vs_out;

void main() {
    vs_out.position = vec3(model * vec4(position, 1.0));
    vs_out.texture = texture;
    float height = texture2D(heights, texture).r / 100;

    gl_Position = mvp * vec4(position.x, position.y + height, position.z, 1.0);
}