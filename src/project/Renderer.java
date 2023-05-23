package project;


import lwjglutils.OGLTextRenderer;
import lwjglutils.OGLUtils;
import lwjglutils.ShaderUtils;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.*;
import transforms.Camera;
import transforms.Mat4OrthoRH;
import transforms.Mat4PerspRH;
import transforms.Vec3D;

import java.nio.DoubleBuffer;
import java.util.ArrayList;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;


/**
* 
* @author PGRF FIM UHK
* @version 2.0
* @since 2019-09-02
*/
public class Renderer extends AbstractRenderer{

    private int shaderProgramMain;
    private int viewLocation;
    private int projectionLocation;
    private int polygonMode = 0;
    private double lastFrameTime;
    private float camMovementSpeed;
    private float camBoostSpeedMultiplier;
    private float camBoostSpeedValue;
    private float transformSpeed;
    private Camera camera;
    private Light directionalLight;
    private Mat4OrthoRH orthoProjection;
    private Mat4PerspRH perspProjection;
    private boolean orthoProjectionEnabled;
    private int shaderMode;
    private int shaderModeLocation;
    private boolean pressedKeys[];
    private boolean mouseButton2 = false;
    double ox, oy;
    private ArrayList<Mesh> meshList;
    private Mesh activeMesh;
    private int meshIDLocation;

    // Is called once
    @Override
    public void init() {
        OGLUtils.printOGLparameters();
        OGLUtils.printLWJLparameters();
        OGLUtils.printJAVAparameters();
        OGLUtils.shaderCheck();

        // What to render - texture, xyz, normals etc. 0 = default
        shaderMode = 0;

        // Key array
        pressedKeys = new boolean[1024];
        // Camera movement speeds
        camMovementSpeed = 1.5f;
        camBoostSpeedValue = 2.5f;
        camBoostSpeedMultiplier = 1f;
        // Transform speeds
        transformSpeed = 1.5f;
        // Meshes list
        meshList = new ArrayList<Mesh>();

        // Set the clear color
        glClearColor(0.15f, 0.15f, 0.15f, 0.15f);
        // Polygon mode
        switchPolygonMode();
        glEnable(GL_DEPTH_TEST);

        textRenderer = new OGLTextRenderer(width, height);

        shaderProgramMain = ShaderUtils.loadProgram("/shader");

        viewLocation = glGetUniformLocation(shaderProgramMain, "view");
        projectionLocation = glGetUniformLocation(shaderProgramMain, "projection");
        shaderModeLocation = glGetUniformLocation(shaderProgramMain, "shaderMode");
        meshIDLocation = glGetUniformLocation(shaderProgramMain, "meshID");

        // ### INITIALIZE OBJECTS ###
        initializeObjects();

        // ### INITIALIZE DIRECTIONAL LIGHT ###
        directionalLight = new Light( 1.f, 0.9f, 0.8f, 0.07f,
                                            0.f, 0.0f, 5.f, 0.54f,
                                            shaderProgramMain);

        // ### CAMERA ###
        camera = new Camera()
                .withPosition(new Vec3D(3,3,3))
                .withAzimuth(5 / 4f * Math.PI)
                .withZenith(-1 / 5f * Math.PI);

        // ## PROJECTIONS ##
        perspProjection = new Mat4PerspRH(
                Math.PI / 3,
                LwjglWindow.HEIGHT / (float) LwjglWindow.WIDTH,
                0.1f,
                20
        );

        orthoProjection = new Mat4OrthoRH(10.0,7.5, 0.1, 100.0);

    }

    private void initializeObjects(){
        meshList.add(new WaveObject(shaderProgramMain, 0.0, 0.0, 0.0));
        activeMesh = meshList.get(0);
        WaveObject obj = new WaveObject(shaderProgramMain, -1.0,0.0,0.0);
        meshList.add(obj);
        Mesh mesh3 = new Mesh(shaderProgramMain, 0.f, 2.f, 0.f);
        meshList.add((mesh3));
        Mesh mesh4 = new Mesh(shaderProgramMain, 0.f,8.f, 0.0f);
        meshList.add(mesh4);
    }

    // Called each frame
    @Override
    public void display() {
        glViewport(0, 0, width, height);
        handleKeyPresses();

        glClearColor(0.15f,0.15f, 0.15f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        handleRenderUniforms();
        directionalLight.useLight();
        drawMeshes();
    }

    // Draws all meshes from the mesh list
    private void drawMeshes() {
        for (int i = 0; i < meshList.size(); i++) {
            // Draw the mesh
            Mesh theMesh = meshList.get(i);
            theMesh.draw();
            // Update its shape - based on it's id
            glUniform1i(meshIDLocation, i);
        }
    }

    // Handles the uniform variables required in Renderer class
    private void handleRenderUniforms() {
        glUniformMatrix4fv(viewLocation, false, camera.getViewMatrix().floatArray());
        glUniform1i(shaderModeLocation, shaderMode);
        if (orthoProjectionEnabled) {
            glUniformMatrix4fv(projectionLocation, false, orthoProjection.floatArray());
        } else {
            glUniformMatrix4fv(projectionLocation, false, perspProjection.floatArray());
        }
    }

    // Handles all movement in the scene
    private void handleKeyPresses(){
        float deltaTime = getDeltaTime();
        // Camera
        handleCameraMovement(deltaTime);
    }

    // Handles camera movement based on player input - handles keypress that need to be smooth
    private void handleCameraMovement(float deltaTime) {
        // Speed multiplier
        if (pressedKeys[GLFW_KEY_LEFT_SHIFT]){
            camBoostSpeedMultiplier = camBoostSpeedValue;
        } else {
            camBoostSpeedMultiplier = 1f;
        }

        float movementSpeed = camMovementSpeed * camBoostSpeedMultiplier * deltaTime;
        float transformActualSpeed = transformSpeed * camBoostSpeedMultiplier * deltaTime;
        float lightDimSpeed = directionalLight.intensityModificationSpeed * deltaTime;

        // WASD RF movement
        if (pressedKeys[GLFW_KEY_W]){
            camera = camera.forward(movementSpeed);
        }
        if (pressedKeys[GLFW_KEY_S]){
            camera = camera.backward(movementSpeed);
        }
        if (pressedKeys[GLFW_KEY_A]){
            camera = camera.left(movementSpeed);
        }
        if (pressedKeys[GLFW_KEY_D]){
            camera = camera.right(movementSpeed);
        }
        if (pressedKeys[GLFW_KEY_R]){
            camera = camera.up(movementSpeed);
        }
        if (pressedKeys[GLFW_KEY_F]){
            camera = camera.down(movementSpeed);
        }

        // Reset the current mesh
        if (pressedKeys[GLFW_KEY_KP_0]){
            activeMesh.resetTransforms();
        }

        // TRANSFORMING the current mesh
        // Scale
        if (pressedKeys[GLFW_KEY_KP_9]){
            activeMesh.scale(transformActualSpeed);
        }
        if (pressedKeys[GLFW_KEY_KP_7]){
            activeMesh.scale(-transformActualSpeed);
        }
        // Up & Down
        if (pressedKeys[GLFW_KEY_KP_SUBTRACT]){
            activeMesh.translate(0.0,0.0,transformActualSpeed);
        }
        if (pressedKeys[GLFW_KEY_KP_ADD]){
            activeMesh.translate(0.0,0.0,-transformActualSpeed);
        }
        // Left & Right
        if (pressedKeys[GLFW_KEY_KP_4]){
            activeMesh.translate(transformActualSpeed, 0.0,0.0);
        }
        if (pressedKeys[GLFW_KEY_KP_6]){
            activeMesh.translate(-transformActualSpeed, 0.0,0.0);
        }
        // Back & Forward
        if (pressedKeys[GLFW_KEY_KP_8]){
            activeMesh.translate(0.0, transformActualSpeed, 0.0);
        }
        if (pressedKeys[GLFW_KEY_KP_5]){
            activeMesh.translate(0.0, -transformActualSpeed, 0.0);
        }
        // Rotation
        if (pressedKeys[GLFW_KEY_KP_1]){
            activeMesh.rotate(0.025f * transformSpeed);
        }
        if (pressedKeys[GLFW_KEY_KP_3]){
            activeMesh.rotate(-0.025f * transformSpeed);
        }

        // Light Dimming - ambient
        if (pressedKeys[GLFW_KEY_N]){
            directionalLight.decreaseAmbientIntensity(lightDimSpeed);
        }
        if (pressedKeys[GLFW_KEY_M]){
            directionalLight.increaseAmbientIntensity(lightDimSpeed);
        }
        // Light Dimming - diffuse
        if (pressedKeys[GLFW_KEY_K]){
            directionalLight.decreaseDiffuseIntensity(lightDimSpeed);
        }
        if (pressedKeys[GLFW_KEY_L]){
            directionalLight.increaseDiffuseIntensity(lightDimSpeed);
        }
    }

    // Returns the delta time
    private float getDeltaTime(){
        double currentTime = glfwGetTime();
        float deltaTime = (float) (currentTime - lastFrameTime);
        lastFrameTime = currentTime;
        return deltaTime;
    }

    // Switches the polygon mode - FILL, LINE, POINT -> hotkey: P
    private void switchPolygonMode(){
        switch (polygonMode) {
            // Default option
            case 0:
                glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
                polygonMode++;
                break;
            case 1:
                glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
                polygonMode++;
                break;
            case 2:
                glPolygonMode(GL_FRONT_AND_BACK, GL_POINT);
                polygonMode = 0;
                break;
        }
    }

    // Switches the shaderMode - rendering textures, xyz, normals, lighting etc.
    private void switchShaderMode(){
        int shaderModesCount = 3;
        if (shaderMode >= shaderModesCount) {
            shaderMode = 0;
        } else {
            shaderMode++;
        }
    }

    private void switchProjection(){
        if (orthoProjectionEnabled) {
            orthoProjectionEnabled = false;
        } else {
            orthoProjectionEnabled = true;
        }
    }

    // Handle key presses that don't have to be smooth
    private GLFWKeyCallback   keyCallback = new GLFWKeyCallback() {
        @Override
        public void invoke(long window, int key, int scancode, int action, int mods) {
            if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
                glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
            // POLYGON MODES
            if (key == GLFW_KEY_P && action == GLFW_PRESS)
                switchPolygonMode();
            // PROJECTION SWITCH
            if (key == GLFW_KEY_O && action == GLFW_PRESS) {
                switchProjection();
            }
            // SHADERMODE SWITCH
            if (key == GLFW_KEY_I && action == GLFW_PRESS) {
                switchShaderMode();
            }
            // OBJECTS RENDERING
            if (key == GLFW_KEY_1 && action == GLFW_PRESS) {
                if (meshList.size() > 0) {
                    activeMesh = meshList.get(0);
                    activeMesh.toggleEnabled();
                }
            }
            if (key == GLFW_KEY_2 && action == GLFW_PRESS) {
                if (meshList.size() > 1) {
                    activeMesh = meshList.get(1);
                    activeMesh.toggleEnabled();
                }
            }
            if (key == GLFW_KEY_3 && action == GLFW_PRESS) {
                if (meshList.size() > 2) {
                    activeMesh = meshList.get(2);
                    activeMesh.toggleEnabled();
                }
            }
            if (key == GLFW_KEY_4 && action == GLFW_PRESS) {
                if (meshList.size() > 3) {
                    activeMesh = meshList.get(3);
                    activeMesh.toggleEnabled();
                }
            }
            if (key == GLFW_KEY_5 && action == GLFW_PRESS) {
                if (meshList.size() > 4) {
                    activeMesh = meshList.get(4);
                    activeMesh.toggleEnabled();
                }
            }
            if (key == GLFW_KEY_6 && action == GLFW_PRESS) {
                if (meshList.size() > 5) {
                    activeMesh = meshList.get(5);
                    activeMesh.toggleEnabled();
                }
            }

            // SMOOTH MOVEMENT & TRANSFORMATIONS -> save to pressedKey Array
            if (action == GLFW_RELEASE){
                pressedKeys[key] = false;
            }
            if (action == GLFW_PRESS){
                pressedKeys[key] = true;
            }
        }
    };


    
    private GLFWWindowSizeCallback wsCallback = new GLFWWindowSizeCallback() {
        @Override
        public void invoke(long window, int w, int h) {
        }
    };
    
    private GLFWMouseButtonCallback mbCallback = new GLFWMouseButtonCallback () {
        @Override
        public void invoke(long window, int button, int action, int mods) {
            mouseButton2 = glfwGetMouseButton(window, GLFW_MOUSE_BUTTON_2) == GLFW_PRESS;

            if (button==GLFW_MOUSE_BUTTON_2 && action == GLFW_PRESS){
                mouseButton2 = true;
                DoubleBuffer xBuffer = BufferUtils.createDoubleBuffer(1);
                DoubleBuffer yBuffer = BufferUtils.createDoubleBuffer(1);
                glfwGetCursorPos(window, xBuffer, yBuffer);
                ox = xBuffer.get(0);
                oy = yBuffer.get(0);
            }

            if (button==GLFW_MOUSE_BUTTON_2 && action == GLFW_RELEASE){
                mouseButton2 = false;
                DoubleBuffer xBuffer = BufferUtils.createDoubleBuffer(1);
                DoubleBuffer yBuffer = BufferUtils.createDoubleBuffer(1);
                glfwGetCursorPos(window, xBuffer, yBuffer);
                double x = xBuffer.get(0);
                double y = yBuffer.get(0);
                camera = camera.addAzimuth((double) Math.PI * (ox - x) / width)
                        .addZenith((double) Math.PI * (oy - y) / width);
                ox = x;
                oy = y;
            }
        }

    };

    private GLFWCursorPosCallback cpCallbacknew = new GLFWCursorPosCallback() {
        @Override
        public void invoke(long window, double x, double y) {
            if (mouseButton2) {
                camera = camera.addAzimuth((double) Math.PI * (ox - x) / width)
                        .addZenith((double) Math.PI * (oy - y) / width);
                ox = x;
                oy = y;
            }
        }
    };
    
    private GLFWScrollCallback scrollCallback = new GLFWScrollCallback() {
        @Override public void invoke (long window, double dx, double dy) {
        }
    };


    public GLFWKeyCallback getKeyCallback() {
        return keyCallback;
    }

    @Override
    public GLFWMouseButtonCallback getMouseCallback() {
        return mbCallback;
    }

    @Override
    public GLFWCursorPosCallback getCursorCallback() {
        return cpCallbacknew;
    }

/*


	@Override
	public GLFWWindowSizeCallback getWsCallback() {
		return wsCallback;
	}


	@Override
	public GLFWScrollCallback getScrollCallback() {
		return scrollCallback;
	}

*/
}