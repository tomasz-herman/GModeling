package pl.edu.pw.mini.mg1.opengl;

import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLJPanel;
import com.jogamp.opengl.util.FPSAnimator;
import org.joml.Math;
import org.joml.Matrix4f;
import org.joml.Vector2i;
import pl.edu.pw.mini.mg1.cameras.PerspectiveCamera;
import pl.edu.pw.mini.mg1.graphics.Shader;
import pl.edu.pw.mini.mg1.models.Torus;

import javax.swing.*;
import java.awt.event.*;

public class GLController implements GLEventListener, MouseListener, MouseWheelListener, MouseMotionListener {
    private Shader shader;
    private Torus torus;
    private PerspectiveCamera camera;

    private final Vector2i lastMousePosition;
    private boolean forward;
    private boolean backward;
    private boolean right;
    private boolean left;
    private boolean up;
    private boolean down;

    public GLController(GLJPanel gljPanel) {
        gljPanel.addGLEventListener(this);

        FPSAnimator animator = new FPSAnimator(gljPanel, 60, true);
        animator.start();

        gljPanel.addMouseListener(this);
        gljPanel.addMouseWheelListener(this);
        gljPanel.addMouseMotionListener(this);

        gljPanel.setFocusable(true);

        installKeyListener(gljPanel);

        lastMousePosition = new Vector2i();
    }

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
        camera.setPosition(0, 0, 2);
    }

    @Override
    public void reshape(GLAutoDrawable drawable, final int x, final int y, final int width, final int height) {
        GL4 gl = drawable.getGL().getGL4();
        camera.setAspect((float) width / height);
        gl.glViewport(x, y, width, height);
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        handleKeyInput();

        GL4 gl = drawable.getGL().getGL4();
        gl.glClear(GL.GL_COLOR_BUFFER_BIT);
        gl.glClear(GL.GL_DEPTH_BUFFER_BIT);

        Matrix4f mvp = camera.getViewProjectionMatrix().get(new Matrix4f());
        mvp.mul(torus.getModelMatrix());

        gl.glUseProgram(shader.getProgramID());

        shader.loadMatrix4f(gl, "mvp", mvp);

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

    private void handleKeyInput() {
        if(forward) camera.move(0, 0, -0.01f);
        if(backward) camera.move(0, 0, 0.01f);
        if(left) camera.move(-0.01f, 0, 0);
        if(right) camera.move(0.01f, 0, 0);
        if(up) camera.move(0, 0.01f, 0);
        if(down) camera.move(0, -0.01f, 0);
    }

    private void installKeyListener(GLJPanel gljPanel) {
        addAction(gljPanel, "pressed S", () -> backward = true);
        addAction(gljPanel, "released S", () -> backward = false);
        addAction(gljPanel, "pressed W", () -> forward = true);
        addAction(gljPanel, "released W", () -> forward = false);

        addAction(gljPanel, "pressed A", () -> left = true);
        addAction(gljPanel, "released A", () -> left = false);
        addAction(gljPanel, "pressed D", () -> right = true);
        addAction(gljPanel, "released D", () -> right = false);

        addAction(gljPanel, "pressed E", () -> up = true);
        addAction(gljPanel, "released E", () -> up = false);
        addAction(gljPanel, "pressed Q", () -> down = true);
        addAction(gljPanel, "released Q", () -> down = false);
    }

    private void addAction(JComponent component, String keyStroke, Runnable action) {
        component.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(keyStroke), keyStroke);
        component.getActionMap().put(keyStroke,
                new AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        action.run();
                    }
                });
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        switch (e.getButton()) {
            case 1 -> lastMousePosition.set(e.getX(), e.getY());
            case 4 -> camera.rotate(0, 0, 0.5f);
            case 5 -> camera.rotate(0, 0, -0.5f);
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        camera.setFov(Math.clamp(1f, 179f, camera.getFov() + 0.5f * e.getWheelRotation()));
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if(SwingUtilities.isLeftMouseButton(e)) {
            Vector2i mouseMove = new Vector2i(e.getX(), e.getY())
                    .sub(lastMousePosition);
            lastMousePosition.set(e.getX(), e.getY());
            camera.rotate(mouseMove.y * 0.1f, mouseMove.x * 0.1f, 0);
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }
}
