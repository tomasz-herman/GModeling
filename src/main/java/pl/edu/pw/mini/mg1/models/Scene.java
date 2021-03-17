package pl.edu.pw.mini.mg1.models;

import com.jogamp.opengl.GL4;
import org.apache.commons.lang3.ArrayUtils;
import org.joml.Vector3f;
import pl.edu.pw.mini.mg1.cameras.PerspectiveCamera;
import pl.edu.pw.mini.mg1.collisions.Ray;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Scene {
    private final List<Model> models = new ArrayList<>();
    private final Pointer globalPointer = new Pointer();
    private final Pointer localPointer = new Pointer();
    private PerspectiveCamera camera;
    private int[] selected = new int[0];

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

    public List<Model> getModelsAndPointers() {
        return Stream.concat(
                models.stream(),
                selected.length > 0 ?
                        Stream.of(globalPointer, localPointer) :
                        Stream.of(globalPointer))
                .collect(Collectors.toList());
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

    public void selectModels(int[] selected) {
        this.selected = selected;
        Vector3f position = new Vector3f();
        for (int i : selected) {
            position.add(models.get(i).getPosition());
        }
        position.div(selected.length);
        localPointer.setPosition(position.x, position.y, position.z);
    }

    public int[] getSelected() {
        return selected;
    }

    public void select(Model model) {
        selected = new int[] {models.indexOf(model)};
    }

    public void invertSelect(Model model) {
        int index = models.indexOf(model);
        List<Integer> selected = IntStream.of(this.selected)
                .boxed().collect(Collectors.toCollection(ArrayList::new));
        if(selected.contains(index)) {
            selected.remove((Object)index);
        } else {
            selected.add(index);
        }
        this.selected = selected.stream().mapToInt(i -> i).sorted().toArray();
    }
}
