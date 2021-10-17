package pl.edu.pw.mini.mg1.layout;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
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
    private JButton doItFasterButton;
    private JProgressBar progressBar;
    private MillingSimulator simulator;

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
            JFileChooser fileChooser = new JFileChooser(".");
            if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                try {
                    Path path = new Path(new FileInputStream(file), 1);
                    simulator.setPath(path);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        showPath.addChangeListener(e -> simulator.setShowPath(showPath.isSelected()));
        simulateButton.addActionListener(e -> simulator.simulate(progressBar::setValue));
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
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(7, 2, new Insets(0, 0, 0, 0), -1, -1));
        mainPane.add(panel1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel1.setBorder(BorderFactory.createTitledBorder(null, "block", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JLabel label1 = new JLabel();
        label1.setText("sizeX");
        panel1.add(label1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("sizeY");
        panel1.add(label2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("resX");
        panel1.add(label3, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label4 = new JLabel();
        label4.setText("resY");
        panel1.add(label4, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label5 = new JLabel();
        label5.setText("H");
        panel1.add(label5, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label6 = new JLabel();
        label6.setText("minH");
        panel1.add(label6, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        showBlock = new JCheckBox();
        showBlock.setText("show");
        panel1.add(showBlock, new GridConstraints(6, 0, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        blockSizeX = new JSpinner();
        panel1.add(blockSizeX, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        blockSizeY = new JSpinner();
        panel1.add(blockSizeY, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        blockResX = new JSpinner();
        panel1.add(blockResX, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        blockResY = new JSpinner();
        panel1.add(blockResY, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        blockH = new JSpinner();
        panel1.add(blockH, new GridConstraints(4, 1, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        blockMinH = new JSpinner();
        panel1.add(blockMinH, new GridConstraints(5, 1, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        mainPane.add(panel2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel2.setBorder(BorderFactory.createTitledBorder(null, "path", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        loadButton = new JButton();
        loadButton.setText("Load...");
        panel2.add(loadButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        showPath = new JCheckBox();
        showPath.setText("show");
        panel2.add(showPath, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(4, 2, new Insets(0, 0, 0, 0), -1, -1));
        mainPane.add(panel3, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel3.setBorder(BorderFactory.createTitledBorder(null, "cutter", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JLabel label7 = new JLabel();
        label7.setText("radius");
        panel3.add(label7, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label8 = new JLabel();
        label8.setText("length");
        panel3.add(label8, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        cutterRound = new JCheckBox();
        cutterRound.setText("round");
        panel3.add(cutterRound, new GridConstraints(2, 0, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        showCutter = new JCheckBox();
        showCutter.setText("show");
        panel3.add(showCutter, new GridConstraints(3, 0, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        cutterRadius = new JSpinner();
        panel3.add(cutterRadius, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        cutterLength = new JSpinner();
        panel3.add(cutterLength, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        simulateButton = new JButton();
        simulateButton.setText("Simulate");
        mainPane.add(simulateButton, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        doItFasterButton = new JButton();
        doItFasterButton.setText("Do it Faster");
        mainPane.add(doItFasterButton, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        progressBar = new JProgressBar();
        mainPane.add(progressBar, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPane;
    }

}
