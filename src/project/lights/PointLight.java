package project.lights;

import lwjglutils.OGLBuffers;
import lwjglutils.ToFloatArray;
import project.rendering.Mesh;
import transforms.*;

import static org.lwjgl.opengl.GL20.*;

public class PointLight extends Light{

    protected Vec3D position;
    protected Vec3D defaultPosition;
    protected Vec3D defaultColor;
    protected boolean enabled;
    protected float constant, linear, exponent;
    protected int positionLocation, constantLocation, linearLocation, exponentLocation;
    protected OGLBuffers oglBuffers;
    protected Mat4Transl translMatrix;
    protected boolean isAttachedToCamera;
    protected Mesh lightMesh;

    public PointLight(float red, float green, float blue,
                      float aIntensity, float dIntensity, int shaderProgramLoc,
                      float xPos, float yPos, float zPos,
                      float con, float lin, float exp) {
        super(red, green, blue, aIntensity, dIntensity, shaderProgramLoc);

        position = new Vec3D(xPos, yPos, zPos);
        defaultPosition = position;
        defaultColor = new Vec3D(red, green, blue);
        constant = con;
        linear = lin;
        exponent = exp;

        isAttachedToCamera = false;
        enabled = true;

        positionLocation = glGetUniformLocation(shaderProgram, "pointLight.position");
        constantLocation = glGetUniformLocation(shaderProgram, "pointLight.constant");
        linearLocation = glGetUniformLocation(shaderProgram, "pointLight.linear");
        exponentLocation = glGetUniformLocation(shaderProgram, "pointLight.exponent");

        colorLocation = glGetUniformLocation(shaderProgram, "pointLight.base.color");
        ambientIntensityLocation = glGetUniformLocation(shaderProgram, "pointLight.base.ambientIntensity");
        diffuseIntensityLocation = glGetUniformLocation(shaderProgram, "pointLight.base.diffuseIntensity");
    }

    public void useLight(Camera camera){
        if (!enabled) {
            color = new Vec3D(0f, 0f, 0f);
            System.out.println("disabled");
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
