package pl.edu.pw.mini.mg1.models;

import com.jogamp.opengl.GL4;
import org.joml.Vector2f;
import org.joml.Vector2i;
import pl.edu.pw.mini.mg1.cameras.PerspectiveCamera;
import pl.edu.pw.mini.mg1.graphics.Renderer;
import pl.edu.pw.mini.mg1.milling.MaterialBlock;
import pl.edu.pw.mini.mg1.milling.MillingException;
import pl.edu.pw.mini.mg1.milling.MillingTool;
import pl.edu.pw.mini.mg1.milling.Path;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

public class MillingSimulator extends Model {
    public static final int SIMULATION_TO_SCENE_SCALE = 100;
    private final List<Model> trash = new ArrayList<>();
    private Path path;
    private MillingTool tool;
    private MaterialBlock block;

    private PolyLine polyline;
    private MilledBlock blockModel;
    private Cutter cutter;

    private boolean showPath = false;
    private boolean showCutter = true;
    private boolean showBlock = true;

    private volatile boolean realtime = false;

    public MillingSimulator() {
        block = new MaterialBlock(new Vector2f(180, 180), new Vector2i(400, 400), 50, 15);
        tool = new MillingTool(5, 15, false);
        blockModel = new MilledBlock(block);
        cutter = new Cutter(tool);
        cutter.setPosition(0, 0.8f, 0);
    }

    @Override
    protected void load(GL4 gl) {

    }

    @Override
    public void render(GL4 gl, PerspectiveCamera camera, Renderer renderer) {
        if(polyline != null && showPath) polyline.render(gl, camera, renderer);
        if(blockModel != null && showBlock) blockModel.render(gl, camera, renderer);
        if(cutter != null && showCutter) cutter.render(gl, camera, renderer);
    }

    @Override
    public void validate(GL4 gl) {
        if(polyline != null) polyline.validate(gl);
        if(blockModel != null) blockModel.validate(gl);
        if(cutter != null) cutter.validate(gl);
        for (Model model : trash) {
            model.dispose(gl);
        }
        trash.clear();
    }

    @Override
    public void dispose(GL4 gl) {
        if(polyline != null) polyline.dispose(gl);
        if(blockModel != null) blockModel.dispose(gl);
        if(cutter != null) cutter.dispose(gl);
        for (Model model : trash) {
            model.dispose(gl);
        }
        trash.clear();
    }

    public void setPath(Path path) {
        this.path = path;
        if(polyline != null) trash.add(polyline);
        this.polyline = path.toPolyLine();
    }

    public MillingTool getTool() {
        return tool;
    }

    public MaterialBlock getBlock() {
        return block;
    }

    public void setTool(MillingTool tool) {
        this.tool = tool;
        if(cutter != null) trash.add(cutter);
        cutter = new Cutter(tool);
        cutter.setPosition(0, 0.8f, 0);
    }

    public void setBlock(MaterialBlock block) {
        this.block = block;
        if(blockModel != null) trash.add(blockModel);
        blockModel = new MilledBlock(block);
    }

    public boolean isShowPath() {
        return showPath;
    }

    public void setShowPath(boolean showPath) {
        this.showPath = showPath;
    }

    public boolean isShowCutter() {
        return showCutter;
    }

    public void setShowCutter(boolean showCutter) {
        this.showCutter = showCutter;
    }

    public boolean isShowBlock() {
        return showBlock;
    }

    public void setShowBlock(boolean showBlock) {
        this.showBlock = showBlock;
    }

    public void simulate(Consumer<Integer> progress, Consumer<Boolean> disablePanels) {
        new Thread(() -> {
            try {
                if(disablePanels != null) disablePanels.accept(true);
                AtomicLong last = new AtomicLong();
                block.mill(tool, path, progress, vec -> {
                    try {
                        vec.div(SIMULATION_TO_SCENE_SCALE);
                        cutter.setPosition(vec.x, vec.y, vec.z);
                        if(realtime) {
                            long now = System.nanoTime();
                            long delta = (now - last.get()) / 1_000_000;
                            long maxTime = 10;
                            if(delta < maxTime) {
                                Thread.sleep(maxTime - delta);
                            }
                            last.set(System.nanoTime());
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }, blockModel::reloadTexture);
            } catch (MillingException e) {
                JOptionPane.showMessageDialog(null, e.getMessage());
            } finally {
                if(disablePanels != null) disablePanels.accept(false);
            }
        }
        ).start();
    }

    public boolean getRealtime() {
        return realtime;
    }

    public void setRealtime(boolean realtime) {
        this.realtime = realtime;
    }
}
