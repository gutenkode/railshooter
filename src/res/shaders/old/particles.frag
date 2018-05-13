// flat fragment shader
#version 330 core

in float z;

out vec4 FragColor;

void main() 
{
	FragColor = vec4(vec3(1),z);
}