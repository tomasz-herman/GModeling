package pl.edu.pw.mini.mg1.layout;

import com.hermant.swing.WindowBuilder;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import org.joml.Vector3f;
import org.joml.Vector4f;
import pl.edu.pw.mini.mg1.models.*;
import pl.edu.pw.mini.mg1.numerics.IntersectionStart;
import pl.edu.pw.mini.mg1.numerics.Newton;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedList;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class IntersectionWizard {
    private final Consumer<Model> addModel;
    private final Consumer<Vector3f> setPointerPos;
    private final Supplier<Vector3f> getPointerPos;

    private JPanel mainPane;
    private JButton findButton;
    private JButton cancelButton;
    private JSpinner stepSpinner;
    private JCheckBox nearPointerCheckbox;

    public IntersectionWizard(Intersectable P, Intersectable Q, Consumer<Vector3f> setPointerPos, Supplier<Vector3f> getPointerPos, Consumer<Model> addModel) {
        $$$setupUI$$$();
        this.setPointerPos = setPointerPos;
        this.getPointerPos = getPointerPos;
        this.addModel = addModel;
        JDialog dialog = new WindowBuilder()
                .setContentPane(mainPane)
                .setDisposeOnClose()
                .setSize(300, 200)
                .buildDialog();
        stepSpinner.setModel(new SpinnerNumberModel(0.01, 0.0001, 10, 0.001));
        cancelButton.addActionListener(e -> dialog.dispose());
        findButton.addActionListener(e -> findIntersection(P, Q));
    }

    public float getStep() {
        return ((Number) stepSpinner.getValue()).floatValue();
    }

    public boolean getNearPointer() {
        return nearPointerCheckbox.isSelected();
    }

    public void findIntersection(Intersectable P, Intersectable Q) {
        IntersectionStart start = new IntersectionStart(P::P, Q::P, P == Q);
        Vector4f s = start.solve(getNearPointer() ? getPointerPos.get() : null);
        if (s == null) return;
        boolean pWrapsU = P.wrapsU();
        boolean pWrapsV = P.wrapsV();
        boolean qWrapsU = Q.wrapsU();
        boolean qWrapsV = Q.wrapsV();
        Vector3f found = P.P(s.x, s.y);
        setPointerPos.accept(found);
        Vector4f next = new Vector4f(s);
        Function<Float, Float> wrap = val -> val < 0 ? val + 1 : val > 1 ? val - 1 : val;
        LinkedList<Vector4f> parameters = new LinkedList<>();
        parameters.addLast(s);
        int i = 1000;
        while (i-- > 0) {
            Newton newton = new Newton(P::P, Q::P, P::N, Q::N, next, getStep(), 100);
            next = newton.solve();
            parameters.addLast(next);
            if (!pWrapsU && (next.x > 1 || next.x < 0)) break;
            else next.x = wrap.apply(next.x);
            if (!pWrapsV && (next.y > 1 || next.y < 0)) break;
            else next.y = wrap.apply(next.y);
            if (!qWrapsU && (next.z > 1 || next.z < 0)) break;
            else next.z = wrap.apply(next.z);
            if (!qWrapsV && (next.w > 1 || next.w < 0)) break;
            else next.w = wrap.apply(next.w);
            if (found.distance(P.P(next.x, next.y)) < 0.005f) {
                parameters.addLast(s);
                addModel.accept(new IntersectionCurve(parameters, P, Q));
                return;
            }
        }
        i = 1000;
        next = new Vector4f(s);
        while (i-- > 0) {
            Newton newton = new Newton(P::P, Q::P, P::N, Q::N, next, -getStep(), 100);
            next = newton.solve();
            parameters.addFirst(next);
            if (!pWrapsU && (next.x > 1 || next.x < 0)) break;
            else next.x = wrap.apply(next.x);
            if (!pWrapsV && (next.y > 1 || next.y < 0)) break;
            else next.y = wrap.apply(next.y);
            if (!qWrapsU && (next.z > 1 || next.z < 0)) break;
            else next.z = wrap.apply(next.z);
            if (!qWrapsV && (next.w > 1 || next.w < 0)) break;
            else next.w = wrap.apply(next.w);
        }
        addModel.accept(new IntersectionCurve(parameters, P, Q));
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
        mainPane.setLayout(new GridLayoutManager(4, 1, new Insets(5, 5, 5, 5), -1, -1));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        mainPane.add(panel1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("d");
        panel1.add(label1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        stepSpinner = new JSpinner();
        panel1.add(stepSpinner, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        mainPane.add(spacer1, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        mainPane.add(panel2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("near pointer");
        panel2.add(label2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        nearPointerCheckbox = new JCheckBox();
        nearPointerCheckbox.setText("");
        panel2.add(nearPointerCheckbox, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1));
        mainPane.add(panel3, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        findButton = new JButton();
        findButton.setText("Find");
        panel3.add(findButton, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        panel3.add(spacer2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        cancelButton = new JButton();
        cancelButton.setText("Cancel");
        panel3.add(cancelButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPane;
    }

}
