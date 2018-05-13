// non-transformed quad texture fragment shader
#version 330 core

#define PI 3.1415926535897932384626433832795

in vec2 texCoord;

out vec4 FragColor;

uniform sampler2D texture1;
uniform sampler2D tex_gradient;
uniform sampler2D tex_color;
uniform sampler2D tex_lensflare;
uniform vec4 colorMult = vec4(1.0), colorAdd = vec4(0.0);
uniform float time, xScale = 1;

void main()
{
	float centerRadius = length(vec2(texCoord.xy*vec2(xScale,1)-vec2(xScale/2,.5)));
	float angle = atan(texCoord.y-.5, texCoord.x*xScale-xScale/2);


	FragColor = texture(texture1, vec2(angle/PI, centerRadius*.3-time));
	FragColor += texture(tex_gradient, vec2(angle/PI, centerRadius*.5-time/8));
	angle = angle/2+time;
	FragColor += texture(tex_color, vec2(angle/PI, centerRadius*.75-time/2));
	FragColor += texture(tex_color, vec2(angle/PI+.5, centerRadius*.75-time/2+.75));

	float lensflareCoef = 1;//mod(time, .7)+.3;
	FragColor += vec4(lensflareCoef) * texture(tex_lensflare, texCoord);

	FragColor *= colorMult;
	FragColor += colorAdd;
	FragColor.a *= (1-centerRadius)+(colorMult.a);
}
