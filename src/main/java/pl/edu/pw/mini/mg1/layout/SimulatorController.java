package pl.edu.pw.mini.mg1.layout;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import org.apache.commons.io.FilenameUtils;
import org.joml.Vector2f;
import org.joml.Vector2i;
import pl.edu.pw.mini.mg1.milling.MaterialBlock;
import pl.edu.pw.mini.mg1.milling.MillingTool;
import pl.edu.pw.mini.mg1.milling.Path;
import pl.edu.pw.mini.mg1.models.MillingSimulator;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class SimulatorController implements Controller<MillingSimulator> {
    private JPanel mainPane;
    private JButton loadButton;
    private JCheckBox showPath;
    private JCheckBox cutterRound;
    private JCheckBox showCutter;
    private JSpinner cutterRadius;
    private JSpinner cutterLength;
    private JCheckBox showBlock;
    private JSpinner blockSizeX;
    private JSpinner blockSizeY;
    private JSpinner blockResX;
    private JSpinner blockResY;
    private JSpinner blockH;
    private JSpinner blockMinH;
    private JButton simulateButton;
    private JProgressBar progressBar;
    private JCheckBox realtimeCheckBox;
    private JPanel blockPane;
    private JPanel pathPane;
    private JPanel cutterPane;
    private JLabel pathName;
    private MillingSimulator simulator;

    private final JFileChooser fileChooser = new JFileChooser(".");

    public SimulatorController() {
        $$$setupUI$$$();
        blockSizeX.setModel(new SpinnerNumberModel(180, 1, 10000, 1));
        blockSizeY.setModel(new SpinnerNumberModel(180, 1, 10000, 1));
        blockResX.setModel(new SpinnerNumberModel(256, 16, 1024 * 8, 64));
        blockResY.setModel(new SpinnerNumberModel(256, 16, 1024 * 8, 64));
        blockH.setModel(new SpinnerNumberModel(50, 0, 1000, 0.1));
        blockMinH.setModel(new SpinnerNumberModel(15, 0, 1000, 0.1));
        cutterRadius.setModel(new SpinnerNumberModel(5, 0, 100, 0.1));
        cutterLength.setModel(new SpinnerNumberModel(25, 0, 1000, 0.1));
        blockSizeX.addChangeListener(e -> simulator.setBlock(newBlock()));
        blockSizeY.addChangeListener(e -> simulator.setBlock(newBlock()));
        blockResX.addChangeListener(e -> simulator.setBlock(newBlock()));
        blockResY.addChangeListener(e -> simulator.setBlock(newBlock()));
        blockH.addChangeListener(e -> simulator.setBlock(newBlock()));
        blockMinH.addChangeListener(e -> simulator.setBlock(newBlock()));
        showBlock.addChangeListener(e -> simulator.setShowBlock(showBlock.isSelected()));
        cutterRadius.addChangeListener(e -> simulator.setTool(newCutter()));
        cutterLength.addChangeListener(e -> simulator.setTool(newCutter()));
        cutterRound.addChangeListener(e -> simulator.setTool(newCutter()));
        showCutter.addChangeListener(e -> simulator.setShowCutter(showCutter.isSelected()));
        loadButton.addActionListener(e -> {
            if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                String name = file.getName();
                String ext = FilenameUtils.getExtension(name);
                if (ext.matches("[fk]\\d+")) {
                    cutterRound.setSelected(ext.startsWith("k"));
                    int r = Integer.parseInt(ext.substring(1));
                    if (r > 0) cutterRadius.setValue(r);
                    simulator.setTool(newCutter());
                }
                try {
                    Path path = new Path(new FileInputStream(file), 5);
                    simulator.setPath(path);
                    pathName.setText(name);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        showPath.addChangeListener(e -> simulator.setShowPath(showPath.isSelected()));
        simulateButton.addActionListener(e -> simulator.simulate(progressBar::setValue, this::disablePanels));
        realtimeCheckBox.addChangeListener(e -> simulator.setRealtime(realtimeCheckBox.isSelected()));
    }

    private void disablePanels(boolean disable) {
        blockPane.setEnabled(!disable);
        cutterPane.setEnabled(!disable);
        pathPane.setEnabled(!disable);
        blockSizeX.setEnabled(!disable);
        blockSizeY.setEnabled(!disable);
        blockResX.setEnabled(!disable);
        blockResY.setEnabled(!disable);
        blockH.setEnabled(!disable);
        blockMinH.setEnabled(!disable);
        cutterRadius.setEnabled(!disable);
        cutterLength.setEnabled(!disable);
        cutterRound.setEnabled(!disable);
        loadButton.setEnabled(!disable);
        simulateButton.setEnabled(!disable);
    }

    private MaterialBlock newBlock() {
        return new MaterialBlock(
                new Vector2f(((Number) blockSizeX.getValue()).floatValue(), ((Number) blockSizeY.getValue()).floatValue()),
                new Vector2i(((Number) blockResX.getValue()).intValue(), ((Number) blockResY.getValue()).intValue()),
                ((Number) blockH.getValue()).floatValue(), ((Number) blockMinH.getValue()).floatValue());
    }

    private MillingTool newCutter() {
        return new MillingTool(
                ((Number) cutterRadius.getValue()).floatValue(),
                ((Number) cutterLength.getValue()).floatValue(),
                !cutterRound.isSelected());
    }

    @Override
    public void set(MillingSimulator simulator) {
        this.simulator = simulator;
        refresh();
    }

    @Override
    public Container getMainPane() {
        return mainPane;
    }

    @Override
    public void refresh() {
        blockSizeX.setValue(simulator.getBlock().getSize().x());
        blockSizeY.setValue(simulator.getBlock().getSize().y());
        blockResX.setValue(simulator.getBlock().getResolution().x());
        blockResY.setValue(simulator.getBlock().getResolution().y());
        blockH.setValue(simulator.getBlock().getOriginalHeight());
        blockMinH.setValue(simulator.getBlock().getMinHeight());
        showBlock.setSelected(simulator.isShowBlock());

        cutterRadius.setValue(simulator.getTool().getRadius());
        cutterLength.setValue(simulator.getTool().getLength());
        cutterRound.setSelected(!simulator.getTool().isFlat());
        showCutter.setSelected(simulator.isShowCutter());

        showPath.setSelected(simulator.isShowPath());

        realtimeCheckBox.setSelected(simulator.getRealtime());
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
        mainPane.setLayout(new GridLayoutManager(6, 1, new Insets(0, 0, 0, 0), -1, -1));
        blockPane = new JPanel();
        blockPane.setLayout(new GridLayoutManager(7, 2, new Insets(0, 0, 0, 0), -1, -1));
        mainPane.add(blockPane, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        blockPane.setBorder(BorderFactory.createTitledBorder(null, "block", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JLabel label1 = new JLabel();
        label1.setText("sizeX");
        blockPane.add(label1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("sizeY");
        blockPane.add(label2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("resX");
        blockPane.add(label3, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label4 = new JLabel();
        label4.setText("resY");
        blockPane.add(label4, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label5 = new JLabel();
        label5.setText("H");
        blockPane.add(label5, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label6 = new JLabel();
        label6.setText("minH");
        blockPane.add(label6, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        showBlock = new JCheckBox();
        showBlock.setText("show");
        blockPane.add(showBlock, new GridConstraints(6, 0, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        blockSizeX = new JSpinner();
        blockPane.add(blockSizeX, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        blockSizeY = new JSpinner();
        blockPane.add(blockSizeY, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        blockResX = new JSpinner();
        blockPane.add(blockResX, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        blockResY = new JSpinner();
        blockPane.add(blockResY, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        blockH = new JSpinner();
        blockPane.add(blockH, new GridConstraints(4, 1, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        blockMinH = new JSpinner();
        blockPane.add(blockMinH, new GridConstraints(5, 1, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        pathPane = new JPanel();
        pathPane.setLayout(new GridLayoutManager(3, 2, new Insets(0, 0, 0, 0), -1, -1));
        mainPane.add(pathPane, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        pathPane.setBorder(BorderFactory.createTitledBorder(null, "path", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        loadButton = new JButton();
        loadButton.setText("Load...");
        pathPane.add(loadButton, new GridConstraints(0, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        showPath = new JCheckBox();
        showPath.setText("show");
        pathPane.add(showPath, new GridConstraints(2, 0, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        pathName = new JLabel();
        pathName.setText("none");
        pathPane.add(pathName, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label7 = new JLabel();
        label7.setText("Loaded");
        pathPane.add(label7, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        cutterPane = new JPanel();
        cutterPane.setLayout(new GridLayoutManager(4, 2, new Insets(0, 0, 0, 0), -1, -1));
        mainPane.add(cutterPane, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        cutterPane.setBorder(BorderFactory.createTitledBorder(null, "cutter", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JLabel label8 = new JLabel();
        label8.setText("radius");
        cutterPane.add(label8, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label9 = new JLabel();
        label9.setText("length");
        cutterPane.add(label9, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        cutterRound = new JCheckBox();
        cutterRound.setText("round");
        cutterPane.add(cutterRound, new GridConstraints(2, 0, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        showCutter = new JCheckBox();
        showCutter.setText("show");
        cutterPane.add(showCutter, new GridConstraints(3, 0, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        cutterRadius = new JSpinner();
        cutterPane.add(cutterRadius, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        cutterLength = new JSpinner();
        cutterPane.add(cutterLength, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        simulateButton = new JButton();
        simulateButton.setText("Simulate");
        mainPane.add(simulateButton, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        progressBar = new JProgressBar();
        mainPane.add(progressBar, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        realtimeCheckBox = new JCheckBox();
        realtimeCheckBox.setText("realtime");
        mainPane.add(realtimeCheckBox, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPane;
    }

}
