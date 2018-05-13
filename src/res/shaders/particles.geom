#version 330 core

layout(points) in;
layout(triangle_strip, max_vertices=6) out;

in VertexData {
    float fragDepth;
} VertexIn[1];

out VertexData {
    float fragDepth;
} VertexOut;

const float SIZE = .03;

void main()
{
    VertexOut.fragDepth = VertexIn[0].fragDepth;
    vec4 center = gl_in[0].gl_Position;

    gl_Position = center + vec4(SIZE,0,0,0);
    EmitVertex();
    gl_Position = center + vec4(0,-SIZE,0,0);
    EmitVertex();
    gl_Position = center + vec4(-SIZE,0,0,0);
    EmitVertex();
    EndPrimitive();

    gl_Position = center + vec4(SIZE,0,0,0);
    EmitVertex();
    gl_Position = center + vec4(0,SIZE,0,0);
    EmitVertex();
    gl_Position = center + vec4(-SIZE,0,0,0);
    EmitVertex();
    EndPrimitive();

}
