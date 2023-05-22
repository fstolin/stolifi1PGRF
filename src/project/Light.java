package project;

import transforms.Vec3D;

import static org.lwjgl.opengl.GL20.*;

public class Light {

    protected int shaderProgram;
    protected Vec3D color;
    protected Vec3D direction;
    protected float ambientIntensity;
    protected float diffuseIntensity;
    protected final float intensityModificationSpeed;
    protected int colorLocation, directionLocation, ambientIntensityLocation, diffuseIntensityLocation;

    Light(float red, float green, float blue, float aIntensity,
          float xDir, float yDir, float zDir, float dIntensity,
          int shaderProgramLoc
          ){
            color = new Vec3D(red, green, blue);
            direction = new Vec3D(xDir, yDir, zDir);
            ambientIntensity = aIntensity;
            diffuseIntensity = dIntensity;
            // Speed at which modifications to intensity are set
            intensityModificationSpeed = 0.2f;
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

    public void decreaseAmbientIntensity(float dimSpeed){
        if (ambientIntensity > 0.0f) ambientIntensity -= dimSpeed;
    }

    public void increaseAmbientIntensity(float dimSpeed){
        if (ambientIntensity < 1.0f) ambientIntensity += dimSpeed;
    }

    public void decreaseDiffuseIntensity(float dimSpeed){
        if (diffuseIntensity > 0.0f) diffuseIntensity -= dimSpeed;
    }

    public void increaseDiffuseIntensity(float dimSpeed){
        if (diffuseIntensity < 1.0f) diffuseIntensity += dimSpeed;
    }


}
