package pl.edu.pw.mini.mg1.graphics;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL4;
import org.joml.Matrix4f;
import pl.edu.pw.mini.mg1.models.Model;
import pl.edu.pw.mini.mg1.models.Pointer;
import pl.edu.pw.mini.mg1.models.Scene;

public class Renderer {
    private final Shader shader;

    public Renderer(GL4 gl) {
        shader = new Shader(gl, "/default.vert", "/default.frag");
        gl.glClearColor(0f, 0f, 0f, 1.0f);
        gl.glClearDepth(1.0f);
        gl.glEnable(GL.GL_DEPTH_TEST);
        gl.glDepthFunc(GL.GL_LEQUAL);
        gl.glVertexAttrib3f(1, 1, 1, 1);
        gl.glPointSize(3.0f);
    }

    public void render(GL4 gl, Scene scene) {
        gl.glClear(GL.GL_COLOR_BUFFER_BIT);
        gl.glClear(GL.GL_DEPTH_BUFFER_BIT);

        for (Model model : scene.getModelsAndPointers()) {
            gl.glLineWidth(model instanceof Pointer ? 3 : 1);
            model.validate(gl);

            Matrix4f mvp = scene.getCamera().getViewProjectionMatrix().get(new Matrix4f());
            mvp.mul(model.getModelMatrix());

            gl.glUseProgram(shader.getProgramID());

            shader.loadMatrix4f(gl, "mvp", mvp);

            gl.glBindVertexArray(model.getMesh().getVao());
            gl.glDrawElements(model.getMesh().getPrimitivesType(),
                    model.getMesh().vertexCount(),
                    GL4.GL_UNSIGNED_INT, 0);
            gl.glBindVertexArray(0);
            gl.glUseProgram(0);
        }
    }

    public void reshape(GL gl, int x, int y, int width, int height) {
        gl.glViewport(x, y, width, height);
    }

    public void dispose(GL4 gl) {
        shader.dispose(gl);
    }
}
