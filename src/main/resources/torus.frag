#version 410 core
out vec4 FragColor;

in GS_OUT {
    vec2 uv;
} fs_in;

uniform vec3 color;
uniform sampler2D trimming;
uniform int right;
uniform int left;

void main() {
    if(texture(trimming, fs_in.uv).r == 1) {
        if(right == 0) discard;
    } else if(left == 0) discard;
    FragColor = vec4(color, 1.0);
}