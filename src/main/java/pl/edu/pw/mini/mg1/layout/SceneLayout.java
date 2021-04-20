package pl.edu.pw.mini.mg1.layout;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import org.joml.Vector3fc;
import pl.edu.pw.mini.mg1.models.*;
import pl.edu.pw.mini.mg1.models.Point;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static pl.edu.pw.mini.mg1.layout.MainLayout.createSlider;

public class SceneLayout implements Controller<Scene> {
    private JTable table;
    private JPanel mainPane;
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
    private JComboBox<String> deleteCombo;
    private Scene scene;
    private Controller<Scene> pointerController;
    private Controller<Model> modelController;

    public SceneLayout() {
        $$$setupUI$$$();
        TableModel model = new SceneTableModel();
        table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        table.setModel(model);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                var indices = table.getSelectionModel().getSelectedIndices();
                scene.selectModels(indices);
                if (indices.length == 1) {
                    modelController.set(scene.getSelectedModels().stream().findFirst().orElse(null));
                } else {
                    modelController.set(null);
                }
            }
        });
        deleteCombo.setRenderer(new PromptComboBoxRenderer("Delete"));
        deleteCombo.setSelectedIndex(-1);
        deleteCombo.setPrototypeDisplayValue("XXXXXX");
        deleteCombo.addPopupMenuListener(new BoundsPopupMenuListener(true, false));
        deleteCombo.addActionListener(e -> {
            switch ((String) Objects.requireNonNull(deleteCombo.getSelectedItem())) {
                case "Objects" -> {
                    scene.deleteSelected();
                    table.getSelectionModel().clearSelection();
                    table.revalidate();
                }
                case "Points from curve" -> {
                    List<Model> selected = scene.getSelectedModels();
                    List<Curve> curves = selected.stream()
                            .filter(c -> c instanceof Curve)
                            .map(c -> (Curve) c)
                            .collect(Collectors.toList());
                    List<Point> points = selected.stream()
                            .filter(p -> p instanceof Point)
                            .map(p -> (Point) p)
                            .collect(Collectors.toList());
                    for (Curve curve : curves) {
                        for (Point point : points) {
                            curve.removePoint(point);
                        }
                    }
                }
            }
            deleteCombo.setSelectedIndex(-1);
        });
        addCombo.setRenderer(new PromptComboBoxRenderer("Add"));
        addCombo.setSelectedIndex(-1);
        addCombo.setPrototypeDisplayValue("XXXXXX");
        addCombo.addPopupMenuListener(new BoundsPopupMenuListener(true, false));
        addCombo.addActionListener(e -> {
            switch ((String) Objects.requireNonNull(addCombo.getSelectedItem())) {
                case "Point" -> {
                    Point p = new Point();
                    scene.getSelectedModels().stream()
                            .filter(c -> c instanceof Curve)
                            .map(c -> (Curve) c)
                            .forEach(c -> c.addPoint(p));
                    scene.addModel(p);
                }
                case "Torus" -> scene.addModel(new Torus());
                case "BezierC0" -> {
                    List<Point> points = scene.getSelectedModels().stream()
                            .filter(p -> p instanceof Point)
                            .map(p -> (Point) p)
                            .collect(Collectors.toList());
                    if (points.size() == 0) break;
                    scene.addModel(new BezierC0(points));
                }
                case "BezierC2" -> {
                    List<Point> points = scene.getSelectedModels().stream()
                            .filter(p -> p instanceof Point)
                            .map(p -> (Point) p)
                            .collect(Collectors.toList());
                    if (points.size() == 0) break;
                    scene.addModel(new BezierC2(points));
                }
                case "Points to curve" -> {
                    List<Model> selected = scene.getSelectedModels();
                    List<Curve> curves = selected.stream()
                            .filter(c -> c instanceof Curve)
                            .map(c -> (Curve) c)
                            .collect(Collectors.toList());
                    List<Point> points = selected.stream()
                            .filter(p -> p instanceof Point)
                            .map(p -> (Point) p)
                            .collect(Collectors.toList());
                    for (Curve curve : curves) {
                        for (Point point : points) {
                            curve.addPoint(point);
                        }
                    }
                }
            }
            addCombo.setSelectedIndex(-1);
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
                scene.setRotation(xRotation.getValue() / 100f, rotation.y(), rotation.z());
            }
        });
        yRotation.addChangeListener(e -> {
            if (scene != null) {
                Vector3fc rotation = scene.getRotation();
                scene.setRotation(rotation.x(), yRotation.getValue() / 100f, rotation.z());
            }
        });
        zRotation.addChangeListener(e -> {
            if (scene != null) {
                Vector3fc rotation = scene.getRotation();
                scene.setRotation(rotation.x(), rotation.y(), zRotation.getValue() / 100f);
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
            selection.addSelectionInterval(i, i);
        }
        if (scene != null) {
            xScale.setValue(scene.getScale().x());
            yScale.setValue(scene.getScale().y());
            zScale.setValue(scene.getScale().z());
            xTranslation.setValue(scene.getTranslation().x());
            yTranslation.setValue(scene.getTranslation().y());
            zTranslation.setValue(scene.getTranslation().z());
            xRotation.setValue((int) scene.getRotation().x() * 100);
            yRotation.setValue((int) scene.getRotation().y() * 100);
            zRotation.setValue((int) scene.getRotation().z() * 100);
        }
        table.revalidate();
    }

    public void setPointerController(Controller<Scene> pointerController) {
        this.pointerController = pointerController;
        pointerController.set(scene);
        pointerControllerPane.removeAll();
        pointerControllerPane.add(pointerController.getMainPane(), BorderLayout.CENTER);
    }

    public void setModelController(Controller<Model> modelController) {
        this.modelController = modelController;
    }

    private void createUIComponents() {
        xRotation = createSlider();
        yRotation = createSlider();
        zRotation = createSlider();
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
            if (modelController != null) modelController.refresh();
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

    private static class BoundsPopupMenuListener implements PopupMenuListener {
        private boolean scrollBarRequired = true;
        private boolean popupWider;
        private int maximumWidth = -1;
        private boolean popupAbove;
        private JScrollPane scrollPane;

        /**
         * Convenience constructore to allow the display of a horizontal scrollbar
         * when required.
         */
        public BoundsPopupMenuListener() {
            this(true, false, -1, false);
        }

        /**
         * Convenience constructor that allows you to display the popup
         * wider and/or above the combo box.
         *
         * @param popupWider when true, popup width is based on the popup
         *                   preferred width
         * @param popupAbove when true, popup is displayed above the combobox
         */
        public BoundsPopupMenuListener(boolean popupWider, boolean popupAbove) {
            this(true, popupWider, -1, popupAbove);
        }

        /**
         * Convenience constructor that allows you to display the popup
         * wider than the combo box and to specify the maximum width
         *
         * @param maximumWidth the maximum width of the popup. The
         *                     popupAbove value is set to "true".
         */
        public BoundsPopupMenuListener(int maximumWidth) {
            this(true, true, maximumWidth, false);
        }

        /**
         * General purpose constructor to set all popup properties at once.
         *
         * @param scrollBarRequired display a horizontal scrollbar when the
         *                          preferred width of popup is greater than width of scrollPane.
         * @param popupWider        display the popup at its preferred with
         * @param maximumWidth      limit the popup width to the value specified
         *                          (minimum size will be the width of the combo box)
         * @param popupAbove        display the popup above the combo box
         */
        public BoundsPopupMenuListener(
                boolean scrollBarRequired, boolean popupWider, int maximumWidth, boolean popupAbove) {
            setScrollBarRequired(scrollBarRequired);
            setPopupWider(popupWider);
            setMaximumWidth(maximumWidth);
            setPopupAbove(popupAbove);
        }

        /**
         * Return the maximum width of the popup.
         *
         * @return the maximumWidth value
         */
        public int getMaximumWidth() {
            return maximumWidth;
        }

        /**
         * Set the maximum width for the popup. This value is only used when
         * setPopupWider( true ) has been specified. A value of -1 indicates
         * that there is no maximum.
         *
         * @param maximumWidth the maximum width of the popup
         */
        public void setMaximumWidth(int maximumWidth) {
            this.maximumWidth = maximumWidth;
        }

        /**
         * Determine if the popup should be displayed above the combo box.
         *
         * @return the popupAbove value
         */
        public boolean isPopupAbove() {
            return popupAbove;
        }

        /**
         * Change the location of the popup relative to the combo box.
         *
         * @param popupAbove true display popup above the combo box,
         *                   false display popup below the combo box.
         */
        public void setPopupAbove(boolean popupAbove) {
            this.popupAbove = popupAbove;
        }

        /**
         * Determine if the popup might be displayed wider than the combo box
         *
         * @return the popupWider value
         */
        public boolean isPopupWider() {
            return popupWider;
        }

        /**
         * Change the width of the popup to be the greater of the width of the
         * combo box or the preferred width of the popup. Normally the popup width
         * is always the same size as the combo box width.
         *
         * @param popupWider true adjust the width as required.
         */
        public void setPopupWider(boolean popupWider) {
            this.popupWider = popupWider;
        }

        /**
         * Determine if the horizontal scroll bar might be required for the popup
         *
         * @return the scrollBarRequired value
         */
        public boolean isScrollBarRequired() {
            return scrollBarRequired;
        }

        /**
         * For some reason the default implementation of the popup removes the
         * horizontal scrollBar from the popup scroll pane which can result in
         * the truncation of the rendered items in the popop. Adding a scrollBar
         * back to the scrollPane will allow horizontal scrolling if necessary.
         *
         * @param scrollBarRequired true add horizontal scrollBar to scrollPane
         *                          false remove the horizontal scrollBar
         */
        public void setScrollBarRequired(boolean scrollBarRequired) {
            this.scrollBarRequired = scrollBarRequired;
        }

        /**
         * Alter the bounds of the popup just before it is made visible.
         */
        @Override
        public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
            @SuppressWarnings("rawtypes") JComboBox comboBox = (JComboBox) e.getSource();

            if (comboBox.getItemCount() == 0) return;

            final Object child = comboBox.getAccessibleContext().getAccessibleChild(0);

            if (child instanceof BasicComboPopup)
                SwingUtilities.invokeLater(() -> customizePopup((BasicComboPopup) child));
        }

        protected void customizePopup(BasicComboPopup popup) {
            scrollPane = getScrollPane(popup);

            if (popupWider)
                popupWider(popup);

            checkHorizontalScrollBar(popup);

            //  For some reason in JDK7 the popup will not display at its preferred
            //  width unless its location has been changed from its default
            //  (ie. for normal "pop down" shift the popup and reset)

            Component comboBox = popup.getInvoker();
            java.awt.Point location = comboBox.getLocationOnScreen();

            if (popupAbove) {
                int height = popup.getPreferredSize().height;
                popup.setLocation(location.x, location.y - height);
            } else {
                int height = comboBox.getPreferredSize().height;
                popup.setLocation(location.x, location.y + height - 1);
                popup.setLocation(location.x, location.y + height);
            }
        }

        /*
         *  Adjust the width of the scrollpane used by the popup
         */
        protected void popupWider(BasicComboPopup popup) {
            @SuppressWarnings("rawtypes") JList list = popup.getList();

            //  Determine the maximimum width to use:
            //  a) determine the popup preferred width
            //  b) limit width to the maximum if specified
            //  c) ensure width is not less than the scroll pane width

            int popupWidth = list.getPreferredSize().width
                    + 5  // make sure horizontal scrollbar doesn't appear
                    + getScrollBarWidth(popup, scrollPane);

            if (maximumWidth != -1) {
                popupWidth = Math.min(popupWidth, maximumWidth);
            }

            Dimension scrollPaneSize = scrollPane.getPreferredSize();
            popupWidth = Math.max(popupWidth, scrollPaneSize.width);

            //  Adjust the width

            scrollPaneSize.width = popupWidth;
            scrollPane.setPreferredSize(scrollPaneSize);
            scrollPane.setMaximumSize(scrollPaneSize);
        }

        /*
         *  This method is called every time:
         *  - to make sure the viewport is returned to its default position
         *  - to remove the horizontal scrollbar when it is not wanted
         */
        private void checkHorizontalScrollBar(BasicComboPopup popup) {
            //  Reset the viewport to the left

            JViewport viewport = scrollPane.getViewport();
            java.awt.Point p = viewport.getViewPosition();
            p.x = 0;
            viewport.setViewPosition(p);

            //  Remove the scrollbar so it is never painted

            if (!scrollBarRequired) {
                scrollPane.setHorizontalScrollBar(null);
                return;
            }

            //	Make sure a horizontal scrollbar exists in the scrollpane

            JScrollBar horizontal = scrollPane.getHorizontalScrollBar();

            if (horizontal == null) {
                horizontal = new JScrollBar(JScrollBar.HORIZONTAL);
                scrollPane.setHorizontalScrollBar(horizontal);
                scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            }

            //	Potentially increase height of scroll pane to display the scrollbar

            if (horizontalScrollBarWillBeVisible(popup, scrollPane)) {
                Dimension scrollPaneSize = scrollPane.getPreferredSize();
                scrollPaneSize.height += horizontal.getPreferredSize().height;
                scrollPane.setPreferredSize(scrollPaneSize);
                scrollPane.setMaximumSize(scrollPaneSize);
                scrollPane.revalidate();
            }
        }

        /*
         *  Get the scroll pane used by the popup so its bounds can be adjusted
         */
        protected JScrollPane getScrollPane(BasicComboPopup popup) {
            @SuppressWarnings("rawtypes") JList list = popup.getList();
            Container c = SwingUtilities.getAncestorOfClass(JScrollPane.class, list);

            return (JScrollPane) c;
        }

        /*
         *  I can't find any property on the scrollBar to determine if it will be
         *  displayed or not so use brute force to determine this.
         */
        protected int getScrollBarWidth(BasicComboPopup popup, JScrollPane scrollPane) {
            int scrollBarWidth = 0;
            @SuppressWarnings("rawtypes") JComboBox comboBox = (JComboBox) popup.getInvoker();

            if (comboBox.getItemCount() > comboBox.getMaximumRowCount()) {
                JScrollBar vertical = scrollPane.getVerticalScrollBar();
                scrollBarWidth = vertical.getPreferredSize().width;
            }

            return scrollBarWidth;
        }

        /*
         *  I can't find any property on the scrollBar to determine if it will be
         *  displayed or not so use brute force to determine this.
         */
        protected boolean horizontalScrollBarWillBeVisible(BasicComboPopup popup, JScrollPane scrollPane) {
            @SuppressWarnings("rawtypes") JList list = popup.getList();
            int scrollBarWidth = getScrollBarWidth(popup, scrollPane);
            int popupWidth = list.getPreferredSize().width + scrollBarWidth;

            return popupWidth > scrollPane.getPreferredSize().width;
        }

        @Override
        public void popupMenuCanceled(PopupMenuEvent e) {
        }

        @Override
        public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
            //  In its normal state the scrollpane does not have a scrollbar

            if (scrollPane != null) {
                scrollPane.setHorizontalScrollBar(null);
            }
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
        createUIComponents();
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
        final Spacer spacer2 = new Spacer();
        panel1.add(spacer2, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        addCombo = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel1 = new DefaultComboBoxModel();
        defaultComboBoxModel1.addElement("Point");
        defaultComboBoxModel1.addElement("Torus");
        defaultComboBoxModel1.addElement("BezierC0");
        defaultComboBoxModel1.addElement("BezierC2");
        defaultComboBoxModel1.addElement("Points to curve");
        addCombo.setModel(defaultComboBoxModel1);
        panel1.add(addCombo, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        deleteCombo = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel2 = new DefaultComboBoxModel();
        defaultComboBoxModel2.addElement("Objects");
        defaultComboBoxModel2.addElement("Points from curve");
        deleteCombo.setModel(defaultComboBoxModel2);
        panel1.add(deleteCombo, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
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
        xRotation.setMaximum(36000);
        xRotation.setValue(0);
        panel5.add(xRotation, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        yRotation.setMaximum(36000);
        yRotation.setValue(0);
        panel5.add(yRotation, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        zRotation.setMaximum(36000);
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
