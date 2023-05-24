package project;

import lwjglutils.OGLBuffers;
import transforms.Vec3D;

import static org.lwjgl.opengl.GL20.*;

public class PointLight extends Light{

    private Vec3D position;
    private float constant, linear, exponent;
    private int positionLocation, constantLocation, linearLocation, exponentLocation;
    private OGLBuffers oglBuffers;

    PointLight(float red, float green, float blue,
               float aIntensity, float dIntensity, int shaderProgramLoc,
               float xPos, float yPos, float zPos,
               float con, float lin, float exp) {
        super(red, green, blue, aIntensity, dIntensity, shaderProgramLoc);

        position = new Vec3D(xPos, yPos, zPos);
        constant = con;
        linear = lin;
        exponent = exp;

        positionLocation = glGetUniformLocation(shaderProgram, "pointLight.position");
        constantLocation = glGetUniformLocation(shaderProgram, "pointLight.constant");
        linearLocation = glGetUniformLocation(shaderProgram, "pointLight.linear");
        exponentLocation = glGetUniformLocation(shaderProgram, "pointLight.exponent");

        colorLocation = glGetUniformLocation(shaderProgram, "pointLight.base.color");
        ambientIntensityLocation = glGetUniformLocation(shaderProgram, "pointLight.base.ambientIntensity");
        diffuseIntensityLocation = glGetUniformLocation(shaderProgram, "pointLight.base.diffuseIntensity");
        // prepare draw shader for light highlighting
        prepareDrawShader();
    }

    public void useLight(){
        glUniform3f(colorLocation, (float) color.getX(), (float) color.getY(), (float) color.getZ());
        glUniform1f(ambientIntensityLocation, ambientIntensity);
        glUniform1f(diffuseIntensityLocation, diffuseIntensity);

        glUniform3f(positionLocation, (float) position.getX(), (float) position.getY(), (float) position.getZ());
        glUniform1f(constantLocation, constant);
        glUniform1f(linearLocation, linear);
        glUniform1f(exponentLocation, exponent);
    }

    public void draw(){
        glUseProgram(drawProgram);
        oglBuffers.draw(GL_TRIANGLES, drawProgram);
    }

    private void prepareDrawShader(){
        oglBuffers = GridFactory.generateGrid(20,20);
    }

}
