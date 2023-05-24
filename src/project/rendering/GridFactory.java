package project.rendering;

import lwjglutils.OGLBuffers;

import java.util.ArrayList;
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

    static OGLBuffers generateStripeGrid(int m, int n) {
        float[] vertexBuffer = new float[m * n * 2];
        // helper variable to index the vertexBuffer
        int index = 0;

        // Fill the vertex Buffer
        for (int row = m; row > 0; row--){
            for (int col = 0; col < n; col++) {
                vertexBuffer[index++] = col / (float) (n - 1);
                vertexBuffer[index++] = (row - 1) / (float) (m - 1);
            }
        }

        // Index Buffer
        int index2 = 0;
        //int[] indexBuffer = new int[(2 * m * (n - 1)) + ((m - 2) * 2)];
        ArrayList<Integer> indexBufferList = new ArrayList<Integer>();

        for (int row = 0; row < m - 1; row++){
            for (int col = 0; col < n ; col++) {
                if (row % 2 == 0) {
                    // Even row
                    indexBufferList.add(row * n + col);
                    indexBufferList.add((row + 1) * n + col);
                } else {
                    // Odd row
                    indexBufferList.add((row + 1) * n + (n - 1) - col);
                    indexBufferList.add(row * n + (n - 1) - col);
                }
            }

            // Degenerate triangle
            if (row < m - 2) {
                if (row % 2 == 0) {
                    indexBufferList.add((row + 1) * n + (n - 1));
                    indexBufferList.add((row + 1) * n + (n - 1));
                } else {
                    indexBufferList.add((row + 1) * n);
                    indexBufferList.add((row + 1) * n);
                }
            }
        }

        // ArrayList workaround
        int indexBuffer[] = new int[indexBufferList.size()];
        for (int i = 0; i < indexBuffer.length; i++) {
            indexBuffer[i] = indexBufferList.get(i);
        }

        // Attributes
        OGLBuffers.Attrib[] attributes = {
                new OGLBuffers.Attrib("inPosition", 2)
        };

        return new OGLBuffers(vertexBuffer,attributes,indexBuffer);
    }
}
