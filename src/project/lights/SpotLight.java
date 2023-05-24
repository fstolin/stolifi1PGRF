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
    private Mat4RotXYZ rotatMatrix;

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

        if (!enabled) {
            color = new Vec3D(0f, 0f, 0f);
        } else {
            color = defaultColor;
        }

        glUniform3f(colorLocation, (float) color.getX(), (float) color.getY(), (float) color.getZ());
        glUniform1f(ambientIntensityLocation, ambientIntensity);
        glUniform1f(diffuseIntensityLocation, diffuseIntensity);


        glUniform1f(edgeLocation, procEdge);

        // If attached to camera -> set to Cameras position
        if (isAttachedToCamera) {
            glUniform3fv(positionLocation, ToFloatArray.convert(camera.getEye()));
            glUniform3fv(directionLocation, ToFloatArray.convert(camera.getViewVector().opposite()));
        } else {
            glUniform3f(directionLocation, (float) direction.getX(), (float) direction.getY(), (float) direction.getZ());
            glUniform3f(positionLocation, (float) position.getX(), (float) position.getY(), (float) position.getZ());
        }

        glUniform1f(constantLocation, constant);
        glUniform1f(linearLocation, linear);
        glUniform1f(exponentLocation, exponent);
    }

    private void transform() {
        Mat4 model = new Mat4Identity();

        translMatrix = new Mat4Transl(position);
        rotatMatrix = new Mat4RotXYZ(direction.getX(), direction.getY(), direction.getZ());

        // translate
        model = model.mul(translMatrix);
        // rotate
        model = model.mul(rotatMatrix);

        glUniformMatrix4fv(positionLocation, false, model.floatArray());
    }

    public void translate(double x, double y, double z) {
        position = position.add(new Vec3D(x, y, z));
        if (lightMesh != null) lightMesh.translate(x, y, z);
    }

    public void rotateX(double x) {
        direction = direction.add(new Vec3D(x, 0.f, 0.0f));
    }

    public void rotateY(double y) {
        direction = direction.add(new Vec3D(0.0f, y, 0.0f));
    }

    public void rotateZ(double z) {
        direction = direction.add(new Vec3D(0.0f, 0.0f, z));
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
        if (lightMesh != null) {
            lightMesh.resetTransforms();
        }
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
