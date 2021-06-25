#version 410 core

layout(quads, equal_spacing, ccw) in;

out TE_OUT {
    vec2 uv;
    float id;
} te_out;

void main() {
    vec4 p00 = gl_in[ 0].gl_Position;
    vec4 p10 = gl_in[ 1].gl_Position;
    vec4 p20 = gl_in[ 2].gl_Position;
    vec4 p30 = gl_in[ 3].gl_Position;
    vec4 p01 = gl_in[ 4].gl_Position;
    vec4 p11 = gl_in[ 5].gl_Position;
    vec4 p21 = gl_in[ 6].gl_Position;
    vec4 p31 = gl_in[ 7].gl_Position;
    vec4 p02 = gl_in[ 8].gl_Position;
    vec4 p12 = gl_in[ 9].gl_Position;
    vec4 p22 = gl_in[10].gl_Position;
    vec4 p32 = gl_in[11].gl_Position;
    vec4 p03 = gl_in[12].gl_Position;
    vec4 p13 = gl_in[13].gl_Position;
    vec4 p23 = gl_in[14].gl_Position;
    vec4 p33 = gl_in[15].gl_Position;
    float u = gl_TessCoord.x;
    float v = gl_TessCoord.y;

    vec4 b00 = 1.0 / 6.0 * p00 + 2.0 / 3.0 * p10 + 1.0 / 6.0 * p20;
    vec4 b01 = 2.0 / 3.0 * p10 + 1.0 / 3.0 * p20;
    vec4 b02 = 1.0 / 3.0 * p10 + 2.0 / 3.0 * p20;
    vec4 b03 = 1.0 / 6.0 * p10 + 2.0 / 3.0 * p20 + 1.0 / 6.0 * p30;

    vec4 b10 = 1.0 / 6.0 * p01 + 2.0 / 3.0 * p11 + 1.0 / 6.0 * p21;
    vec4 b11 = 2.0 / 3.0 * p11 + 1.0 / 3.0 * p21;
    vec4 b12 = 1.0 / 3.0 * p11 + 2.0 / 3.0 * p21;
    vec4 b13 = 1.0 / 6.0 * p11 + 2.0 / 3.0 * p21 + 1.0 / 6.0 * p31;

    vec4 b20 = 1.0 / 6.0 * p02 + 2.0 / 3.0 * p12 + 1.0 / 6.0 * p22;
    vec4 b21 = 2.0 / 3.0 * p12 + 1.0 / 3.0 * p22;
    vec4 b22 = 1.0 / 3.0 * p12 + 2.0 / 3.0 * p22;
    vec4 b23 = 1.0 / 6.0 * p12 + 2.0 / 3.0 * p22 + 1.0 / 6.0 * p32;

    vec4 b30 = 1.0 / 6.0 * p03 + 2.0 / 3.0 * p13 + 1.0 / 6.0 * p23;
    vec4 b31 = 2.0 / 3.0 * p13 + 1.0 / 3.0 * p23;
    vec4 b32 = 1.0 / 3.0 * p13 + 2.0 / 3.0 * p23;
    vec4 b33 = 1.0 / 6.0 * p13 + 2.0 / 3.0 * p23 + 1.0 / 6.0 * p33;

    float omu = 1.0 - u;
    float omu2 = omu * omu;
    float omu3 = omu2 * omu;
    float u2 = u * u;
    float u3 = u * u2;

    vec4 d0 = omu3 * b00 + 3.0 * u * omu2 * b01 + 3.0 * u2 * omu * b02 + u3 * b03;
    vec4 d1 = omu3 * b10 + 3.0 * u * omu2 * b11 + 3.0 * u2 * omu * b12 + u3 * b13;
    vec4 d2 = omu3 * b20 + 3.0 * u * omu2 * b21 + 3.0 * u2 * omu * b22 + u3 * b23;
    vec4 d3 = omu3 * b30 + 3.0 * u * omu2 * b31 + 3.0 * u2 * omu * b32 + u3 * b33;

    vec4 b0 = 1.0 / 6.0 * d0 + 2.0 / 3.0 * d1 + 1.0 / 6.0 * d2;
    vec4 b1 = 2.0 / 3.0 * d1 + 1.0 / 3.0 * d2;
    vec4 b2 = 1.0 / 3.0 * d1 + 2.0 / 3.0 * d2;
    vec4 b3 = 1.0 / 6.0 * d1 + 2.0 / 3.0 * d2 + 1.0 / 6.0 * d3;

    float omv = 1.0 - v;
    float omv2 = omv * omv;
    float omv3 = omv * omv2;
    float v2 = v * v;
    float v3 = v * v2;

    te_out.uv = vec2(gl_TessCoord.x, gl_TessCoord.y);
    te_out.id = gl_PrimitiveID;
    gl_Position = omv3 * b0 + 3.0 * v * omv2 * b1 + 3.0 * v2 * omv * b2 + v3 * b3;
}
