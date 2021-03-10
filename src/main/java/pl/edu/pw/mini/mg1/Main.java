package pl.edu.pw.mini.mg1;

import com.formdev.flatlaf.FlatDarkLaf;
import com.hermant.swing.WindowBuilder;
import pl.edu.pw.mini.mg1.layout.MainLayout;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FlatDarkLaf.install();
            MainLayout mainLayout = new MainLayout();
            JFrame window = new WindowBuilder()
                    .setContentPane(mainLayout.getMainPane())
                    .setSize(1280, 720)
                    .setMinimumSize(320, 240)
                    .setTitle("GModeling")
                    .setExitOnClose()
                    .buildFrame();
        });
    }
}
