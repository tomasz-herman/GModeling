package pl.edu.pw.mini.mg1.opengl;

import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLJPanel;
import com.jogamp.opengl.util.FPSAnimator;
import org.joml.Math;
import org.joml.Vector2f;
import org.joml.Vector2i;
import pl.edu.pw.mini.mg1.cameras.PerspectiveCamera;
import pl.edu.pw.mini.mg1.graphics.Renderer;
import pl.edu.pw.mini.mg1.layout.Controller;
import pl.edu.pw.mini.mg1.milling.MaterialBlock;
import pl.edu.pw.mini.mg1.milling.MillingException;
import pl.edu.pw.mini.mg1.milling.MillingTool;
import pl.edu.pw.mini.mg1.milling.Path;
import pl.edu.pw.mini.mg1.models.*;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import java.awt.event.*;
import java.util.HashSet;
import java.util.List;
import java.util.function.Consumer;

public class GLController implements GLEventListener, MouseListener, MouseWheelListener, MouseMotionListener {
    public float cameraSpeed = 0.01f;
    private Scene scene;
    private Renderer renderer;

    private final Vector2i lastMousePosition = new Vector2i();
    private final Vector2i selectionStart = new Vector2i();
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
    private Controller<Renderer> rendererController;
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

        scene.addModel(new MillingSimulator());

//        MaterialBlock block = new MaterialBlock(new Vector2f(180, 180), new Vector2i(1000, 1000), 50, 16);
//        MillingTool tool = new MillingTool(8, 20, false);
//
//        Cutter cutter = new Cutter(tool);
//        scene.addModel(cutter);
//
//        MilledBlock model = new MilledBlock(block);
//        scene.addModel(model);
//
//        new Thread(() -> {
//            try {
//                Path path = new Path(GLController.class.getResourceAsStream("/p2/1.k16"), 1);
//                try {
//                    block.mill(tool, path, progress -> System.out.printf("%.2f%%%n", progress), vec -> {
//                        try {
//                            vec.div(100);
//                            cutter.setPosition(vec.x, vec.y, vec.z);
//                            Thread.sleep(1);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                    }, model::reloadTexture);
//                } catch (MillingException e) {
//                    e.printStackTrace();
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }).start();

        modelController.set(null);
        cameraController.set(scene.getCamera());
        sceneController.set(scene);
        pointerController.set(scene);
        rendererController.set(renderer);
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
        renderer.getRenderFunction().accept(gl, scene);
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {
        GL4 gl = drawable.getGL().getGL4();
        renderer.dispose(gl);
        scene.dispose(gl);
    }

    private void handleKeyInput() {
        if(forward) scene.getCamera().move(0, 0, -cameraSpeed);
        if(backward) scene.getCamera().move(0, 0, cameraSpeed);
        if(left) scene.getCamera().move(-cameraSpeed, 0, 0);
        if(right) scene.getCamera().move(cameraSpeed, 0, 0);
        if(up) scene.getCamera().move(0, cameraSpeed, 0);
        if(down) scene.getCamera().move(0, -cameraSpeed, 0);
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

        addAction(gljPanel, "control pressed A", () -> scene.selectAll());
        addAction(gljPanel, "control pressed X", () -> scene.deleteSelected());

        addAction(gljPanel, "pressed M", () -> scene.mergePoints());
        addAction(gljPanel, "pressed U", () -> scene.selectPointsFromSelectedObjects());

        for (int i = 0; i < 10; i++) {
            int finalI = i;
            addAction(gljPanel, "control pressed %d".formatted(i), () -> scene.serialize("scene%d.xml".formatted(finalI)));
            addAction(gljPanel, "pressed %d".formatted(i), () -> scene.deserialize("scene%d.xml".formatted(finalI)));
        }

        addAction(gljPanel, "pressed PERIOD", () -> cameraSpeed += 0.01f);
        addAction(gljPanel, "pressed COMMA", () -> cameraSpeed -= 0.01f);

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
            else if(e.isShiftDown()) {
                List<Model> hits = scene.testAll((float)e.getX() / gljPanel.getWidth(), 1 - (float)e.getY() / gljPanel.getHeight());
                for (Model model : hits) {
                    scene.invertSelect(model);
                }
            }
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
        if(SwingUtilities.isRightMouseButton(e)) {
            selectionStart.set(e.getX(), e.getY());
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if(SwingUtilities.isRightMouseButton(e)) {
            Vector2i selectionEnd = new Vector2i(e.getX(), e.getY());
            if(selectionStart.x > selectionEnd.x) {
                int temp = selectionStart.x;
                selectionEnd.x = selectionStart.x;
                selectionStart.x = temp;
            }
            if(selectionStart.y > selectionEnd.y) {
                int temp = selectionStart.y;
                selectionEnd.y = selectionStart.y;
                selectionStart.y = temp;
            }
            HashSet<Model> selected = new HashSet<>();
            for (int i = selectionStart.x; i < selectionEnd.x; i++) {
                for (int j = selectionStart.y; j < selectionEnd.y; j++) {
                    List<Model> hits = scene.testAll((float)i / gljPanel.getWidth(), 1 - (float)j / gljPanel.getHeight());
                    selected.addAll(hits);
                }
            }
            scene.selectModels(new int[]{});
            for (Model model : selected) {
                scene.invertSelect(model);
            }
        }
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

    public void setRendererController(Controller<Renderer> rendererController) {
        this.rendererController = rendererController;
    }
}
