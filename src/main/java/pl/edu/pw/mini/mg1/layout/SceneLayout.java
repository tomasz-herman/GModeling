package pl.edu.pw.mini.mg1.layout;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import pl.edu.pw.mini.mg1.models.Scene;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.util.Arrays;

public class SceneLayout implements Controller<Scene> {
    private JTable table;
    private JPanel mainPane;
    private Scene scene;

    public SceneLayout() {
        $$$setupUI$$$();
        TableModel model = new SceneTableModel();
        table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        table.setModel(model);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                var indices = table.getSelectionModel().getSelectedIndices();
                System.out.println(Arrays.toString(indices));
            } else {
                var indices = table.getSelectionModel().getSelectedIndices();
                System.err.println(Arrays.toString(indices));
            }
        });
    }

    @Override
    public void set(Scene scene) {
        this.scene = scene;
    }

    @Override
    public Container getMainPane() {
        return mainPane;
    }

    @Override
    public void refresh() {

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

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        mainPane = new JPanel();
        mainPane.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        final JScrollPane scrollPane1 = new JScrollPane();
        mainPane.add(scrollPane1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        table = new JTable();
        scrollPane1.setViewportView(table);
        final Spacer spacer1 = new Spacer();
        mainPane.add(spacer1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPane;
    }

}
