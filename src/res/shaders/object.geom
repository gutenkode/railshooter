#version 330 core

layout(triangles) in;
layout(triangle_strip, max_vertices=3) out;

in VertexData {
    noperspective vec2 texCoord;
    vec3 normal;
    float fragDepth;
} VertexIn[3];

out VertexData {
    noperspective vec2 texCoord;
    vec3 normal;
    float fragDepth;
} VertexOut;

uniform float geomDistortCoef = 0.0;

void main()
{
    for(int i = 0; i < gl_in.length(); i++)
    {
        vec3 distort = VertexIn[0].normal * vec3(geomDistortCoef,geomDistortCoef,0);

        // copy attributes
        gl_Position = gl_in[i].gl_Position + vec4(distort,0);
        VertexOut.normal = VertexIn[i].normal;
        VertexOut.texCoord = VertexIn[i].texCoord;
        VertexOut.fragDepth = VertexIn[i].fragDepth;
        // done with the vertex
        EmitVertex();
    }
    EndPrimitive();
}
