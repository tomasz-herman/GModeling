#version 330 core
out vec4 FragColor;

uniform sampler2D rightTexture;
uniform sampler2D leftTexture;

in vec2 UV;

void main() {
    vec3 left = texture(leftTexture, UV).xyz;
    vec3 right = texture(rightTexture, UV).xyz;
    float red = clamp(0.4561 * left.r + 0.500484 * left.g + 0.176381 * left.b - 0.0434706 * right.r - 0.0879388 * right.g - 0.00155529 * right.b, 0, 1);
    float green = clamp(-0.0400822 * left.r - 0.0378246 * left.g - 0.0157589 * left.b + 0.378476 * right.r + 0.73364 * right.g - 0.0184503 * right.b, 0, 1);
    float blue = clamp(-0.0152161 * left.r - 0.0205971 * left.g - 0.00546856 * left.b - 0.0721527 * right.r - 0.112961 * right.g + 1.2264 * right.b, 0, 1);
    gl_FragColor = vec4(red, green, blue, 1);
}
