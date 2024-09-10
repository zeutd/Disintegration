uniform sampler2D u_texture;

uniform float u_time;

varying vec2 v_texCoords;

#define NSCALE 200.0 / 2.0

void main() {
    vec2 coords = v_texCoords.xy;
    vec4 col = texture2D(u_texture, v_texCoords);
    vec2 uv = vec2(atan(v_texCoords.x, v_texCoords.y) * 6.28 + 0.5, length(v_texCoords)) - 0.5;
    vec4 res = col.rgba;
    res *= 5. * (-abs(uv.x - uv.y) + 0.1);
    res += col * 0.5;
    gl_FragColor = col;
}
