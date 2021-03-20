package pl.edu.pw.mini.mg1.layout;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import org.joml.Vector3fc;
import pl.edu.pw.mini.mg1.models.Point;
import pl.edu.pw.mini.mg1.models.Scene;
import pl.edu.pw.mini.mg1.models.Torus;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.util.Objects;

public class SceneLayout implements Controller<Scene> {
    private JTable table;
    private JPanel mainPane;
    private JButton deleteButton;
    private JComboBox<String> addCombo;
    private JPanel pointerControllerPane;
    private JRadioButton useLocalPointer;
    private JRadioButton useGlobalPointer;
    private JSlider xRotation;
    private JSlider yRotation;
    private JSlider zRotation;
    private JSpinner yScale;
    private JSpinner zScale;
    private JSpinner xScale;
    private JSpinner zTranslation;
    private JSpinner yTranslation;
    private JSpinner xTranslation;
    private Scene scene;
    private Controller<Scene> pointerController;

    public SceneLayout() {
        $$$setupUI$$$();
        TableModel model = new SceneTableModel();
        table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        table.setModel(model);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                var indices = table.getSelectionModel().getSelectedIndices();
                scene.selectModels(indices);
            }
        });
        addCombo.setRenderer(new PromptComboBoxRenderer("Add"));
        addCombo.setSelectedIndex(-1);
        addCombo.addActionListener(e -> {
            switch ((String) Objects.requireNonNull(addCombo.getSelectedItem())) {
                case "Point" -> scene.addModel(new Point());
                case "Torus" -> scene.addModel(new Torus());
            }
            addCombo.setSelectedIndex(-1);
            table.revalidate();
        });
        deleteButton.addActionListener(e -> {
            scene.deleteSelected();
            table.revalidate();
        });
        xScale.setModel(new SpinnerNumberModel(1, 0.01, 1000, 0.01));
        yScale.setModel(new SpinnerNumberModel(1, 0.01, 1000, 0.01));
        zScale.setModel(new SpinnerNumberModel(1, 0.01, 1000, 0.01));
        xTranslation.setModel(new SpinnerNumberModel(0, -1000, 1000, 0.01));
        yTranslation.setModel(new SpinnerNumberModel(0, -1000, 1000, 0.01));
        zTranslation.setModel(new SpinnerNumberModel(0, -1000, 1000, 0.01));
        xRotation.addChangeListener(e -> {
            if (scene != null) {
                Vector3fc rotation = scene.getRotation();
                scene.setRotation(xRotation.getValue(), rotation.y(), rotation.z());
            }
        });
        yRotation.addChangeListener(e -> {
            if (scene != null) {
                Vector3fc rotation = scene.getRotation();
                scene.setRotation(rotation.x(), yRotation.getValue(), rotation.z());
            }
        });
        zRotation.addChangeListener(e -> {
            if (scene != null) {
                Vector3fc rotation = scene.getRotation();
                scene.setRotation(rotation.x(), rotation.y(), zRotation.getValue());
            }
        });
        xScale.addChangeListener(e -> {
            if (scene != null) {
                Vector3fc scale = scene.getScale();
                scene.setScale(((Number) xScale.getValue()).floatValue(), scale.y(), scale.z());
            }
        });
        yScale.addChangeListener(e -> {
            if (scene != null) {
                Vector3fc scale = scene.getScale();
                scene.setScale(scale.x(), ((Number) yScale.getValue()).floatValue(), scale.z());
            }
        });
        zScale.addChangeListener(e -> {
            if (scene != null) {
                Vector3fc scale = scene.getScale();
                scene.setScale(scale.x(), scale.y(), ((Number) zScale.getValue()).floatValue());
            }
        });
        xTranslation.addChangeListener(e -> {
            if (scene != null) {
                Vector3fc position = scene.getTranslation();
                scene.setTranslation(((Number) xTranslation.getValue()).floatValue(), position.y(), position.z());
            }
        });
        yTranslation.addChangeListener(e -> {
            if (scene != null) {
                Vector3fc position = scene.getTranslation();
                scene.setTranslation(position.x(), ((Number) yTranslation.getValue()).floatValue(), position.z());
            }
        });
        zTranslation.addChangeListener(e -> {
            if (scene != null) {
                Vector3fc position = scene.getTranslation();
                scene.setTranslation(position.x(), position.y(), ((Number) zTranslation.getValue()).floatValue());
            }
        });
        ButtonGroup group = new ButtonGroup();
        group.add(useLocalPointer);
        group.add(useGlobalPointer);
        useGlobalPointer.addActionListener(e -> {
            scene.setTransformationCenter(true);
            refresh();
        });
        useLocalPointer.addActionListener(e -> {
            scene.setTransformationCenter(false);
            refresh();
        });
    }

    @Override
    public void set(Scene scene) {
        this.scene = scene;
        if (pointerController != null) {
            pointerController.set(scene);
        }
    }

    @Override
    public Container getMainPane() {
        return mainPane;
    }

    @Override
    public void refresh() {
        ListSelectionModel selection = table.getSelectionModel();
        int[] selected = scene.getSelected();
        selection.clearSelection();
        for (int i : selected) {
            selection.addSelectionInterval(i, i);
        }
        if (scene != null) {
            xScale.setValue(scene.getScale().x());
            yScale.setValue(scene.getScale().y());
            zScale.setValue(scene.getScale().z());
            xTranslation.setValue(scene.getTranslation().x());
            yTranslation.setValue(scene.getTranslation().y());
            zTranslation.setValue(scene.getTranslation().z());
            xRotation.setValue((int) scene.getRotation().x());
            yRotation.setValue((int) scene.getRotation().y());
            zRotation.setValue((int) scene.getRotation().z());
        }
    }

    public void setPointerController(Controller<Scene> pointerController) {
        this.pointerController = pointerController;
        pointerController.set(scene);
        pointerControllerPane.removeAll();
        pointerControllerPane.add(pointerController.getMainPane(), BorderLayout.CENTER);
    }

    private final class SceneTableModel extends AbstractTableModel {
        @Override
        public int getRowCount() {
            if (scene == null) return 0;
            else return scene.getModels().size();
        }

        @Override
        public int getColumnCount() {
            return 1;
        }

        @Override
        public String getColumnName(int columnIndex) {
            return "Models";
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return true;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            return scene.getModels().get(rowIndex).getName();
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            scene.getModels().get(rowIndex).setName(aValue.toString());
        }
    }

    private static class PromptComboBoxRenderer extends BasicComboBoxRenderer {
        private final String prompt;

        public PromptComboBoxRenderer(String prompt) {
            this.prompt = prompt;
        }

        public Component getListCellRendererComponent(
                JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value == null) setText(prompt);
            return this;
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
        mainPane = new JPanel();
        mainPane.setLayout(new GridLayoutManager(5, 1, new Insets(0, 0, 0, 0), -1, -1));
        final JScrollPane scrollPane1 = new JScrollPane();
        mainPane.add(scrollPane1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(-1, 200), null, 0, false));
        table = new JTable();
        scrollPane1.setViewportView(table);
        final Spacer spacer1 = new Spacer();
        mainPane.add(spacer1, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1));
        mainPane.add(panel1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        deleteButton = new JButton();
        deleteButton.setText("Delete");
        panel1.add(deleteButton, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        panel1.add(spacer2, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        addCombo = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel1 = new DefaultComboBoxModel();
        defaultComboBoxModel1.addElement("Point");
        defaultComboBoxModel1.addElement("Torus");
        addCombo.setModel(defaultComboBoxModel1);
        panel1.add(addCombo, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        pointerControllerPane = new JPanel();
        pointerControllerPane.setLayout(new BorderLayout(0, 0));
        mainPane.add(pointerControllerPane, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        pointerControllerPane.setBorder(BorderFactory.createTitledBorder(null, "pointer", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(2, 2, new Insets(0, 0, 0, 0), -1, -1));
        mainPane.add(panel2, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel2.setBorder(BorderFactory.createTitledBorder(null, "transformation", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        useLocalPointer = new JRadioButton();
        useLocalPointer.setText("local");
        panel2.add(useLocalPointer, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        useGlobalPointer = new JRadioButton();
        useGlobalPointer.setSelected(true);
        useGlobalPointer.setText("global");
        panel2.add(useGlobalPointer, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(3, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel2.add(panel3, new GridConstraints(1, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridLayoutManager(3, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel3.add(panel4, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel4.setBorder(BorderFactory.createTitledBorder(null, "translation", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JLabel label1 = new JLabel();
        label1.setText("x");
        panel4.add(label1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("y");
        panel4.add(label2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("z");
        panel4.add(label3, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        zTranslation = new JSpinner();
        panel4.add(zTranslation, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        yTranslation = new JSpinner();
        panel4.add(yTranslation, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        xTranslation = new JSpinner();
        panel4.add(xTranslation, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new GridLayoutManager(3, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel3.add(panel5, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel5.setBorder(BorderFactory.createTitledBorder(null, "rotation", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JLabel label4 = new JLabel();
        label4.setText("x");
        panel5.add(label4, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label5 = new JLabel();
        label5.setText("y");
        panel5.add(label5, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label6 = new JLabel();
        label6.setText("z");
        panel5.add(label6, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        xRotation = new JSlider();
        xRotation.setMaximum(360);
        xRotation.setValue(0);
        panel5.add(xRotation, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        yRotation = new JSlider();
        yRotation.setMaximum(360);
        yRotation.setValue(0);
        panel5.add(yRotation, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        zRotation = new JSlider();
        zRotation.setMaximum(360);
        zRotation.setValue(0);
        panel5.add(zRotation, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel6 = new JPanel();
        panel6.setLayout(new GridLayoutManager(3, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel3.add(panel6, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel6.setBorder(BorderFactory.createTitledBorder(null, "scale", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JLabel label7 = new JLabel();
        label7.setText("x");
        panel6.add(label7, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        xScale = new JSpinner();
        panel6.add(xScale, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label8 = new JLabel();
        label8.setText("y");
        panel6.add(label8, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label9 = new JLabel();
        label9.setText("z");
        panel6.add(label9, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        yScale = new JSpinner();
        panel6.add(yScale, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        zScale = new JSpinner();
        panel6.add(zScale, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPane;
    }

}
