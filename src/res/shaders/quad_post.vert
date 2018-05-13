// non-transformed quad texture vertex shader
#version 330 core

layout(location = 0) in vec4 VertexIn;
layout(location = 2) in vec2 TexIn;

uniform float aspectRatio = 1.0;

out vec2 texCoord;

void main()
{
	gl_Position = VertexIn*vec4(aspectRatio,1,1,1);
	texCoord = TexIn;
}
