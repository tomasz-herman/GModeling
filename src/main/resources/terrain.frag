#version 330 core
out vec4 FragColor;

uniform sampler2D heights;

in VertexData {
    vec3 position;
    vec2 texture;
} fs_in;

const vec3 tileSize = vec3(1.1, 1.0, 1.1);
const vec3 tilePct = vec3(0.98, 1.0, 0.98);

float mod289(float x){return x - floor(x * (1.0 / 289.0)) * 289.0;}
vec4 mod289(vec4 x){return x - floor(x * (1.0 / 289.0)) * 289.0;}
vec4 perm(vec4 x){return mod289(((x * 34.0) + 1.0) * x);}

float noise(vec3 p){
    vec3 a = floor(p);
    vec3 d = p - a;
    d = d * d * (3.0 - 2.0 * d);

    vec4 b = a.xxyy + vec4(0.0, 1.0, 0.0, 1.0);
    vec4 k1 = perm(b.xyxy);
    vec4 k2 = perm(k1.xyxy + b.zzww);

    vec4 c = k2 + a.zzzz;
    vec4 k3 = perm(c);
    vec4 k4 = perm(c + 1.0);

    vec4 o1 = fract(k3 * (1.0 / 41.0));
    vec4 o2 = fract(k4 * (1.0 / 41.0));

    vec4 o3 = o2 * d.z + o1 * (1.0 - d.z);
    vec2 o4 = o3.yw * d.x + o3.xz * (1.0 - d.x);

    return o4.y * d.y + o4.x * (1.0 - d.y);
}

vec3 marble_color (float x) {
    vec3 col;
    x = 0.5*(x+1.);
    x = sqrt(x);
    x = sqrt(x);
    x = sqrt(x);
    col = vec3(.2 + .75*x);
    col.b*=0.95;
    return col;
}

float turbulence (vec3 P, int numFreq) {
    float val = 0.0;
    float freq = 1.0;
    for (int i=0; i<numFreq; i++) {
        val += abs (noise (P*freq) / freq);
        freq *= 2.07;
    }
    return val;
}

void main() {
    float amplitude = 8.0;
    const int roughness = 4;

    float t = 6.28 * fs_in.position.x / tileSize.x ;
    t += amplitude * turbulence (fs_in.position, roughness);

    t = sin(t);
    vec3 marbleColor = marble_color(t);

    float top = texture2D(heights, fs_in.texture + vec2(0, 0.0001)).r / 100;
    float bot = texture2D(heights, fs_in.texture + vec2(0, -0.0001)).r / 100;
    float left = texture2D(heights, fs_in.texture + vec2(0.0001, 0)).r / 100;
    float right = texture2D(heights, fs_in.texture + vec2(-0.0001, 0)).r / 100;

    vec3 normal = normalize(vec3((bot - top) / 0.0002, 1, (right - left) / 0.0002));

    vec3 lightDir = vec3(0, -1, 0);

    float light = max(dot(normal, -lightDir), 0.2);

    FragColor = vec4(marbleColor * light, 1.0f);
}