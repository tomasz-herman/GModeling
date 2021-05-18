package pl.edu.pw.mini.mg1.layout;

import com.hermant.swing.WindowBuilder;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import org.joml.Vector3f;
import pl.edu.pw.mini.mg1.models.BezierPatchC0;
import pl.edu.pw.mini.mg1.models.Model;
import pl.edu.pw.mini.mg1.models.Pointer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class PatchWizard {
    private JComboBox<String> patchTypeCombo;
    private JSpinner xSpinner;
    private JSpinner ySpinner;
    private JSpinner iSpinner;
    private JSpinner jSpinner;
    private JPanel mainPane;
    private JLabel xLabel;
    private JLabel yLabel;
    private JButton addPatchButton;
    private JSpinner dSpinner;
    private JCheckBox bCheckbox;

    private final Supplier<BezierPatchC0> flatSupplier = () -> BezierPatchC0.flat(
            ((Number) xSpinner.getValue()).floatValue(),
            ((Number) ySpinner.getValue()).floatValue(),
            ((Number) iSpinner.getValue()).intValue(),
            ((Number) jSpinner.getValue()).intValue());
    private final Supplier<BezierPatchC0> cylinderSupplier = () -> BezierPatchC0.cylinder(
            ((Number) xSpinner.getValue()).floatValue(),
            ((Number) ySpinner.getValue()).floatValue(),
            ((Number) iSpinner.getValue()).intValue(),
            ((Number) jSpinner.getValue()).intValue());

    private Supplier<BezierPatchC0> patchSupplier = flatSupplier;
    private BezierPatchC0 patch;

    private final Consumer<Model> addModel;
    private final Consumer<Model> removeModel;
    private final Supplier<Pointer> getPointer;
    private final Runnable refresh;

    public PatchWizard(Consumer<Model> addModel, Consumer<Model> removeModel, Supplier<Pointer> getPointer, Runnable refresh) {
        $$$setupUI$$$();

        this.addModel = addModel;
        this.removeModel = removeModel;
        this.getPointer = getPointer;
        this.refresh = refresh;
        JDialog dialog = new WindowBuilder()
                .setContentPane(mainPane)
                .setNothingOnClose()
                .setSize(180, 400)
                .buildDialog();
        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (patch != null && removeModel != null) removeModel.accept(patch);
                dialog.dispose();
                refresh.run();
            }
        });
        xSpinner.setModel(new SpinnerNumberModel(1, 0.01, 1000, 0.1));
        ySpinner.setModel(new SpinnerNumberModel(1, 0.01, 1000, 0.1));
        iSpinner.setModel(new SpinnerNumberModel(2, 1, 10, 1));
        jSpinner.setModel(new SpinnerNumberModel(2, 1, 10, 1));
        dSpinner.setModel(new SpinnerNumberModel(3, 2, 64, 1));

        xSpinner.addChangeListener(e -> replacePatch());
        ySpinner.addChangeListener(e -> replacePatch());
        iSpinner.addChangeListener(e -> replacePatch());
        jSpinner.addChangeListener(e -> replacePatch());
        dSpinner.addChangeListener(e -> patch.setDivisions(((Number) dSpinner.getValue()).intValue()));
        bCheckbox.addActionListener(e -> patch.setShowBezierMesh(bCheckbox.isSelected()));

        patchTypeCombo.addActionListener(e -> {
            switch ((String) Objects.requireNonNull(patchTypeCombo.getSelectedItem())) {
                case "Flat" -> {
                    patchSupplier = flatSupplier;
                    xLabel.setText("x");
                    yLabel.setText("y");
                    xSpinner.setValue(2);
                    ySpinner.setValue(2);
                    iSpinner.setValue(2);
                    jSpinner.setValue(2);
                }
                case "Cylinder" -> {
                    patchSupplier = cylinderSupplier;
                    xLabel.setText("r");
                    yLabel.setText("h");
                    xSpinner.setValue(1);
                    ySpinner.setValue(2);
                    iSpinner.setValue(5);
                    jSpinner.setValue(2);
                }
            }
            replacePatch();
        });

        addPatchButton.addActionListener(e -> {
            patch.getPoints().forEach(addModel);
            refresh.run();
            dialog.dispose();
        });

        replacePatch();
    }

    private void replacePatch() {
        if (patch != null) removeModel.accept(patch);
        addModel.accept(patch = patchSupplier.get());
        Vector3f pointer = getPointer.get().getPosition().get(new Vector3f());
        patch.getPoints().distinct().forEach(point -> point.move(pointer.x, pointer.y, pointer.z));
        patch.setDivisions(((Number) dSpinner.getValue()).intValue());
        patch.setShowBezierMesh(bCheckbox.isSelected());
        refresh.run();
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
        mainPane.setLayout(new GridLayoutManager(2, 1, new Insets(5, 5, 5, 5), -1, -1));
        patchTypeCombo = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel1 = new DefaultComboBoxModel();
        defaultComboBoxModel1.addElement("Flat");
        defaultComboBoxModel1.addElement("Cylinder");
        patchTypeCombo.setModel(defaultComboBoxModel1);
        mainPane.add(patchTypeCombo, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(8, 2, new Insets(0, 0, 0, 0), -1, -1));
        mainPane.add(panel1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        xLabel = new JLabel();
        xLabel.setText("x");
        panel1.add(xLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel1.add(spacer1, new GridConstraints(6, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        yLabel = new JLabel();
        yLabel.setText("y");
        panel1.add(yLabel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("i");
        panel1.add(label1, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("j");
        panel1.add(label2, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        xSpinner = new JSpinner();
        panel1.add(xSpinner, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        ySpinner = new JSpinner();
        panel1.add(ySpinner, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        iSpinner = new JSpinner();
        panel1.add(iSpinner, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        jSpinner = new JSpinner();
        panel1.add(jSpinner, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        addPatchButton = new JButton();
        addPatchButton.setText("Add Patch");
        panel1.add(addPatchButton, new GridConstraints(7, 1, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("d");
        panel1.add(label3, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        dSpinner = new JSpinner();
        panel1.add(dSpinner, new GridConstraints(4, 1, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label4 = new JLabel();
        label4.setText("b");
        panel1.add(label4, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        bCheckbox = new JCheckBox();
        bCheckbox.setText("");
        panel1.add(bCheckbox, new GridConstraints(5, 1, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPane;
    }

}
