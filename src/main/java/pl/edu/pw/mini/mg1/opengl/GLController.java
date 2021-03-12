package pl.edu.pw.mini.mg1.opengl;

import com.jogamp.opengl.*;
import pl.edu.pw.mini.mg1.graphics.Shader;
import pl.edu.pw.mini.mg1.models.Torus;

public class GLController implements GLEventListener {
    Shader shader;
    Torus torus;

    @Override
    public void init(GLAutoDrawable drawable) {
        GL4 gl = drawable.getGL().getGL4();
        gl.glClearColor(0.2f, 0.3f, 1.0f, 1.0f);
        gl.glClearDepth(1.0f);
        gl.glEnable(GL.GL_DEPTH_TEST);
        gl.glDepthFunc(GL.GL_LEQUAL);
        shader = new Shader(gl, "/default.vert", "/default.frag");
        torus = new Torus(40, 40, 0.5f, 0.1f);
        torus.load(gl);
    }

    @Override
    public void reshape(GLAutoDrawable drawable, final int x, final int y, final int width, final int height) {
        GL4 gl = drawable.getGL().getGL4();
        gl.glViewport(x, y, width, height);
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        GL4 gl = drawable.getGL().getGL4();
        gl.glClear(GL.GL_COLOR_BUFFER_BIT);
        gl.glClear(GL.GL_DEPTH_BUFFER_BIT);

        gl.glUseProgram(shader.getProgramID());
        gl.glEnable(GL.GL_LINE_SMOOTH);
        gl.glBindVertexArray(torus.getVao());
        gl.glDrawElements(GL4.GL_LINES, torus.vertexCount(), GL4.GL_UNSIGNED_INT, 0);
        gl.glBindVertexArray(0);
        gl.glUseProgram(0);
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {
        GL4 gl = drawable.getGL().getGL4();
        shader.dispose(gl);
        torus.dispose(gl);
    }
}
