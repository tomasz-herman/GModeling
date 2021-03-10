package pl.edu.pw.mini.mg1.opengl;

import com.jogamp.opengl.*;

public class GLController implements GLEventListener {
    @Override
    public void init(GLAutoDrawable drawable) {
        GL4 gl = drawable.getGL().getGL4();
        gl.glClearColor(0.2f, 0.3f, 1.0f, 1.0f);
        gl.glClearDepth(1.0f);
        gl.glEnable(GL.GL_DEPTH_TEST);
        gl.glDepthFunc(GL.GL_LEQUAL);
    }

    @Override
    public void reshape(GLAutoDrawable drawable, final int x, final int y, final int width, final int height) {
        GL4 gl = drawable.getGL().getGL4();
        gl.glViewport(x, y, width, height);
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        GL4 gl = drawable.getGL().getGL4();
        gl.glClearColor(0.6f, 0.8f, 1.0f, 1.0f);
        gl.glClearDepth(1.0f);
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {
        GL4 gl = drawable.getGL().getGL4();

    }
}
