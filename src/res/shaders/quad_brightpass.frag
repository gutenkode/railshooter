// non-transformed quad texture fragment shader
#version 330 core

in vec2 texCoord;

out vec4 FragColor;

uniform sampler2D texture1;

void main()
{
	FragColor = texture(texture1, texCoord);
	FragColor -= vec4(vec3(.55),0);
}
