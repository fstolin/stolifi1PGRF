package project;

import lwjglutils.OGLBuffers;
import transforms.*;

import static org.lwjgl.opengl.GL11.GL_TRIANGLE_STRIP;
import static org.lwjgl.opengl.GL20.*;

public class Mesh {

    protected int shaderProgram;
    protected String name;
    protected int modelLocation;
    protected Mat4Scale scaleMatrix;
    protected Mat4Transl translMatrix;
    protected Mat4RotZ rotMatrix;
    protected OGLBuffers oglBuffers;
    protected boolean enabled;
    protected Vec3D position;
    protected Vec3D scale;
    protected double rotation;

    protected Vec3D defaultPos;
    protected Vec3D defaultScale;
    protected double defaultRot;
    protected int triangleMode;
    protected Material material;


    Mesh(int theShaderProgram, double xLoc, double yLoc, double zLoc, String name) {
        shaderProgram = theShaderProgram;
        modelLocation = glGetUniformLocation(shaderProgram, "model");

        scale = new Vec3D(1.0,1.0,1.0);
        position = new Vec3D(xLoc, yLoc, zLoc);
        rotation = 0.0;
        enabled = true;

        defaultPos = position;
        defaultRot = rotation;
        defaultScale = scale;
        this.name = name;

        // Default material
        material = new Material(0.45f, 0.45f, shaderProgram);

        oglBuffers = GridFactory.generateStripeGrid(80,80);
        triangleMode = GL_TRIANGLE_STRIP;
    }

    public void draw(){
        if (!enabled) return;

        glUseProgram(shaderProgram);
        this.transform();
        this.material.useMaterial();
        oglBuffers.draw(triangleMode, shaderProgram);
    }

    // Apply all transformations
    private void transform() {
        Mat4 model = new Mat4Identity();

        translMatrix = new Mat4Transl(position);
        scaleMatrix = new Mat4Scale(scale);
        rotMatrix = new Mat4RotZ(rotation);

        // translate
        model = model.mul(translMatrix);
        // rotate
        model = model.mul(rotMatrix);
        // scale
        model = model.mul(scaleMatrix);
        glUniformMatrix4fv(modelLocation, false, model.floatArray());
    }

    public void setMaterial(Material theMaterial){
        material = theMaterial;
    }

    // Translate the mesh
    public void translate(double x, double y, double z) {
        position = position.add(new Vec3D(x, y, z));
    }

    // Scale the mesh
    public void scale(double x, double y, double z) {
        scale = scale.add(new Vec3D(x, y , z));
    }

    // Scale the mesh
    public void scale(double xyz) {
        scale = scale.add(new Vec3D(xyz, xyz, xyz));
    }

    public void rotate(double a) {
        rotation += a;
    }

    public void setScale(double xyz){
        this.scale = new Vec3D(xyz, xyz, xyz);
    }

    // Enables drawing
    public void enable(){
        enabled = true;
    }

    // Disables drawing
    public void disable() {
        enabled = false;
    }

    // Toggles the enabled state
    public void toggleEnabled() {
        if (enabled) {
            enabled = false;
        } else {
            enabled = true;
        }
    }

    public void resetTransforms() {
        scale = defaultScale;
        position = defaultPos;
        rotation = defaultRot;
    }

}
