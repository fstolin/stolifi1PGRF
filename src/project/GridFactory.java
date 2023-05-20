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

    static OGLBuffers generateStripeGrid(int m, int n) {
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
        int[] indexBuffer = new int[(2 * m * (n - 1)) + ((m - 2) * 2)];

        for (int row = 0; row < m - 1; row++){
            for (int col = 0; col < n ; col++) {
                if (row % 2 == 0) {
                    // Even row
                    indexBuffer[index2++] = row * n + col;
                    indexBuffer[index2++] = (row + 1) * n + col;
                } else {
                    // Odd row
                    indexBuffer[index2++] = (row + 1) * n + (m - 1) - col;
                    indexBuffer[index2++] = row * n + (m - 1) - col;
                }
            }

            // Degenerate triangle
            if (row < m - 2) {
                if (row % 2 == 0) {
                    indexBuffer[index2++] = (row + 1) * n + (n - 1);
                    indexBuffer[index2++] = (row + 1) * n + (n - 1);
                } else {
                    indexBuffer[index2++] = (row + 1) * n;
                    indexBuffer[index2++] = (row + 1) * n;
                }
            }
        }

        // [0, 4, 1, 5, 2, 6, 3, 7, 11, 7, 10, 6, 9, 5, 8, 4, 8, 12, 9, 13, 10, 14, 11, 15, 0]
        // [0, 4, 1, 5, 2, 6, 3, 7, 7, 7, 11, 7, 10, 6, 9, 5, 8, 4, 8, 8, 12, 9, ...
        // [0, 4, 1, 5, 2, 6, 3, 7, 7, 7, 11, 7, 10, 6, 9, 5, 8, 4, 8, 8, 8, 12

        // Attributes
        OGLBuffers.Attrib[] attributes = {
                new OGLBuffers.Attrib("inPosition", 2)
        };

        System.out.println(Arrays.toString(indexBuffer));
        return null;
       // return new OGLBuffers(vertexBuffer, attributes, indexBuffer);
    }

    public static void main(String[] args) {
        generateStripeGrid(4,4);
    }
}
