package pl.edu.pw.mini.mg1.opengl;

import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLJPanel;
import com.jogamp.opengl.util.FPSAnimator;
import org.joml.Math;
import org.joml.Matrix4f;
import org.joml.Vector2i;
import org.joml.Vector3f;
import pl.edu.pw.mini.mg1.cameras.PerspectiveCamera;
import pl.edu.pw.mini.mg1.collisions.Ray;
import pl.edu.pw.mini.mg1.graphics.Shader;
import pl.edu.pw.mini.mg1.layout.Controller;
import pl.edu.pw.mini.mg1.models.*;

import javax.swing.*;
import java.awt.event.*;
import java.util.function.Consumer;

public class GLController implements GLEventListener, MouseListener, MouseWheelListener, MouseMotionListener {
    private Shader shader;
    private Torus torus;
    private PerspectiveCamera camera;

    private Scene scene;

    private final Vector2i lastMousePosition;
    private boolean forward;
    private boolean backward;
    private boolean right;
    private boolean left;
    private boolean up;
    private boolean down;
    private boolean roll;
    private boolean unroll;

    private GLContext context;

    private Controller<Model> modelController;
    private Controller<PerspectiveCamera> cameraController;
    private final GLJPanel gljPanel;

    public GLController(GLJPanel gljPanel) {
        this.gljPanel = gljPanel;
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
        context = drawable.getContext();
        gl.glClearColor(0f, 0f, 0f, 1.0f);
        gl.glClearDepth(1.0f);
        gl.glEnable(GL.GL_DEPTH_TEST);
        gl.glDepthFunc(GL.GL_LEQUAL);
        shader = new Shader(gl, "/default.vert", "/default.frag");
        scene = new Scene(new PerspectiveCamera(1, 1, 1000, 60));

        torus = new Torus(100, 40, 0.5f, 0.1f);
        scene.addModel(torus);
        scene.addModel(new Torus(10, 10, 10, 2));
        scene.addModel(new Point());
        scene.getCamera().setPosition(0, 0, 2);
        scene.addModel(new Pointer());
        gl.glVertexAttrib3f(1, 1, 1, 1);

        modelController.set(torus);
        cameraController.set(scene.getCamera());
    }

    @Override
    public void reshape(GLAutoDrawable drawable, final int x, final int y, final int width, final int height) {
        GL4 gl = drawable.getGL().getGL4();
        scene.getCamera().setAspect((float) width / height);
        gl.glViewport(x, y, width, height);
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        handleKeyInput();

        GL4 gl = drawable.getGL().getGL4();
        gl.glClear(GL.GL_COLOR_BUFFER_BIT);
        gl.glClear(GL.GL_DEPTH_BUFFER_BIT);

        for (Model model : scene.getModels()) {
            model.validate(gl);

            Matrix4f mvp = scene.getCamera().getViewProjectionMatrix().get(new Matrix4f());
            mvp.mul(torus.getModelMatrix());

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

    @Override
    public void dispose(GLAutoDrawable drawable) {
        GL4 gl = drawable.getGL().getGL4();
        shader.dispose(gl);
        scene.dispose(gl);
    }

    private void handleKeyInput() {
        if(forward) scene.getCamera().move(0, 0, -0.01f);
        if(backward) scene.getCamera().move(0, 0, 0.01f);
        if(left) scene.getCamera().move(-0.01f, 0, 0);
        if(right) scene.getCamera().move(0.01f, 0, 0);
        if(up) scene.getCamera().move(0, 0.01f, 0);
        if(down) scene.getCamera().move(0, -0.01f, 0);
        if(roll) scene.getCamera().rotate(0, 0, -0.5f);
        if(unroll) scene.getCamera().rotate(0, 0, 0.5f);
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

        addAction(gljPanel, "pressed Z", () -> roll = true);
        addAction(gljPanel, "released Z", () -> roll = false);
        addAction(gljPanel, "pressed X", () -> unroll = true);
        addAction(gljPanel, "released X", () -> unroll = false);
    }

    private void addAction(JComponent component, String keyStroke, Runnable action) {
        component.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(keyStroke), keyStroke);
        component.getActionMap().put(keyStroke, new AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        action.run();
                    }
                });
    }

    private void runOnOpenGL(Consumer<GL4> action) throws GLException {
        context.makeCurrent();
        try {
            action.accept(context.getGL().getGL4());
        } finally {
            context.release();
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        gljPanel.requestFocus();
        Ray ray = scene.getCamera().getRay((float)e.getX() / gljPanel.getWidth(), (float)e.getY() / gljPanel.getHeight());
        System.out.println(ray);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        gljPanel.requestFocus();
        if(SwingUtilities.isLeftMouseButton(e)) {
            lastMousePosition.set(e.getX(), e.getY());
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {
        Vector3f v = new Vector3f();
        System.out.println(scene.getCamera().project(v));
    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        scene.getCamera().setFov(Math.clamp(1f, 179f, scene.getCamera().getFov() + 0.5f * e.getWheelRotation()));
        cameraController.refresh();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if(SwingUtilities.isLeftMouseButton(e)) {
            Vector2i mouseMove = new Vector2i(e.getX(), e.getY())
                    .sub(lastMousePosition);
            lastMousePosition.set(e.getX(), e.getY());
            scene.getCamera().rotate(mouseMove.y * 0.1f, mouseMove.x * 0.1f, 0);
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }

    public void setModelController(Controller<Model> modelController) {
        this.modelController = modelController;
    }

    public void setCameraController(Controller<PerspectiveCamera> cameraController) {
        this.cameraController = cameraController;
    }
}
