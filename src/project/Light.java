package project;

import transforms.Vec3D;

import static org.lwjgl.opengl.GL20.*;

public class Light {

    protected int shaderProgram;
    protected Vec3D color;
    protected Vec3D direction;
    protected float ambientIntensity;
    protected float diffuseIntensity;
    protected int colorLocation, directionLocation, ambientIntensityLocation, diffuseIntensityLocation;

    Light(float red, float green, float blue, float aIntensity,
          float xDir, float yDir, float zDir, float dIntensity,
          int shaderProgramLoc
          ){
            color = new Vec3D(red, green, blue);
            direction = new Vec3D(xDir, yDir, zDir);
            ambientIntensity = aIntensity;
            diffuseIntensity = dIntensity;
            shaderProgram = shaderProgramLoc;

            colorLocation = glGetUniformLocation(shaderProgram, "directionalLight.color");
            directionLocation = glGetUniformLocation(shaderProgram, "directionalLight.direction");
            ambientIntensityLocation = glGetUniformLocation(shaderProgram, "directionalLight.ambientIntensity");
            diffuseIntensityLocation = glGetUniformLocation(shaderProgram, "directionalLight.diffuseIntensity");
    }

    public void useLight(){
        glUniform3f(colorLocation, (float) color.getX(), (float) color.getY(), (float) color.getZ());
        glUniform1f(ambientIntensityLocation, ambientIntensity);

        glUniform3f(directionLocation, (float) direction.getX(), (float) direction.getY(), (float) direction.getZ());
        glUniform1f(diffuseIntensityLocation, diffuseIntensity);
    }


}
