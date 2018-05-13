// flat vertex shader
#version 330

layout(location = 0) in vec4 VertexIn;

out float z;

uniform float timestep;
uniform mat4 projectionMatrix; 	// defines the visible area on the screen
uniform mat4 viewMatrix;	// represents camera transformations
uniform mat4 modelMatrix;	// represents model transformations

void main()
{
	vec4 vertex = VertexIn;
	vertex.z += timestep* mod(vertex.x+vertex.y, .1)*10.0;
	vertex.z = mod(vertex.z,1);
	z = vertex.z;
	gl_Position = projectionMatrix * viewMatrix * modelMatrix * vertex;
}