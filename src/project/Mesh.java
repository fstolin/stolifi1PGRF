package project;

import lwjglutils.OGLBuffers;

import static org.lwjgl.opengl.GL11.GL_TRIANGLE_STRIP;
import static org.lwjgl.opengl.GL20.glUseProgram;

public class Mesh {

    protected int shaderProgram;
    protected OGLBuffers oglBuffers;

    Mesh(int theShaderProgram) {
        shaderProgram = theShaderProgram;
    }

    public void draw(){
        glUseProgram(shaderProgram);
        oglBuffers.draw(GL_TRIANGLE_STRIP, shaderProgram);
    }

    public void delete() {

    }

}
