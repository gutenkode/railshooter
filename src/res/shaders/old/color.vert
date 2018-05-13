// flat vertex shader
#version 330

layout(location = 0) in vec4 VertexIn;

uniform mat4 projectionMatrix; 	// defines the visible area on the screen
uniform mat4 viewMatrix;	// represents camera transformations
uniform mat4 modelMatrix;	// represents model transformations

void main()
{
	gl_Position = projectionMatrix * viewMatrix * modelMatrix * VertexIn;
}