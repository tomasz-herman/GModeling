#version 410 core
out vec4 FragColor;

uniform vec3 color;
uniform int U;
uniform int V;

in GS_OUT {
    vec2 uv;
    float id;
} fs_in;

void main() {
    int H = int(fs_in.id) / V;
    int W = int(fs_in.id) - V * H;
    vec2 uv = vec2((float(H) + fs_in.uv.x) / U, (float(W) + fs_in.uv.y) / V);
    FragColor = vec4(uv, 1.0, 1.0);
}