package pl.edu.pw.mini.mg1.layout;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import org.joml.Vector3fc;
import pl.edu.pw.mini.mg1.models.Scene;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class PointerLayout implements Controller<Scene> {
    private JSpinner xWorld;
    private JSpinner yWorld;
    private JSpinner zWorld;
    private JSpinner xScreen;
    private JSpinner yScreen;
    private JSpinner zScreen;
    private JPanel mainPane;
    private Scene scene;
    private boolean refreshing = false;

    public PointerLayout() {
        xWorld.setModel(new SpinnerNumberModel(0, -1000, 1000, 0.01));
        yWorld.setModel(new SpinnerNumberModel(0, -1000, 1000, 0.01));
        zWorld.setModel(new SpinnerNumberModel(0, -1000, 1000, 0.01));
        xScreen.setModel(new SpinnerNumberModel(0, -1000, 1000, 0.001));
        yScreen.setModel(new SpinnerNumberModel(0, -1000, 1000, 0.001));
        zScreen.setModel(new SpinnerNumberModel(0, -1000, 1000, 0.001));
        xWorld.addChangeListener(change -> {
            if (scene == null || refreshing) return;
            scene.setPointerWorldCoords(
                    scene.getPointerWorldCoords().setComponent(0,
                            ((Number) xWorld.getValue()).floatValue()));
            refresh();
        });
        yWorld.addChangeListener(change -> {
            if (scene == null || refreshing) return;
            scene.setPointerWorldCoords(
                    scene.getPointerWorldCoords().setComponent(1,
                            ((Number) yWorld.getValue()).floatValue()));
            refresh();
        });
        zWorld.addChangeListener(change -> {
            if (scene == null || refreshing) return;
            scene.setPointerWorldCoords(
                    scene.getPointerWorldCoords().setComponent(2,
                            ((Number) zWorld.getValue()).floatValue()));
            refresh();
        });
        xScreen.addChangeListener(change -> {
            if (scene == null || refreshing) return;
            scene.setPointerScreenCoords(
                    scene.getPointerScreenCoords().setComponent(0,
                            ((Number) xScreen.getValue()).floatValue()));
            refresh();
        });
        yScreen.addChangeListener(change -> {
            if (scene == null || refreshing) return;
            scene.setPointerScreenCoords(
                    scene.getPointerScreenCoords().setComponent(1,
                            ((Number) yScreen.getValue()).floatValue()));
            refresh();
        });
        zScreen.addChangeListener(change -> {
            if (scene == null || refreshing) return;
            scene.setPointerScreenCoords(
                    scene.getPointerScreenCoords().setComponent(2,
                            ((Number) zScreen.getValue()).floatValue()));
            refresh();
        });
    }

    @Override
    public void set(Scene scene) {
        this.scene = scene;
        refresh();
    }

    @Override
    public Container getMainPane() {
        return mainPane;
    }

    @Override
    public void refresh() {
        if (scene == null) return;
        refreshing = true;
        Vector3fc position = scene.getPointerWorldCoords();
        Vector3fc screen = scene.getPointerScreenCoords();
        xWorld.setValue((double) position.x());
        yWorld.setValue((double) position.y());
        zWorld.setValue((double) position.z());
        xScreen.setValue((double) screen.x());
        yScreen.setValue((double) screen.y());
        zScreen.setValue((double) screen.z());
        refreshing = false;
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        mainPane = new JPanel();
        mainPane.setLayout(new GridLayoutManager(3, 1, new Insets(0, 0, 0, 0), -1, -1));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(3, 2, new Insets(0, 0, 0, 0), -1, -1));
        mainPane.add(panel1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel1.setBorder(BorderFactory.createTitledBorder(null, "world position", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JLabel label1 = new JLabel();
        label1.setText("x");
        panel1.add(label1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("y");
        panel1.add(label2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("z");
        panel1.add(label3, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        xWorld = new JSpinner();
        panel1.add(xWorld, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        yWorld = new JSpinner();
        panel1.add(yWorld, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        zWorld = new JSpinner();
        panel1.add(zWorld, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        mainPane.add(spacer1, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(3, 2, new Insets(0, 0, 0, 0), -1, -1));
        mainPane.add(panel2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel2.setBorder(BorderFactory.createTitledBorder(null, "screen position", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JLabel label4 = new JLabel();
        label4.setText("x");
        panel2.add(label4, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label5 = new JLabel();
        label5.setText("y");
        panel2.add(label5, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label6 = new JLabel();
        label6.setText("z");
        panel2.add(label6, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        xScreen = new JSpinner();
        panel2.add(xScreen, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        yScreen = new JSpinner();
        panel2.add(yScreen, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        zScreen = new JSpinner();
        panel2.add(zScreen, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPane;
    }

}
