package project;

import lwjglutils.OGLTextRenderer;
import lwjglutils.OGLUtils;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.*;

import java.nio.DoubleBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;


/**
* 
* @author PGRF FIM UHK
* @version 2.0
* @since 2019-09-02
*/
public abstract class AbstractRenderer {
	int pass;
	protected int width;
	protected int height;
	protected OGLTextRenderer textRenderer;
	
	public void init() {
		OGLUtils.printOGLparameters();
		OGLUtils.printLWJLparameters();
		OGLUtils.printJAVAparameters();
		OGLUtils.shaderCheck();
		// Set the clear color
		glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		textRenderer = new OGLTextRenderer(width, height);	
	}
	
	public void display() {
		glViewport(0, 0, width, height);

		glClearColor(0.15f,0.15f, 0.15f, 1.0f);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); 

	}

	protected GLFWKeyCallback keyCallback = new GLFWKeyCallback() {
		@Override
		public void invoke(long window, int key, int scancode, int action, int mods) {
			if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
				// We will detect this in our rendering loop
				glfwSetWindowShouldClose(window, true); 
			if (action == GLFW_RELEASE){
				System.out.println("Key release " + key);
				}
			if (action == GLFW_PRESS){
				System.out.println("Key pressed " + key);
			}
		}
	};
    
	protected GLFWWindowSizeCallback wsCallback = new GLFWWindowSizeCallback() {
        @Override
        public void invoke(long window, int w, int h) {
            if (w > 0 && h > 0) {
            	width = w;
            	height = h;
            	System.out.println("Windows resize to [" + w + ", " + h + "]");
            	if (textRenderer != null)
            		textRenderer.resize(width, height);
            }
        }
    };
    
    protected GLFWMouseButtonCallback mbCallback = new GLFWMouseButtonCallback () {
		
		@Override
		public void invoke(long window, int button, int action, int mods) {
			//mouseButton1 = glfwGetMouseButton(window, GLFW_MOUSE_BUTTON_1) == GLFW_PRESS;
			DoubleBuffer xBuffer = BufferUtils.createDoubleBuffer(1);
			DoubleBuffer yBuffer = BufferUtils.createDoubleBuffer(1);
			glfwGetCursorPos(window, xBuffer, yBuffer);
			double x = xBuffer.get(0);
			double y = yBuffer.get(0);
			
			if (button==GLFW_MOUSE_BUTTON_1 && action == GLFW_PRESS){
				System.out.println("Mouse button 1 is pressed at coursor position [" + x + ", " + y + "]");
			}
			
			if (button==GLFW_MOUSE_BUTTON_1 && action == GLFW_RELEASE){
				System.out.println("Mouse button 1 is released at coursor position [" + x + ", " + y + "]");
        	}

			if (button==GLFW_MOUSE_BUTTON_2 && action == GLFW_PRESS){
				System.out.println("Mouse button 2 is pressed at coursor position [" + x + ", " + y + "]");
			}

			if (button==GLFW_MOUSE_BUTTON_2 && action == GLFW_RELEASE){
				System.out.println("Mouse button 2 is released at coursor position [" + x + ", " + y + "]");
			}
		}
		
	};
	
	protected GLFWCursorPosCallback cpCallbacknew = new GLFWCursorPosCallback() {
        @Override
        public void invoke(long window, double x, double y) {
        	System.out.println("Coursor position [" + x + ", " + y + "]");
    	}
    };
    
    protected GLFWScrollCallback scrollCallback = new GLFWScrollCallback() {
        @Override public void invoke (long window, double dx, double dy) {
            System.out.println("Mouse whell velocity " + dy);
        }
    };
 
	public GLFWKeyCallback getKeyCallback() {
		return keyCallback;
	}

	public GLFWWindowSizeCallback getWsCallback() {
		return wsCallback;
	}

	public GLFWMouseButtonCallback getMouseCallback() {
		return mbCallback;
	}

	public GLFWCursorPosCallback getCursorCallback() {
		return cpCallbacknew;
	}
	
	public GLFWScrollCallback getScrollCallback() {
		return scrollCallback;
	}
	
	public void dispose(){

	}
}