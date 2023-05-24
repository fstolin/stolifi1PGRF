package project.lights;

import lwjglutils.ToFloatArray;
import project.rendering.Mesh;
import transforms.*;

import static org.lwjgl.opengl.GL20.*;

public class SpotLight extends PointLight{

    // Angle of edge + processed edge
    private float edge, procEdge;

    SpotLight(float red, float green, float blue,
              float aIntensity, float dIntensity, int shaderProgramLoc,
              float xPos, float yPos, float zPos,
              float con, float lin, float exp,
              float theProcEdge){
        super(red, green, blue, aIntensity, dIntensity, shaderProgramLoc, xPos, yPos, zPos, con, lin, exp);

        procEdge = theProcEdge;
    }


    public void useLight(Camera camera){
        if (!enabled) {
            color = new Vec3D(0f, 0f, 0f);
        } else {
            color = defaultColor;
        }

        glUniform3f(colorLocation, (float) color.getX(), (float) color.getY(), (float) color.getZ());
        glUniform1f(ambientIntensityLocation, ambientIntensity);
        glUniform1f(diffuseIntensityLocation, diffuseIntensity);

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

}
