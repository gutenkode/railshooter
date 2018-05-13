// flat fragment shader
#version 330 core

in VertexData {
    float fragDepth;
} VertexIn;

out vec4 FragColor;

uniform vec3 color = vec3(1);

void main()
{
	FragColor = vec4(color, VertexIn.fragDepth);
}
