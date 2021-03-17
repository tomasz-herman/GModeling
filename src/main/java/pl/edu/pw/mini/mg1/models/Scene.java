package pl.edu.pw.mini.mg1.models;

import com.jogamp.opengl.GL4;
import pl.edu.pw.mini.mg1.cameras.PerspectiveCamera;
import pl.edu.pw.mini.mg1.collisions.Ray;

import java.util.ArrayList;
import java.util.List;

public class Scene {
    private final List<Model> models = new ArrayList<>();
    private final List<Pointer> pointers = new ArrayList<>();
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

    public Model test(float x, float y) {
        Ray ray = camera.getRay(x, y);
        float minDistance = Float.POSITIVE_INFINITY;
        Model closest = null;
        for (Model model : models) {
            float dist = model.test(ray);
            if(dist > 0 && dist < minDistance) {
                minDistance = dist;
                closest = model;
            }
        }
        return closest;
    }
}
