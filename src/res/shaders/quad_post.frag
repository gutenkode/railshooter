// final mix fragment shader
#version 330 core

#define CRT
//#define QUILEZ

#ifdef CRT
	#define TEX_FUNC crtTexture
#else
	#ifdef QUILEZ
		#define TEX_FUNC quilezTexture
	#else
		#define TEX_FUNC texture
	#endif
#endif

in vec2 texCoord;

out vec4 FragColor;

uniform sampler2D tex_scene;
uniform sampler2D tex_blur;
uniform sampler2D tex_scanlines;
uniform sampler2D tex_noise;
uniform sampler2D tex_vignette;

uniform float distortCoef = 0.0;
uniform vec2 noiseOffset = vec2(0.0);
uniform vec2 texSize = vec2(256.0);
uniform vec3 colorMult = vec3(1.0);

vec4 quilezTexture(sampler2D tex, vec2 p)
{
	// pulled from:
	// iquilezles.org/www/articles/texture/texture.htm
    p = p*texSize + 0.5;

    vec2 i = floor(p);
    vec2 f = p - i;
	//f = smoothstep(0,1,f);
    f = f*f*f*(f*(f*6.0-15.0)+10.0); // smoothstep
    p = i + f;

    p = (p - 0.5)/texSize;
    return texture(tex, p);
}

vec4 crtTexture(sampler2D tex, vec2 p)
{
	vec4 result = quilezTexture(tex, p) * .45;
	result += quilezTexture(tex, p+vec2(0.5/texSize.x,0)) * .25;
	result += quilezTexture(tex, p-vec2(0.5/texSize.x,0)) * .25;
	result += quilezTexture(tex, p+vec2(1.5/texSize.x,0)) * .1;
	result += quilezTexture(tex, p-vec2(1.5/texSize.x,0)) * .1;
	//result += quilezTexture(tex, p+vec2(2.5/texSize.x,0)) * .10;
	//result += quilezTexture(tex, p-vec2(2.5/texSize.x,0)) * .10;

	float blue = quilezTexture(tex, p-vec2(2/texSize.x,0)).b * .5;
	float red = quilezTexture(tex, p+vec2(1/texSize.x,0)).r * .25;
	result.b = max(result.b, blue);
	result.r = max(result.r, red);
	return result;
}

mat4 ditherMat = mat4(1,9,3,11, 13,5,15,7, 4,12,2,10, 16,8,14,6) * (1.0/17.0) * .25;
vec3 dither(vec3 pixel)
{
	int pixX = int(texSize.x*texCoord.x);
	int pixY = int(texSize.y*texCoord.y);
	pixel += (pixel * ditherMat[int(mod(pixX,4))][int(mod(pixY,4))]);
	pixel = floor(pixel*6.0)/6.0;
	return pixel;
}

void main()
{
	float yDistortCoef = (1+distortCoef*3) * pow(1-texCoord.y,14.0);

	float randOffset = yDistortCoef*0.05 + yDistortCoef*noiseOffset.y*0.007;
	FragColor = TEX_FUNC(tex_scene, texCoord +vec2(randOffset,0));
	//FragColor.rgb = dither(FragColor.rgb);

	#ifdef CRT
		FragColor += (distortCoef*2+yDistortCoef*5+.5) * texture(tex_noise, texCoord*3+noiseOffset); // noise
		vec2 scanlineCoord = texCoord * vec2(1,texSize.y); // scanlines
		FragColor *= texture(tex_scanlines, scanlineCoord);

		FragColor += texture(tex_blur, texCoord +vec2(randOffset,0));
		FragColor *= texture(tex_vignette, texCoord);
	#endif

	FragColor.rgb *= colorMult; // final global multiply, used for fading in/out
}
