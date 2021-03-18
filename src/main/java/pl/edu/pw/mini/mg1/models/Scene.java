package pl.edu.pw.mini.mg1.models;

import com.jogamp.opengl.GL4;
import org.joml.Vector2fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import pl.edu.pw.mini.mg1.cameras.PerspectiveCamera;
import pl.edu.pw.mini.mg1.collisions.Ray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Scene {
    private final List<Model> models = new ArrayList<>();
    private final List<Model> removedModels = new ArrayList<>();
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
        Vector3fc position = globalPointer.getPosition();
        model.setPosition(position.x(), position.y(), position.z());
        models.add(model);
    }

    public void deleteSelected() {
        Arrays.stream(selected)
                .boxed()
                .sorted(Comparator.reverseOrder())
                .forEach(i -> removedModels.add(models.remove((int)i)));
        selected = new int[0];
    }

    public void disposeRemovedModels(GL4 gl) {
        removedModels.forEach(model -> model.getMesh().dispose(gl));
        removedModels.clear();
    }

    public void dispose(GL4 gl) {
        for (Model model : models) {
            model.getMesh().dispose(gl);
        }
        globalPointer.validate(gl);
        globalPointer.getMesh().dispose(gl);
        localPointer.validate(gl);
        localPointer.getMesh().dispose(gl);
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

    public void selectModels() {
        selectModels(selected);
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

    public Pointer getPointer() {
        return globalPointer;
    }

    public Vector3f getPointerScreenCoords() {
        return camera.project(getPointerWorldCoords());
    }

    public Vector3f getPointerWorldCoords() {
        return globalPointer.getPosition().get(new Vector3f());
    }

    public void setPointerScreenCoords(Vector3fc coords) {
        Vector3fc position = camera.unproject(coords);
        setPointerWorldCoords(position);
    }

    public void setPointerWorldCoords(Vector3fc coords) {
        globalPointer.setPosition(coords.x(), coords.y(), coords.z());
    }
}
