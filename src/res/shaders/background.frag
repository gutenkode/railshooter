#version 330 core

in vec2 texCoord;

out vec4 FragColor;

uniform sampler2D texture1;
uniform vec2 screenSize = vec2(128.0);
uniform vec2 imgSize = vec2(256.0);
uniform vec2 pos = vec2(0.5);

void main()
{
	//vec2 upperLeft = vec2(pos.x * (imgSize.x-screenSize.x), pos.y * (imgSize.y-screenSize.y));

	vec2 coord = pos+vec2(.3f,.3f) + texCoord*(screenSize/imgSize);

	FragColor = texture(texture1, vec2(1,-1)*coord);
}
