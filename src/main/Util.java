package main;

import mote4.util.vertex.builder.MeshBuilder;
import mote4.util.vertex.mesh.Mesh;

import static org.lwjgl.opengl.GL11.GL_TRIANGLES;

public class Util {

    public static Mesh constructBarrel() {
        MeshBuilder builder = new MeshBuilder(2);
        builder.includeTexCoords(2);
        float stride = .05f;
        float distort = .4f;
        int size = (int)(1/stride)+1;
        float[][][] vertices = new float[size][size][2];

        for (int x = 0; x < size; x++)
            for (int y = 0; y < size; y ++) {
                // starting rectangular coordinates, in range -1 to 1
                float xVal = 2*(x*stride -.5f);
                float yVal = 2*(y*stride -.5f);

                // convert to polar
                double theta  = Math.atan2(yVal, xVal);
                double radius = Math.sqrt(Math.pow(yVal,2) + Math.pow(xVal,2));

                // apply distortion to radius, the inverse tangent graph has a nice falloff to it
                radius = Math.atan(radius*distort);

                radius /= Math.atan(distort); // increase size, to take up the entire screen after distortion

                // convert back to rectangular
                xVal = (float) (radius * Math.cos(theta));
                yVal = (float) (radius * Math.sin(theta));
                vertices[x][y] = new float[] {xVal, yVal};
            }
        for (int x = 0; x < size-1; x ++)
            for (int y = 0; y < size-1; y ++) {
                builder.vertices(
                        vertices[x][y][0], vertices[x][y][1],
                        vertices[x][y+1][0], vertices[x][y+1][1],
                        vertices[x+1][y+1][0], vertices[x+1][y+1][1],

                        vertices[x][y][0], vertices[x][y][1],
                        vertices[x+1][y][0], vertices[x+1][y][1],
                        vertices[x+1][y+1][0], vertices[x+1][y+1][1]
                );

                float cX = x*stride;
                float cY = y*stride;
                builder.texCoords(
                        cX,cY,
                        cX,cY+stride,
                        cX+stride,cY+stride,

                        cX,cY,
                        cX+stride,cY,
                        cX+stride,cY+stride
                );
            }
        return builder.constructVAO(GL_TRIANGLES);
    }

    public static double[] rectToSphere(double x, double y, double z) {
        double radius = Math.sqrt(Math.pow(x,2) + Math.pow(y,2) + Math.pow(z,2));
        double theta  = Math.atan2(y, x);
        double phi = Math.atan2(Math.sqrt(Math.pow(x,2) + Math.pow(y,2)), z);
        return new double[] {radius, theta, phi};
    }
    public static double[] sphereToRect(double radius, double theta, double phi) {
        double x = radius * Math.sin(phi) * Math.cos(theta);
        double y = radius * Math.sin(phi) * Math.sin(theta);
        double z = radius * Math.cos(phi);
        return new double[] {x, y, z};
    }
}
