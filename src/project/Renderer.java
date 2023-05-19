package project;


import lwjglutils.OGLBuffers;
import lwjglutils.OGLTextRenderer;
import lwjglutils.OGLUtils;
import lwjglutils.ShaderUtils;
import org.lwjgl.glfw.*;
import transforms.Camera;
import transforms.Mat4PerspRH;
import transforms.Vec3D;

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
    private Camera camera;
    private Mat4PerspRH projection;

    // Is called once
    @Override
    public void init() {
        OGLUtils.printOGLparameters();
        OGLUtils.printLWJLparameters();
        OGLUtils.printJAVAparameters();
        OGLUtils.shaderCheck();

        // Set the clear color
        glClearColor(0.15f, 0.15f, 0.15f, 0.15f);
        textRenderer = new OGLTextRenderer(width, height);

        shaderProgramMain = ShaderUtils.loadProgram("/shader");

        viewLocation = glGetUniformLocation(shaderProgramMain, "view");
        projectionLocation = glGetUniformLocation(shaderProgramMain, "projection");
        
        camera = new Camera()
                .withPosition(new Vec3D(5,5,5))
                .withAzimuth(5 / 4f * Math.PI)
                .withZenith(-1 / 5f * Math.PI);

        projection = new Mat4PerspRH(
                Math.PI / 3,
                LwjglWindow.HEIGHT / (float) LwjglWindow.WIDTH,
                0.1f,
                50.f
        );
        
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
    }

    // Called each frame
    @Override
    public void display() {
        glViewport(0, 0, width, height);

        glClearColor(0.15f,0.15f, 0.15f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        glUniform4fv(viewLocation, camera.getViewMatrix().floatArray());
        glUniform4fv(projectionLocation, projection.floatArray());

        glUseProgram(shaderProgramMain);
        oglBuffers.draw(GL_TRIANGLES, shaderProgramMain);

    }

    private GLFWKeyCallback   keyCallback = new GLFWKeyCallback() {
        @Override
        public void invoke(long window, int key, int scancode, int action, int mods) {
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

/*
	@Override
	public GLFWKeyCallback getKeyCallback() {
		return keyCallback;
	}

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