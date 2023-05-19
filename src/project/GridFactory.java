package project;

import lwjglutils.OGLBuffers;

import java.util.Arrays;

public class GridFactory {

    /**
     *
     * @param m - number of row vertices
     * @param n - number of column vertices
     * @return
     */
    static OGLBuffers generateGrid(int m, int n) {
        float[] vertexBuffer = new float[m * n * 2];
        // helper variable to index the vertexBuffer
        int index = 0;

        // Fill the vertex Buffer
        for (int i = 0; i < n; i++){
            for (int j = 0; j < m; j++) {
                vertexBuffer[index++] = (j / (float) (m - 1));
                vertexBuffer[index++] = (i / (float) (n - 1));
            }
        }

        // Index Buffer
        int index2 = 0;
        int[] indexBuffer = new int[2 * 3 * (m - 1) * (n - 1)];
        for (int i = 0; i < n - 1; i++){
            int rowOffset = i * m;
            for (int j = 0; j < m - 1; j++) {
                indexBuffer[index2++] = (j + rowOffset);
                indexBuffer[index2++] = (j + m + rowOffset);
                indexBuffer[index2++] = (j + 1 + rowOffset);

                indexBuffer[index2++] = (j + 1 + rowOffset);
                indexBuffer[index2++] = (j + m + rowOffset);
                indexBuffer[index2++] = (j + m + 1 + rowOffset);

            }
        }

        // Attributes
        OGLBuffers.Attrib[] attributes = {
            new OGLBuffers.Attrib("inPosition", 2)
        };

        // Return
        return new OGLBuffers(vertexBuffer, attributes, indexBuffer);
    }
}
