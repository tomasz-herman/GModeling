#version 410 core
out vec4 FragColor;

uniform vec3 color;

in GS_OUT {
    vec2 uv;
} fs_in;

void main() {
    FragColor = vec4(color, 1.0);
}