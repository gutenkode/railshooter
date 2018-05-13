#version 330

in VertexData {
    noperspective vec2 texCoord;
    vec3 normal;
    float fragDepth;
} VertexIn;

out vec4 FragColor;

uniform float textureCoef = 0.0;
uniform sampler2D texture1;

uniform vec4 color = vec4(1);

uniform float fogStart = 10.0;
uniform float fogEnd = 120.0;

uniform float fadeStart = 80.0;
uniform float fadeEnd = 110.0;
uniform float alpha = 1.0;

uniform vec3 light = vec3(0,1,0);

void main(void)
{
  	vec3 Idiff = vec3((dot(VertexIn.normal,light)+1)/2);

  	FragColor = vec4(Idiff*2,1) * color;

  	float fadeFactor = (fadeEnd - VertexIn.fragDepth) / (fadeEnd - fadeStart);
    float fogFactor = (fogEnd - VertexIn.fragDepth) / (fogEnd - fogStart);
  	FragColor.a *= fadeFactor;
    FragColor.a *= alpha;
  	FragColor.rgb *= fogFactor;

    if (textureCoef > 0)
	   FragColor *= texture(texture1, VertexIn.texCoord);

  	if (FragColor.a == 0.0)
    	discard;
}
