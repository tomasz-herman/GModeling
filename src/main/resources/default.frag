#version 330 core
out vec4 FragColor;

in VertexData {
    vec3 color;
} vs_in;

void main() {
    FragColor = vec4(vs_in.color, 1.0f);
}