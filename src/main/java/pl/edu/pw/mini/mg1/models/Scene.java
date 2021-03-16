package pl.edu.pw.mini.mg1.models;

import com.jogamp.opengl.GL4;
import pl.edu.pw.mini.mg1.cameras.PerspectiveCamera;

import java.util.ArrayList;
import java.util.List;

public class Scene {
    private final List<Model> models = new ArrayList<>();
    private PerspectiveCamera camera;

    public Scene(PerspectiveCamera camera) {
        this.camera = camera;
    }

    public Scene() { }

    public PerspectiveCamera getCamera() {
        return camera;
    }

    public void setCamera(PerspectiveCamera camera) {
        this.camera = camera;
    }

    public List<Model> getModels() {
        return models;
    }

    public void addModel(Model model) {
        models.add(model);
    }

    public void dispose(GL4 gl) {
        for (Model model : models) {
            model.getMesh().dispose(gl);
        }
    }
}
