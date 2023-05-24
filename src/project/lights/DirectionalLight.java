package project.lights;

import transforms.Vec3D;

import static org.lwjgl.opengl.GL20.*;

public class DirectionalLight extends Light {

    protected Vec3D direction;
    protected int directionLocation;

    public DirectionalLight(float red, float green, float blue, float aIntensity,
                            float xDir, float yDir, float zDir, float dIntensity,
                            int shaderProgramLoc){
        super(red, green, blue, aIntensity, dIntensity, shaderProgramLoc);

        direction = new Vec3D(xDir, yDir, zDir);
        directionLocation = glGetUniformLocation(shaderProgram, "directionalLight.direction");

        colorLocation = glGetUniformLocation(shaderProgram, "directionalLight.base.color");
        ambientIntensityLocation = glGetUniformLocation(shaderProgram, "directionalLight.base.ambientIntensity");
        diffuseIntensityLocation = glGetUniformLocation(shaderProgram, "directionalLight.base.diffuseIntensity");
    }

    public void useLight(){
        glUniform3f(colorLocation, (float) color.getX(), (float) color.getY(), (float) color.getZ());
        glUniform1f(ambientIntensityLocation, ambientIntensity);

        glUniform3f(directionLocation, (float) direction.getX(), (float) direction.getY(), (float) direction.getZ());
        glUniform1f(diffuseIntensityLocation, diffuseIntensity);
    }


}
