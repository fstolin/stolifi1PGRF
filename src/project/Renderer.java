package project;


import lwjglutils.OGLBuffers;
import lwjglutils.OGLTextRenderer;
import lwjglutils.OGLUtils;
import lwjglutils.ShaderUtils;
import org.lwjgl.glfw.*;
import transforms.Camera;
import transforms.Mat4PerspRH;
import transforms.Vec3D;

import java.awt.event.KeyEvent;

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
    private OGLBuffers oglBuffers;
    private int viewLocation;
    private int projectionLocation;
    private double lastFrameTime;
    private float camMovementSpeed;
    private float camBoostSpeedMultiplier;
    private float camBoostSpeedValue;
    private Camera camera;
    private Mat4PerspRH projection;
    private boolean pressedKeys[];

    // Is called once
    @Override
    public void init() {
        OGLUtils.printOGLparameters();
        OGLUtils.printLWJLparameters();
        OGLUtils.printJAVAparameters();
        OGLUtils.shaderCheck();

        // Key array
        pressedKeys = new boolean[1024];
        // Camera movement speeds
        camMovementSpeed = 1.5f;
        camBoostSpeedValue = 2.5f;
        camBoostSpeedMultiplier = 1f;

        // Set the clear color
        glClearColor(0.15f, 0.15f, 0.15f, 0.15f);
        // Polygon mode
        glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);

        textRenderer = new OGLTextRenderer(width, height);

        shaderProgramMain = ShaderUtils.loadProgram("/shader");

        viewLocation = glGetUniformLocation(shaderProgramMain, "view");
        projectionLocation = glGetUniformLocation(shaderProgramMain, "projection");
        
        camera = new Camera()
                .withPosition(new Vec3D(3,3,3))
                .withAzimuth(5 / 4f * Math.PI)
                .withZenith(-1 / 5f * Math.PI);

        projection = new Mat4PerspRH(
                Math.PI / 3,
                LwjglWindow.HEIGHT / (float) LwjglWindow.WIDTH,
                0.1f,
                20
        );

        /*
        float[] vertexBufferData = {
                -1, -1,
                1, 0,
                0, 1,
        };
        int[] indexBufferData = {0, 1, 2};

        OGLBuffers.Attrib[] attributes = {
                new OGLBuffers.Attrib("inPosition", 2)
        };

        oglBuffers = new OGLBuffers(vertexBufferData, attributes, indexBufferData);
   */
        oglBuffers = GridFactory.generateGrid(30, 30);
    }

    // Called each frame
    @Override
    public void display() {
        glViewport(0, 0, width, height);
        handleMovement(getDeltaTime());

        glClearColor(0.15f,0.15f, 0.15f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        glUniformMatrix4fv(viewLocation, false, camera.getViewMatrix().floatArray());
        glUniformMatrix4fv(projectionLocation, false, projection.floatArray());

        glUseProgram(shaderProgramMain);
        oglBuffers.draw(GL_TRIANGLES, shaderProgramMain);

    }

    // Handles all movement in the scene
    private void handleMovement(float deltaTime){
        // Camera
        handleCameraMovement(deltaTime);
    }

    // Handles camera movement based on player input
    private void handleCameraMovement(float deltaTime) {
        // Speed multiplier
        if (pressedKeys[GLFW_KEY_LEFT_SHIFT]){
            camBoostSpeedMultiplier = camBoostSpeedValue;
        } else {
            camBoostSpeedMultiplier = 1f;
        }

        float movementSpeed = camMovementSpeed * camBoostSpeedMultiplier * deltaTime;

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
    }

    private float getDeltaTime(){
        double currentTime = glfwGetTime();
        float deltaTime = (float) (currentTime - lastFrameTime);
        lastFrameTime = currentTime;
        return deltaTime;
    }

    private GLFWKeyCallback   keyCallback = new GLFWKeyCallback() {
        @Override
        public void invoke(long window, int key, int scancode, int action, int mods) {
            if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
                glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
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
        }

    };

    private GLFWCursorPosCallback cpCallbacknew = new GLFWCursorPosCallback() {
        @Override
        public void invoke(long window, double x, double y) {
        }
    };
    
    private GLFWScrollCallback scrollCallback = new GLFWScrollCallback() {
        @Override public void invoke (long window, double dx, double dy) {
        }
    };


    public GLFWKeyCallback getKeyCallback() {
        return keyCallback;
    }

/*


	@Override
	public GLFWWindowSizeCallback getWsCallback() {
		return wsCallback;
	}

	@Override
	public GLFWMouseButtonCallback getMouseCallback() {
		return mbCallback;
	}

	@Override
	public GLFWCursorPosCallback getCursorCallback() {
		return cpCallbacknew;
	}

	@Override
	public GLFWScrollCallback getScrollCallback() {
		return scrollCallback;
	}

*/
}