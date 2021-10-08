package pl.edu.pw.mini.mg1.layout;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import org.joml.Vector3fc;
import pl.edu.pw.mini.mg1.models.*;
import pl.edu.pw.mini.mg1.models.Point;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class ModelLayout implements Controller<Model> {
    private JSpinner rotationX;
    private JSpinner rotationY;
    private JSpinner rotationZ;
    private JSpinner scaleX;
    private JSpinner scaleY;
    private JSpinner scaleZ;
    private JSpinner positionX;
    private JSpinner positionY;
    private JSpinner positionZ;
    private JPanel mainPane;
    private JPanel specificFeaturesPane;
    private JLabel modelName;
    private Model model;

    public ModelLayout() {
        $$$setupUI$$$();
        scaleX.setModel(new SpinnerNumberModel(1, 0.01, 1000, 0.01));
        scaleY.setModel(new SpinnerNumberModel(1, 0.01, 1000, 0.01));
        scaleZ.setModel(new SpinnerNumberModel(1, 0.01, 1000, 0.01));
        positionX.setModel(new SpinnerNumberModel(0, -1000, 1000, 0.01));
        positionY.setModel(new SpinnerNumberModel(0, -1000, 1000, 0.01));
        positionZ.setModel(new SpinnerNumberModel(0, -1000, 1000, 0.01));
        rotationX.setModel(new SpinnerNumberModel(0, 0, 360, 1.0));
        rotationY.setModel(new SpinnerNumberModel(0, 0, 360, 1.0));
        rotationZ.setModel(new SpinnerNumberModel(0, 0, 360, 1.0));
        rotationX.addChangeListener(e -> {
            if (model != null) {
                Vector3fc rotation = model.getRotation();
                model.setRotation(((Number) rotationX.getValue()).floatValue(), rotation.y(), rotation.z());
            }
        });
        rotationY.addChangeListener(e -> {
            if (model != null) {
                Vector3fc rotation = model.getRotation();
                model.setRotation(rotation.x(), ((Number) rotationY.getValue()).floatValue(), rotation.z());
            }
        });
        rotationZ.addChangeListener(e -> {
            if (model != null) {
                Vector3fc rotation = model.getRotation();
                model.setRotation(rotation.x(), rotation.y(), ((Number) rotationZ.getValue()).floatValue());
            }
        });
        scaleX.addChangeListener(e -> {
            if (model != null) {
                Vector3fc scale = model.getScale();
                model.setScale(((Number) scaleX.getValue()).floatValue(), scale.y(), scale.z());
            }
        });
        scaleY.addChangeListener(e -> {
            if (model != null) {
                Vector3fc scale = model.getScale();
                model.setScale(scale.x(), ((Number) scaleY.getValue()).floatValue(), scale.z());
            }
        });
        scaleZ.addChangeListener(e -> {
            if (model != null) {
                Vector3fc scale = model.getScale();
                model.setScale(scale.x(), scale.y(), ((Number) scaleZ.getValue()).floatValue());
            }
        });
        positionX.addChangeListener(e -> {
            if (model != null) {
                Vector3fc position = model.getPosition();
                model.setPosition(((Number) positionX.getValue()).floatValue(), position.y(), position.z());
            }
        });
        positionY.addChangeListener(e -> {
            if (model != null) {
                Vector3fc position = model.getPosition();
                model.setPosition(position.x(), ((Number) positionY.getValue()).floatValue(), position.z());
            }
        });
        positionZ.addChangeListener(e -> {
            if (model != null) {
                Vector3fc position = model.getPosition();
                model.setPosition(position.x(), position.y(), ((Number) positionZ.getValue()).floatValue());
            }
        });
    }

    @Override
    public void set(Model model) {
        this.model = model;
        refresh();
    }

    @Override
    public void refresh() {
        specificFeaturesPane.removeAll();
        if (model != null) {
            String name = model.getName();
            if (name.length() > 16) {
                modelName.setToolTipText(name);
                name = name.substring(0, 15).concat("...");
            }
            modelName.setText(name);
            scaleX.setValue(model.getScale().x());
            scaleY.setValue(model.getScale().y());
            scaleZ.setValue(model.getScale().z());
            positionX.setValue(model.getPosition().x());
            positionY.setValue(model.getPosition().y());
            positionZ.setValue(model.getPosition().z());
            rotationX.setValue(model.getRotation().x());
            rotationY.setValue(model.getRotation().y());
            rotationZ.setValue(model.getRotation().z());
            if (model instanceof Torus) {
                TorusLayout layout = new TorusLayout();
                layout.set((Torus) model);
                specificFeaturesPane.add(layout.getMainPane());
            } else if (model instanceof Point) {
                specificFeaturesPane.add(new JPanel());
            } else if (model instanceof BezierC0) {
                BezierLayout layout = new BezierLayout();
                layout.set((BezierC0) model);
                specificFeaturesPane.add(layout.getMainPane());
            } else if (model instanceof BezierC2) {
                BezierC2Layout layout = new BezierC2Layout();
                layout.set((BezierC2) model);
                specificFeaturesPane.add(layout.getMainPane());
            } else if (model instanceof BezierInterC2) {
                InterpolationBezierC2Layout layout = new InterpolationBezierC2Layout();
                layout.set((BezierInterC2) model);
                specificFeaturesPane.add(layout.getMainPane());
            } else if (model instanceof BezierInter) {
                ChordInterpolationBezierC2Layout layout = new ChordInterpolationBezierC2Layout();
                layout.set((BezierInter) model);
                specificFeaturesPane.add(layout.getMainPane());
            } else if (model instanceof Patch) {
                PatchController layout = new PatchController();
                layout.set((Patch) model);
                specificFeaturesPane.add(layout.getMainPane());
            } else if (model instanceof IntersectionCurve) {
                IntersectionCurveController layout = new IntersectionCurveController();
                layout.set((IntersectionCurve) model);
                specificFeaturesPane.add(layout.getMainPane());
            }
        } else {
            modelName.setText(null);
            modelName.setToolTipText(null);
        }
        boolean enabled = model != null;
        scaleX.setEnabled(enabled);
        scaleY.setEnabled(enabled);
        scaleZ.setEnabled(enabled);
        positionX.setEnabled(enabled);
        positionY.setEnabled(enabled);
        positionZ.setEnabled(enabled);
        rotationX.setEnabled(enabled);
        rotationY.setEnabled(enabled);
        rotationZ.setEnabled(enabled);
        specificFeaturesPane.revalidate();
    }

    @Override
    public Container getMainPane() {
        return mainPane;
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        mainPane = new javax.swing.JPanel();
        mainPane.setLayout(new GridLayoutManager(5, 2, new java.awt.Insets(0, 0, 0, 0), -1, -1));
        final javax.swing.JPanel panel1 = new javax.swing.JPanel();
        panel1.setLayout(new GridLayoutManager(3, 2, new java.awt.Insets(0, 5, 0, 5), -1, -1));
        mainPane.add(panel1, new GridConstraints(2, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Rotation", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));
        rotationX = new javax.swing.JSpinner();
        panel1.add(rotationX, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        rotationY = new javax.swing.JSpinner();
        panel1.add(rotationY, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        rotationZ = new javax.swing.JSpinner();
        panel1.add(rotationZ, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final javax.swing.JLabel label1 = new javax.swing.JLabel();
        panel1.add(label1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, 1, null, null, null, 0, false));
        final javax.swing.JLabel label2 = new javax.swing.JLabel();
        panel1.add(label2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, 1, null, null, null, 0, false));
        final javax.swing.JLabel label3 = new javax.swing.JLabel();
        panel1.add(label3, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, 1, null, null, null, 0, false));
        final javax.swing.JPanel panel2 = new javax.swing.JPanel();
        panel2.setLayout(new GridLayoutManager(3, 2, new java.awt.Insets(0, 5, 0, 5), -1, -1));
        mainPane.add(panel2, new GridConstraints(1, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Position", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));
        final javax.swing.JLabel label4 = new javax.swing.JLabel();
        panel2.add(label4, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final javax.swing.JLabel label5 = new javax.swing.JLabel();
        panel2.add(label5, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final javax.swing.JLabel label6 = new javax.swing.JLabel();
        panel2.add(label6, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        positionX = new javax.swing.JSpinner();
        panel2.add(positionX, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        positionY = new javax.swing.JSpinner();
        panel2.add(positionY, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        positionZ = new javax.swing.JSpinner();
        panel2.add(positionZ, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final javax.swing.JPanel panel3 = new javax.swing.JPanel();
        panel3.setLayout(new GridLayoutManager(3, 2, new java.awt.Insets(0, 5, 0, 5), -1, -1));
        mainPane.add(panel3, new GridConstraints(3, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel3.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Scale", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));
        final javax.swing.JLabel label7 = new javax.swing.JLabel();
        panel3.add(label7, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        scaleX = new javax.swing.JSpinner();
        panel3.add(scaleX, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final javax.swing.JLabel label8 = new javax.swing.JLabel();
        panel3.add(label8, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        scaleY = new javax.swing.JSpinner();
        panel3.add(scaleY, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final javax.swing.JLabel label9 = new javax.swing.JLabel();
        panel3.add(label9, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        scaleZ = new javax.swing.JSpinner();
        panel3.add(scaleZ, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        specificFeaturesPane = new javax.swing.JPanel();
        specificFeaturesPane.setLayout(new java.awt.BorderLayout(0, 0));
        mainPane.add(specificFeaturesPane, new GridConstraints(4, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        modelName = new javax.swing.JLabel();
        mainPane.add(modelName, new GridConstraints(0, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, 1, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public javax.swing.JComponent $$$getRootComponent$$$() {
        return mainPane;
    }

}
