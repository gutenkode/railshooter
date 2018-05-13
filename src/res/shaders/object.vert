// shader for 3D game objects, e.g. walls, ships, etc.
// flat shaded and solid color specified by the 'color' uniform
// each polygon is shaded based on its normal for basic color
// simulates low-precision vertices, creating a jittery effect
// the 'geomDistortCoef' uniform is used to "explode" each polygon on death
#version 330

layout(location = 0) in vec4 VertexIn;
layout(location = 2) in vec2 TexIn;
layout(location = 3) in vec3 NormalIn;

out VertexData {
    noperspective vec2 texCoord;
    vec3 normal;
    float fragDepth;
} VertexOut;

out float fragDepth;

uniform mat4 projectionMatrix; 	// defines the visible area on the screen
uniform mat4 viewMatrix;	// represents camera transformations
uniform mat4 modelMatrix;	// represents model transformations

void main()
{
	gl_Position = projectionMatrix * viewMatrix * modelMatrix * VertexIn;
	//gl_Position.xy = floor(gl_Position.xy*35.0)/35.0; // jittering vertices effect

	VertexOut.texCoord = TexIn;
	VertexOut.normal = mat3(viewMatrix * modelMatrix) * normalize(NormalIn);
	VertexOut.fragDepth = gl_Position.z;
}
