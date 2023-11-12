uniform sampler2D u_texture;

uniform vec2 u_campos;
uniform vec2 u_resolution;
uniform vec2 v_pos;

varying vec2 v_texCoords;

#define PI 3.1415927

/*float atan2(float x, float y){
    float n = y / x;
    if(n != n){
        //n = (y == x ? 1f : -1f); if both y and x are infinite, n would be NaN
        if(y == x) {
            n = 1;
        }else {
            n = -1;
        }
    }else if(n - n != n - n){
        x = 0; // if n is infinite, y is infinitely larger than x.
    }
    if(x > 0){
        return atan(n);
    }else if(x < 0){
        //return y >= 0 ? atn(n) + PI : atn(n) - PI;
        if(y >= 0){
            return atan(n) + PI;
        }else {
            return atan(n) - PI;
        }
    }else if(y > 0){
        return x + (PI / 2);
    }else if(y < 0){
        return x - (PI / 2);
    }else {
        return x + y; // returns 0 for 0,0 or NaN if either y or x is NaN
    }
}*/
float atan2(in float y, in float x){
    bool s = (abs(x) > abs(y));
    return mix(PI/2.0 - atan(x,y), atan(y,x), s);
}

float cforce(in float a, in float radius, in float force){
    return (abs(a / radius) - 1)*(abs(a / radius) - 1)*force;
}

void main(){
    float radius = 64;
    float force = 64;
    vec2 c = v_texCoords;
    //vec2 p = v_pos * u_resolution + u_campos;
    //vec2 p = v_pos - u_campos;
    //vec2 p = vec2 (0, 0) - u_campos;
    //float dst = distance(c / u_resolution * u_resolution.x, p);
    //vec2 p = v_pos;
    vec2 p = (v_pos - u_campos)/u_resolution;
    vec2 s = vec2(u_resolution.x / u_resolution.y, 1);
    //vec2 sc = (c - p) * s + p;
    vec2 sc = c;

    float dst = sqrt((sc.x - p.x) * (sc.x - p.x) + (sc.y - p.y) * (sc.y - p.y));
    float dstx = dst / length(u_resolution);
    if(dst >= radius / length(u_resolution)){
        vec4 color = texture2D(u_texture, c);
        gl_FragColor = color;
    }else if(dst >= 0){
        //float dstc = abs(force/dst - 1) * force / length(u_resolution) / length(u_resolution);
        //float dstc = 0.1;
        float dstc = cforce(dst, radius / length(u_resolution), force)/length(u_resolution);
        float dir = atan2((sc.y - p.y), (sc.x - p.x));
        vec2 coords = vec2((p.x + cos(dir) * (dst - dstc)), (p.y + sin(dir) * (dst - dstc)));
        vec4 color = texture2D(u_texture, coords);
        gl_FragColor = color;
    }else {
        gl_FragColor = vec4(255, 255, 255, 255);
    }
    //float dir = atan((c.y - p.y)/(c.x - p.x));

    //vec2 coords = vec2(p.x + cos(dir) * (dst - 1/dst), p.y + sin(dir) * (dst - 1/dst));


    //if(dst < 0.1) gl_FragColor = vec4(dst,dst,dst,100);
}
