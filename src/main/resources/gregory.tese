#version 410 core

layout(quads, equal_spacing, ccw) in;

void main() {
    float u = gl_TessCoord.x;
    float v = gl_TessCoord.y;

    vec4 p00 = gl_in[ 0].gl_Position;
    vec4 p01 = gl_in[ 1].gl_Position;
    vec4 p02 = gl_in[ 2].gl_Position;
    vec4 p03 = gl_in[ 3].gl_Position;

    vec4 p10 = gl_in[ 4].gl_Position;
    vec4 p11 = (u != 0 && v != 0) ? ((    u) * gl_in[ 6].gl_Position + (    v) * gl_in[ 5].gl_Position) / (    u + v) : (gl_in[ 5].gl_Position +  gl_in[ 6].gl_Position) / 2;
    vec4 p12 = (u != 1 && v != 0) ? ((1 - u) * gl_in[ 7].gl_Position + (    v) * gl_in[ 8].gl_Position) / (1 - u + v) : (gl_in[ 8].gl_Position +  gl_in[ 7].gl_Position) / 2;
    vec4 p13 = gl_in[ 9].gl_Position;

    vec4 p20 = gl_in[ 10].gl_Position;
    vec4 p21 = (u != 0 && v != 1) ? ((    u) * gl_in[12].gl_Position + (1 - v) * gl_in[11].gl_Position) / (1 + u - v) : (gl_in[11].gl_Position +  gl_in[12].gl_Position) / 2;
    vec4 p22 = (u != 1 && v != 1) ? ((1 - u) * gl_in[13].gl_Position + (1 - v) * gl_in[14].gl_Position) / (2 - u - v) : (gl_in[14].gl_Position +  gl_in[13].gl_Position) / 2;
    vec4 p23 = gl_in[15].gl_Position;

    vec4 p30 = gl_in[16].gl_Position;
    vec4 p31 = gl_in[17].gl_Position;
    vec4 p32 = gl_in[18].gl_Position;
    vec4 p33 = gl_in[19].gl_Position;

    // the basis functions:
    float bu0 = (1.-u) * (1.-u) * (1.-u);
    float bu1 = 3. * u * (1.-u) * (1.-u);
    float bu2 = 3. * u * u * (1.-u);
    float bu3 = u * u * u;
    float dbu0 = -3. * (1.-u) * (1.-u);
    float dbu1 =  3. * (1.-u) * (1.-3.*u);
    float dbu2 =  3. * u *      (2.-3.*u);
    float dbu3 =  3. * u *      u;
    float bv0 = (1.-v) * (1.-v) * (1.-v);
    float bv1 = 3. * v * (1.-v) * (1.-v);
    float bv2 = 3. * v * v * (1.-v);
    float bv3 = v * v * v;
    float dbv0 = -3. * (1.-v) * (1.-v);
    float dbv1 =  3. * (1.-v) * (1.-3.*v);
    float dbv2 =  3. * v *      (2.-3.*v);
    float dbv3 =  3. * v *      v;

    gl_Position =
    ( bu0 * ( bv0*p00 + bv1*p01 + bv2*p02 + bv3*p03 )
    + bu1 * ( bv0*p10 + bv1*p11 + bv2*p12 + bv3*p13 )
    + bu2 * ( bv0*p20 + bv1*p21 + bv2*p22 + bv3*p23 )
    + bu3 * ( bv0*p30 + bv1*p31 + bv2*p32 + bv3*p33 ));
}
