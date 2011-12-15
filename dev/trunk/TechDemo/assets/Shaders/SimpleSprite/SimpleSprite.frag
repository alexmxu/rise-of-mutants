varying vec2 texCoordAni;
uniform sampler2D m_AniTexMap;

#ifdef MULTIPLY_COLOR
uniform vec4 m_Multiply_Color;
#endif

#ifdef FOG
    varying float fog_z;
    uniform vec4 m_FogColor;
    vec4 fogColor;
    float fogFactor;
#endif

#ifdef FOG_SKY
#import "Common/ShaderLib/Optics.glsllib"
    uniform ENVMAP m_FogSkyBox;
    varying vec3 I;
#endif

void main(){

vec4 AniTex = texture2D(m_AniTexMap, vec2(texCoordAni));

#ifdef MULTIPLY_COLOR
        gl_FragColor.rgb = m_Multiply_Color.rgb * AniTex.rgb;
#else
        gl_FragColor.rgb = AniTex.rgb;
#endif


#ifdef FOG
fogColor = m_FogColor;

    #ifdef FOG_SKY
fogColor.rgb = Optics_GetEnvColor(m_FogSkyBox, I).rgb;
    #endif

float fogDensity = 1.2;
float fogDistance = fogColor.a;
float depth = fog_z / fogDistance;
float LOG2 = 1.442695;
 
fogFactor = exp2( -fogDensity * fogDensity * depth *  depth * LOG2 );
fogFactor = clamp(fogFactor, 0.0, 1.0);

gl_FragColor.rgb = mix(fogColor.rgb,gl_FragColor.rgb,vec3(fogFactor));

#endif


        gl_FragColor.a = AniTex.a;

}