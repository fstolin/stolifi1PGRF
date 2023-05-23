package project;

import static org.lwjgl.glfw.GLFW.glfwGetTime;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glUniform1f;

// Object which changes during time using sin function
public class WaveObject extends Mesh {

    private int waveFloatLocation;

    WaveObject(int theShaderProgram, double xLoc, double yLoc, double zLoc, String name) {
        super(theShaderProgram, xLoc, yLoc, zLoc, name);

        waveFloatLocation = glGetUniformLocation(theShaderProgram, "waveFloat");
        oglBuffers = GridFactory.generateStripeGrid(80,60);
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
