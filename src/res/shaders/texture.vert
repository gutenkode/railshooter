// texture vertex shader
#version 330 core

layout(location = 0) in vec4 VertexIn;
layout(location = 2) in vec2 TexIn;

out vec2 texCoord;
out float fragDepth;

uniform mat4 projectionMatrix = mat4(1.0);
uniform mat4 viewMatrix  = mat4(1.0);
uniform mat4 modelMatrix  = mat4(1.0);

void main()
{
	gl_Position = projectionMatrix * viewMatrix * modelMatrix * VertexIn;
	texCoord = TexIn;
	fragDepth = gl_Position.z;
}