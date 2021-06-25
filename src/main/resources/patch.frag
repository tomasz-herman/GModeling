#version 410 core
out vec4 FragColor;

uniform vec3 color;
uniform int U;
uniform int V;
uniform sampler2D trimming;

in GS_OUT {
    vec2 uv;
    float id;
} fs_in;

void main() {
    int H = int(fs_in.id) / V;
    int W = int(fs_in.id) - V * H;
    vec2 uv = vec2((float(H) + fs_in.uv.x) / U, (float(W) + fs_in.uv.y) / V);
    if(texture(trimming, uv).r > 0) discard;
    FragColor = vec4(color, 1.0);
}