package project;

import static org.lwjgl.glfw.GLFW.glfwGetTime;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glUniform1f;

public class Object1 extends Mesh {

    private int waveFloatLocation;

    Object1(int theShaderProgram) {
        super(theShaderProgram);

        waveFloatLocation = glGetUniformLocation(theShaderProgram, "waveFloat");
        oglBuffers = GridFactory.generateStripeGrid(120,60);
    }

    @Override
    public void draw() {
        glUniform1f(waveFloatLocation, handleWave());
        super.draw();
    }

    private float handleWave(){
        return (float) (15 * Math.sin(glfwGetTime())) + 15;
    }
}
