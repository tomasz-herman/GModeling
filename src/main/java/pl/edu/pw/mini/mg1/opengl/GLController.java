package pl.edu.pw.mini.mg1.opengl;

import com.jogamp.opengl.*;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import pl.edu.pw.mini.mg1.cameras.PerspectiveCamera;
import pl.edu.pw.mini.mg1.graphics.Shader;
import pl.edu.pw.mini.mg1.models.Torus;

public class GLController implements GLEventListener {
    private Shader shader;
    private Torus torus;
    private PerspectiveCamera camera;

    @Override
    public void init(GLAutoDrawable drawable) {
        GL4 gl = drawable.getGL().getGL4();
        gl.glClearColor(0f, 0f, 0f, 1.0f);
        gl.glClearDepth(1.0f);
        gl.glEnable(GL.GL_DEPTH_TEST);
        gl.glDepthFunc(GL.GL_LEQUAL);
        shader = new Shader(gl, "/default.vert", "/default.frag");
        torus = new Torus(100, 40, 0.5f, 0.1f);
        torus.getMesh().load(gl);
        camera = new PerspectiveCamera(1, 1, 1000, 60);
        camera.setPosition(new Vector3f(0, 0, 2));
    }

    @Override
    public void reshape(GLAutoDrawable drawable, final int x, final int y, final int width, final int height) {
        GL4 gl = drawable.getGL().getGL4();
        camera.setAspect((float) width / height);
        gl.glViewport(x, y, width, height);
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        GL4 gl = drawable.getGL().getGL4();
        gl.glClear(GL.GL_COLOR_BUFFER_BIT);
        gl.glClear(GL.GL_DEPTH_BUFFER_BIT);

        Matrix4f mvp = camera.getViewProjectionMatrix().get(new Matrix4f());
        mvp.mul(torus.getModelMatrix());

        torus.rotate(0, 0.01f, 0);

        gl.glUseProgram(shader.getProgramID());

        shader.loadMatrix4f(gl, "mvp", mvp);

        gl.glEnable(GL.GL_LINE_SMOOTH);
        gl.glBindVertexArray(torus.getMesh().getVao());
        gl.glDrawElements(torus.getMesh().getPrimitivesType(),
                torus.getMesh().vertexCount(),
                GL4.GL_UNSIGNED_INT, 0);
        gl.glBindVertexArray(0);
        gl.glUseProgram(0);
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {
        GL4 gl = drawable.getGL().getGL4();
        shader.dispose(gl);
        torus.getMesh().dispose(gl);
    }
}
