// flat vertex shader
#version 330

layout(location = 0) in vec4 VertexIn;

out VertexData {
    float fragDepth;
} VertexOut;

uniform mat4 projectionMatrix; 	// defines the visible area on the screen
uniform mat4 viewMatrix;	// represents camera transformations
uniform mat4 modelMatrix;	// represents model transformations

uniform float timestep;
uniform float distance;
void main()
{
	vec4 vertex = VertexIn;
	vertex.z += distance + timestep * mod(vertex.x+vertex.y, .1)*10.0;
	vertex.z = mod(vertex.z,1);
	VertexOut.fragDepth = vertex.z;
	gl_Position = projectionMatrix * viewMatrix * modelMatrix * vertex;
}
