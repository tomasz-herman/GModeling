package pl.edu.pw.mini.mg1.opengl;

import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLJPanel;
import com.jogamp.opengl.util.FPSAnimator;
import org.joml.Math;
import org.joml.Vector2i;
import org.joml.Vector3f;
import pl.edu.pw.mini.mg1.cameras.PerspectiveCamera;
import pl.edu.pw.mini.mg1.graphics.Renderer;
import pl.edu.pw.mini.mg1.layout.Controller;
import pl.edu.pw.mini.mg1.models.*;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import java.awt.event.*;
import java.util.List;
import java.util.function.Consumer;

public class GLController implements GLEventListener, MouseListener, MouseWheelListener, MouseMotionListener {
    private Scene scene;
    private Renderer renderer;

    private final Vector2i lastMousePosition = new Vector2i();
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
    private Controller<Scene> sceneController;
    private Controller<Scene> pointerController;
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
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        GL4 gl = drawable.getGL().getGL4();
        context = drawable.getContext();

        renderer = new Renderer(gl);
        scene = new Scene(new PerspectiveCamera(1, 0.1f, 100, 60));

        scene.getCamera().setPosition(0, 0, 2);

        Point a = new Point();
        Point b = new Point();
        Point c = new Point();
        Point d = new Point();
        Point e = new Point();
        Point f = new Point();
        Point g = new Point();

        scene.addModel(new BSpline(List.of(a, b, c, d, e, f, g)));
        scene.setPointerWorldCoords(new Vector3f(0, 0, 0));
        scene.addModel(a);
        scene.setPointerWorldCoords(new Vector3f(1, 0, 0));
        scene.addModel(b);
        scene.setPointerWorldCoords(new Vector3f(1, 1, 0));
        scene.addModel(c);
        scene.setPointerWorldCoords(new Vector3f(0, 1, 0));
        scene.addModel(d);
        scene.setPointerWorldCoords(new Vector3f(0, 2, 0));
        scene.addModel(e);
        scene.setPointerWorldCoords(new Vector3f(0, 1, 2));
        scene.addModel(f);
        scene.setPointerWorldCoords(new Vector3f(2, 1, 1));
        scene.addModel(g);

        modelController.set(null);
        cameraController.set(scene.getCamera());
        sceneController.set(scene);
        pointerController.set(scene);
    }

    @Override
    public void reshape(GLAutoDrawable drawable, final int x, final int y, final int width, final int height) {
        GL4 gl = drawable.getGL().getGL4();
        scene.getCamera().setResolution(width, height);
        renderer.reshape(gl, x, y, width, height);
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        handleKeyInput();
        GL4 gl = drawable.getGL().getGL4();
        scene.updateLocalPointerPosition();
        scene.disposeRemovedModels(gl);
        pointerController.refresh();
        renderer.render(gl, scene);
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {
        GL4 gl = drawable.getGL().getGL4();
        renderer.dispose(gl);
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

        addAction(gljPanel, "pressed DELETE", () -> {
            scene.deleteSelected();
            sceneController.refresh();
        });
    }

    private void addAction(JComponent component, String keyStroke, Runnable action) {
        component.getInputMap(JComponent.WHEN_FOCUSED)
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
        Model hit = scene.test((float)e.getX() / gljPanel.getWidth(), 1 - (float)e.getY() / gljPanel.getHeight());
        modelController.set(hit);
        if(hit != null) {
            if(e.isControlDown()) scene.invertSelect(hit);
            else scene.selectModel(hit);
        } else {
            scene.selectModels(new int[] {});
        }
        sceneController.refresh();
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

    public void setSceneController(Controller<Scene> sceneController) {
        this.sceneController = sceneController;
    }

    public void setPointerController(Controller<Scene> pointerController) {
        this.pointerController = pointerController;
    }
}
