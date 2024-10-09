#define HIGHP
#define PI 3.1415927
//shades of lithium
#define S3 vec3(85.0, 85.0, 85.0) / 100.0
#define S2 vec3(90, 90, 92) / 100.0
#define S1 vec3(95.0, 95.0, 95.0) / 100.0
#define NSCALE 200.0 / 2.0

uniform sampler2D u_texture;
uniform sampler2D u_noise;
uniform sampler2D u_noise2;
uniform sampler2D u_noise3;

uniform vec2 u_campos;
uniform vec2 u_resolution;
uniform float u_time;

varying vec2 v_texCoords;

void main() {
    vec2 c = v_texCoords.xy;
    vec2 coords = vec2(c.x * u_resolution.x + u_campos.x, c.y * u_resolution.y + u_campos.y);

    float btime = u_time / 5000.0;
    float noise = (texture2D(u_noise, (coords) / NSCALE + vec2(btime) * vec2(-0.9, 0.8)).r + texture2D(u_noise, (coords) / NSCALE + vec2(btime * 1.1) * vec2(0.8, -1.0)).r) / 2.0;
    float noise2 = (texture2D(u_noise2, (coords) / NSCALE + vec2(btime / 1.5) * vec2(-0.9, 0.8)).r + texture2D(u_noise2, (coords) / NSCALE + vec2(btime * 1.1 / 1.5) * vec2(0.8, -1.0)).r) / 2.0;
    float noise3 = (texture2D(u_noise3, (coords) / NSCALE + vec2(btime * 2.) * vec2(-0.9, 0.8)).r + texture2D(u_noise3, (coords) / NSCALE + vec2(btime * 1.1 * 2.) * vec2(0.8, -1.0)).r) / 2.0;

    vec4 color = texture2D(u_texture, c);
    if (noise < 0.6 && noise > 0.5){
        color.rgb = S1;
    }
    else if(noise3 < 0.5 && noise3 > 0.3){
        color.rgb = S2;
    }
    else if(noise2 < 0.5 && noise2 > 0.34){
        color.rgb = S3;
    }

    gl_FragColor = color;
}
