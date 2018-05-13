// 5x5 gaussian kernel in one dimension
#version 330 core

in vec2 texCoord;

out vec4 FragColor;

uniform sampler2D texture1;
uniform vec2 screenSize = vec2(512);
uniform vec2 step = vec2(1,0);

void main()
{
	//5x1 gaussian kernel is:  1,4,6,4,1  total is 16
	vec2 pixelStep = vec2(1.0/screenSize);

	FragColor =  texture(texture1, texCoord                        ) *(6/16.0);
	FragColor += texture(texture1, texCoord +pixelStep*step        ) *(4/16.0);
	FragColor += texture(texture1, texCoord +pixelStep*step*vec2(2)) *(1/16.0);
	FragColor += texture(texture1, texCoord +pixelStep*step*vec2(-1)) *(4/16.0);
	FragColor += texture(texture1, texCoord +pixelStep*step*vec2(-2)) *(1/16.0);
}
