package pl.edu.pw.mini.mg1.layout;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.jogamp.opengl.awt.GLJPanel;
import com.jogamp.opengl.math.Matrix4;
import org.jdesktop.swingx.JXPanel;
import pl.edu.pw.mini.mg1.opengl.GLController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Objects;

public class MainLayout {
    private JSplitPane mainPane;
    private GLJPanel GLPanel;
    private JScrollPane controlsPane;
    private JComboBox<String> controllerComboBox;
    private final JXPanel panel;

    public MainLayout() {
        $$$setupUI$$$();

        mainPane.setDividerLocation(0.75);

        GLController controller = new GLController(GLPanel);

        panel = new JXPanel(new BorderLayout());
        controlsPane.setViewportView(panel);
        panel.setScrollableTracksViewportWidth(true);
        panel.setScrollableTracksViewportHeight(false);

        ModelLayout modelController = new ModelLayout();
        CameraLayout cameraController = new CameraLayout();
        SceneLayout sceneController = new SceneLayout();
        sceneController.setModelController(modelController);
        PointerLayout pointerController = new PointerLayout();
        RendererController rendererController = new RendererController();
        PathGenerationController pathGenerationController = new PathGenerationController();
        loadController(modelController.getMainPane());

        controller.setModelController(modelController);
        controller.setCameraController(cameraController);
        controller.setSceneController(sceneController);
        controller.setPointerController(pointerController);
        controller.setRendererController(rendererController);
        controller.setPathsController(pathGenerationController);

        controllerComboBox.addActionListener(e -> {
            switch ((String) Objects.requireNonNull(controllerComboBox.getSelectedItem())) {
                case "Model" -> loadController(modelController.getMainPane());
                case "Camera" -> loadController(cameraController.getMainPane());
                case "Scene" -> {
                    loadController(sceneController.getMainPane());
                    sceneController.setPointerController(pointerController);
                }
                case "Pointer" -> loadController(pointerController.getMainPane());
                case "Renderer" -> loadController(rendererController.getMainPane());
                case "Paths" -> loadController(pathGenerationController.getMainPane());
            }
        });
    }

    public void loadController(Component controller) {
        panel.removeAll();
        panel.add(controller, BorderLayout.CENTER);
        panel.revalidate();
        panel.repaint();
        Dimension minimumSize = panel.getMinimumSize();
        minimumSize.setSize(minimumSize.getWidth() + 20, -1);
        controlsPane.setMinimumSize(minimumSize);
        if (controlsPane.getSize().width < minimumSize.width) {
            controlsPane.setSize(minimumSize.width, controlsPane.getHeight());
            mainPane.setDividerLocation(mainPane.getWidth() - minimumSize.width - 20 - mainPane.getDividerSize() / 2);
        }
    }

    public Container getMainPane() {
        return mainPane;
    }

    public static JSlider createSlider() {
        return new JSlider(0, 36000, 0) {
            private SliderPopupListener popupHandler;

            @Override
            public void updateUI() {
                removeMouseMotionListener(popupHandler);
                removeMouseListener(popupHandler);
                removeMouseWheelListener(popupHandler);
                super.updateUI();
                popupHandler = new SliderPopupListener();
                addMouseMotionListener(popupHandler);
                addMouseListener(popupHandler);
                addMouseWheelListener(popupHandler);
            }
        };
    }

    private static class SliderPopupListener extends MouseAdapter {
        private final JDialog toolTip = new JDialog();
        private final JLabel label = new JLabel("", SwingConstants.CENTER);
        private final Dimension size = new Dimension(80, 24);
        private int prevValue = -1;

        public SliderPopupListener() {
            super();
            label.setOpaque(false);
            label.setBorder(BorderFactory.createLineBorder(new Color(74, 136, 199)));
            toolTip.setUndecorated(true);
            toolTip.add(label);
            toolTip.setSize(size);
        }

        protected void updateToolTip(MouseEvent me) {
            JSlider slider = (JSlider) me.getComponent();
            int intValue = slider.getValue();
            if (prevValue != intValue) {
                label.setText(String.format("%.2f", slider.getValue() / 100f));
                Point pt = me.getPoint();
                pt.y = -size.height;
                SwingUtilities.convertPointToScreen(pt, me.getComponent());
                pt.translate(-size.width / 2, 0);
                toolTip.setLocation(pt);
            }
            prevValue = intValue;
        }

        @Override
        public void mouseDragged(MouseEvent me) {
            toolTip.setVisible(true);
            updateToolTip(me);
        }

        @Override
        public void mouseReleased(MouseEvent me) {
            toolTip.setVisible(false);
        }
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        mainPane = new JSplitPane();
        mainPane.setDividerLocation(0);
        mainPane.setDividerSize(10);
        mainPane.setOneTouchExpandable(true);
        mainPane.setResizeWeight(0.9);
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        mainPane.setLeftComponent(panel1);
        GLPanel = new GLJPanel();
        panel1.add(GLPanel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(1280, 720), null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(2, 1, new Insets(5, 5, 5, 5), -1, -1));
        mainPane.setRightComponent(panel2);
        controllerComboBox = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel1 = new DefaultComboBoxModel();
        defaultComboBoxModel1.addElement("Model");
        defaultComboBoxModel1.addElement("Camera");
        defaultComboBoxModel1.addElement("Scene");
        defaultComboBoxModel1.addElement("Pointer");
        defaultComboBoxModel1.addElement("Renderer");
        defaultComboBoxModel1.addElement("Paths");
        controllerComboBox.setModel(defaultComboBoxModel1);
        panel2.add(controllerComboBox, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        controlsPane = new JScrollPane();
        controlsPane.setHorizontalScrollBarPolicy(31);
        panel2.add(controlsPane, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPane;
    }

}
