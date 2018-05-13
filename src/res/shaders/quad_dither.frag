// non-transformed quad texture fragment shader
#version 330 core

in vec2 texCoord;

out vec4 FragColor;

uniform sampler2D texture1;
uniform vec2 screenSize = vec2(4.0);

mat4 ditherMat = mat4(1,9,3,11, 13,5,15,7, 4,12,2,10, 16,8,14,6) * (1.0/17.0) * .25;

vec4 multisample() {
	vec2 offset = 0.25/screenSize;
	vec4 value = .2* texture(texture1, texCoord);
	value += .2* texture(texture1, texCoord + offset);
	value += .2* texture(texture1, texCoord - offset);
	value += .2* texture(texture1, texCoord + offset*vec2(1,-1));
	value += .2* texture(texture1, texCoord - offset*vec2(1,-1));
	return value;
}

void main()
{
	FragColor = multisample();//texture(texture1, texCoord);

	int pixX = int(screenSize.x*texCoord.x);
	int pixY = int(screenSize.y*texCoord.y);
	FragColor = FragColor + (FragColor * ditherMat[int(mod(pixX,4))][int(mod(pixY,4))]);
	//float ditherVal = mod(pixX+pixY, 2);
	FragColor = floor(FragColor*12.0)/12.0;
}
