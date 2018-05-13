// flat fragment shader
#version 330 core

in vec2 texCoord;

out vec4 FragColor;

uniform vec4 color = vec4(1);

void main() 
{
	FragColor = color;

	if (FragColor.a == 0.0)
    	discard;
}