uniform sampler2D u_texture;

uniform float u_time;
uniform vec2 u_center;

uniform vec2 u_campos;
uniform vec2 u_resolution;

varying vec2 v_texCoords;

void main() {
    vec2 worldCoords = v_texCoords * u_resolution + u_campos;
    vec4 col = texture2D(u_texture, v_texCoords);
    float dst = distance(worldCoords, u_center);
    float a = (1. - 999. * pow(dst - u_time, 2.))*(1. - pow(1. - 2. * u_time, 2.));
    //(1 - 0.1 * (x - a)^2)*(1 - (1 - 2 * a)^2)
    //gl_FragColor = vec4(uv.x, uv.y, 0, 1);
    if(worldCoords.x < 0.5) gl_FragColor = vec4(1,1,1,1);
    gl_FragColor = texture2D(u_texture, v_texCoords);
    //gl_FragColor = vec4(1,1,1,1);
}
