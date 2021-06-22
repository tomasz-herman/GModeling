#version 410 core
out vec4 FragColor;

in GS_OUT {
    vec2 uv;
} fs_in;

uniform vec3 color;
uniform sampler2D trimming;

void main() {
    if(texture(trimming, fs_in.uv).r == 1) discard;
    FragColor = vec4(color, 1.0);
}