uniform sampler2D u_texture;

uniform vec2 u_campos;
uniform vec2 u_resolution;

varying vec2 v_pos;
varying vec2 v_texCoords;

void main(){
    vec2 c = v_texCoords.xy;
    //vec2 p = v_pos * u_resolution + u_campos;
    //vec2 p = v_pos - u_campos;
    vec2 p = vec2 (0, 0) - u_campos;
    float dst = distance(c, p);
    float dir = atan((c.y - p.y)/(c.x - p.x));
    vec2 coords = vec2((p.x + cos(dir) * (dst - 1/dst * 0.03)) * 1 + 0, (p.y + sin(dir) * (dst - 1/dst * 0.03)) * 1 + 0);
    //vec2 coords = vec2(p.x + cos(dir) * (dst - 1/dst), p.y + sin(dir) * (dst - 1/dst));
    vec4 color = texture2D(u_texture, coords);
    gl_FragColor = color;
    if(dst < 0.1) gl_FragColor = vec4(255,255,255,255);
}
