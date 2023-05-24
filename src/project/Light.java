package project;

import lwjglutils.ShaderUtils;
import transforms.Vec3D;

import static org.lwjgl.opengl.GL20.*;

public class Light {

    protected int shaderProgram;
    protected int drawProgram;
    protected Vec3D color;

    protected float ambientIntensity;
    protected float diffuseIntensity;
    protected final float intensityModificationSpeed;
    protected int colorLocation, ambientIntensityLocation, diffuseIntensityLocation;

    Light(float red, float green, float blue, float aIntensity, float dIntensity, int shaderProgramLoc, int drawProgramLoc){
            color = new Vec3D(red, green, blue);
            ambientIntensity = aIntensity;
            diffuseIntensity = dIntensity;
            // Speed at which modifications to intensity are set
            intensityModificationSpeed = 0.2f;
            shaderProgram = shaderProgramLoc;
            // Draw shader
            drawProgram = drawProgramLoc;
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
