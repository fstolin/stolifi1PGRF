package project;

import lwjglutils.OGLBuffers;
import transforms.*;

import static org.lwjgl.opengl.GL11.GL_TRIANGLE_STRIP;
import static org.lwjgl.opengl.GL20.*;

public class Mesh {

    protected int shaderProgram;
    protected int modelLocation;
    protected Mat4Scale scaleMatrix;
    protected Mat4Transl translMatrix;
    protected Mat4Rot rotMatrix;
    protected OGLBuffers oglBuffers;
    protected boolean enabled;

    Mesh(int theShaderProgram, double xLoc, double yLoc, double zLoc) {
        shaderProgram = theShaderProgram;
        modelLocation = glGetUniformLocation(shaderProgram, "model");

        scaleMatrix = new Mat4Scale(1.0, 1.0, 1.0);
        translMatrix = new Mat4Transl(xLoc, yLoc, zLoc);
        rotMatrix = new Mat4Rot(0.0,0.0,0.0,0.0);
        enabled = true;
    }

    public void draw(){
        if (!enabled) return;

        glUseProgram(shaderProgram);
        this.transform();
        oglBuffers.draw(GL_TRIANGLE_STRIP, shaderProgram);
    }

    // Apply all transformations
    private void transform() {
        Mat4 model = new Mat4Identity();
        // translate
        model = model.mul(translMatrix);
        // rotate
        model = model.mul(rotMatrix);
        // scale
        model = model.mul(scaleMatrix);
        glUniformMatrix4fv(modelLocation, false, model.floatArray());
    }

    // Translate the mesh
    public void translate(double x, double y, double z) {
        translMatrix = new Mat4Transl(x, y, z);
    }

    // Scale the mesh
    public void scale(double x, double y, double z) {
        scaleMatrix = new Mat4Scale(x, y , z);
    }

    // Scale the mesh
    public void scale(double xyz) {
        scaleMatrix = new Mat4Scale(xyz);
    }

    public void rotate(double a, double x, double y, double z) {
        rotMatrix = new Mat4Rot(a, x, y, z);
    }

    // Enables drawing
    public void enable(){
        enabled = true;
    }

    // Disables drawing
    public void disable() {
        enabled = false;
    }

    // Toggles the enabled state
    public void toggleEnabled() {
        if (enabled) {
            enabled = false;
        } else {
            enabled = true;
        }
    }

}
