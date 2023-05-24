package project.lights;

import lwjglutils.ToFloatArray;
import project.rendering.Mesh;
import transforms.*;

import java.util.Arrays;

import static org.lwjgl.opengl.GL20.*;

public class SpotLight extends PointLight{

    // Angle of edge + processed edge
    protected float edge, procEdge;
    protected Vec3D direction;
    protected int directionLocation, edgeLocation;

    public SpotLight(float red, float green, float blue,
                     float aIntensity, float dIntensity, int shaderProgramLoc,
                     float xPos, float yPos, float zPos,
                     float xDir, float yDir, float zDir,
                     float con, float lin, float exp,
                     float theEdge){
        super(red, green, blue, aIntensity, dIntensity, shaderProgramLoc, xPos, yPos, zPos, con, lin, exp);

        edge = theEdge;
        procEdge = (float) Math.cos(Math.toRadians(edge));

        positionLocation = glGetUniformLocation(shaderProgram, "spotLight.base.position");
        constantLocation = glGetUniformLocation(shaderProgram, "spotLight.base.constant");
        linearLocation = glGetUniformLocation(shaderProgram, "spotLight.base.linear");
        exponentLocation = glGetUniformLocation(shaderProgram, "spotLight.base.exponent");

        colorLocation = glGetUniformLocation(shaderProgram, "spotLight.base.base.color");
        ambientIntensityLocation = glGetUniformLocation(shaderProgram, "spotLight.base.base.ambientIntensity");
        diffuseIntensityLocation = glGetUniformLocation(shaderProgram, "spotLight.base.base.diffuseIntensity");

        directionLocation = glGetUniformLocation(shaderProgram, "spotLight.direction");
        edgeLocation = glGetUniformLocation(shaderProgram, "spotLight.edge");

        direction = new Vec3D(xDir, yDir, zDir);
    }


    public void useLight(Camera camera){
        procEdge = (float) Math.cos(Math.toRadians(edge));
        System.out.println(procEdge);
        System.out.println(edge);

        if (!enabled) {
            color = new Vec3D(0f, 0f, 0f);
        } else {
            color = defaultColor;
        }

        glUniform3f(colorLocation, (float) color.getX(), (float) color.getY(), (float) color.getZ());
        glUniform1f(ambientIntensityLocation, ambientIntensity);
        glUniform1f(diffuseIntensityLocation, diffuseIntensity);

        glUniform3f(directionLocation, (float) direction.getX(), (float) direction.getY(), (float) direction.getZ());
        glUniform1f(edgeLocation, procEdge);

        // If attached to camera -> set to Cameras position
        if (isAttachedToCamera) {
            glUniform3fv(positionLocation, ToFloatArray.convert(camera.getEye()));
        } else {
            glUniform3f(positionLocation, (float) position.getX(), (float) position.getY(), (float) position.getZ());
        }
        glUniform1f(constantLocation, constant);
        glUniform1f(linearLocation, linear);
        glUniform1f(exponentLocation, exponent);
    }

    private void transform() {
        Mat4 model = new Mat4Identity();

        translMatrix = new Mat4Transl(position);

        // translate
        model = model.mul(translMatrix);
        glUniformMatrix4fv(positionLocation, false, model.floatArray());
    }

    public void translate(double x, double y, double z) {
        position = position.add(new Vec3D(x, y, z));
        if (lightMesh != null) lightMesh.translate(x, y, z);
    }

    public Vec3D getPosition(){
        return position;
    }

    public void setLightMesh(Mesh m){
        lightMesh = m;
    }

    public Mesh getLightMesh(){
        return lightMesh;
    }

    public void setIsAttachedToCamera(boolean b) {
        isAttachedToCamera = b;
    }

    public boolean getIsAttachedToCamera() {
        return isAttachedToCamera;
    }

    public void resetTransforms(){
        position = defaultPosition;
    }

    public void toggleEnabled() {
        if (enabled) {
            enabled = false;
        } else {
            enabled = true;
        }
    }

    public void lowerEdge(){
        edge -= 0.5f;
    }

    public void increaseEdge(){
        edge += 1f;
    }

}
