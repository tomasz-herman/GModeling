#version 330 core
out vec4 FragColor;

in VertexData {
    vec3 normal;
} fs_in;

void main() {
    vec3 normal = normalize(fs_in.normal);
    vec3 lightDir = vec3(0, -1, 0);

    float color = max(dot(normal, lightDir), 0.2);

    FragColor = vec4(vec3(color), 1.0f);
}