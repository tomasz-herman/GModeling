package pl.edu.pw.mini.mg1.models;

import com.jogamp.opengl.GL4;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import pl.edu.pw.mini.mg1.cameras.PerspectiveCamera;
import pl.edu.pw.mini.mg1.collisions.Ray;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Scene {
    private final List<Model> models = new ArrayList<>();
    private final List<Model> removedModels = new ArrayList<>();
    private final List<Model> selectedModels = new ArrayList<>();
    private final Pointer globalPointer = new Pointer();
    private final Pointer localPointer = new Pointer();
    private PerspectiveCamera camera;
    private int[] selected = new int[0];

    private final Vector3f translation = new Vector3f();
    private final Vector3f rotation = new Vector3f();
    private final Vector3f scale = new Vector3f();
    private final Matrix4f transformation = new Matrix4f();
    private Pointer transformationCenter = globalPointer;

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

    public void addModelAtPointer(Model model) {
        Vector3fc position = globalPointer.getPosition();
        model.setPosition(position.x(), position.y(), position.z());
        models.add(model);
    }

    public void addModel(Model model) {
        models.add(model);
    }

    public void removeModel(Model model) {
        if(models.contains(model)) {
            models.remove(model);
            removedModels.add(model);
        }
    }

    public void mergePoints() {
        List<Point> points = getSelectedModels().stream()
                .filter(m -> m instanceof Point)
                .map(m -> (Point) m)
                .toList();
        if (points.size() < 2) return;
        Vector3f avg = points.stream()
                .map(Point::getTransformedPosition)
                .reduce(new Vector3f(), Vector3f::add, Vector3f::add)
                .div(points.size());
        Point replacement = new Point(avg.x, avg.y, avg.z);
        for (Point replaced : points) {
            getModels().stream()
                    .filter(m -> m instanceof Patch)
                    .map(m -> (Patch) m)
                    .forEach(patch -> patch.replacePoint(replaced, replacement));
            removeModel(replaced);
        }
        addModel(replacement);
    }

    public void deleteSelected() {
        Set<Model> nonRemovable = models.stream().filter(m -> m instanceof Patch)
                .map(m -> (Patch)m)
                .flatMap(Patch::getPoints)
                .collect(Collectors.toSet());

        Arrays.stream(selected)
                .boxed()
                .sorted(Comparator.reverseOrder())
                .filter(i -> !nonRemovable.contains(models.get(i)))
                .forEach(i -> removedModels.add(models.remove((int)i)));
        models.stream().filter(m -> m instanceof Curve)
                .map(m -> (Curve)m)
                .forEach(c -> removedModels.stream()
                        .filter(p -> p instanceof Point)
                        .map(p -> (Point)p)
                        .forEach(c::removePoint));
        selectModels(new int[0]);
    }

    public void disposeRemovedModels(GL4 gl) {
        removedModels.forEach(model -> model.dispose(gl));
        removedModels.clear();
    }

    public void dispose(GL4 gl) {
        for (Model model : models) model.dispose(gl);
        for (Model model : removedModels) model.dispose(gl);
        globalPointer.validate(gl);
        globalPointer.dispose(gl);
        localPointer.validate(gl);
        localPointer.dispose(gl);
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

    public List<Model> testAll(float x, float y) {
        Ray ray = camera.getRay(x, y);
        List<Model> hits = new ArrayList<>();
        for (Model model : models) {
            float dist = model.test(ray);
            if(dist != -1) {
                hits.add(model);
            }
        }
        return hits;
    }

    public void updateLocalPointerPosition() {
        if(selected == null || selected.length == 0) return;
        Vector3f position = new Vector3f();
        AtomicInteger c = new AtomicInteger();
        selectedModels.stream().filter(m -> !(m instanceof Curve)).forEach(m -> {
            c.getAndIncrement();
            if(transformationCenter == localPointer)
                position.add(m.getPosition());
            else
                position.add(m.getTransformedPosition());
        });
        position.div(c.get());
        localPointer.setPosition(position.x, position.y, position.z);
    }

    public void selectModels(int[] selected) {
        this.selected = selected;
        selectedModels.stream().distinct().forEach(Model::applyTransformationMatrix);
        translation.set(0);
        rotation.set(0);
        scale.set(1);
        updateTransformationMatrix();
        selectedModels.clear();
        Arrays.stream(selected).mapToObj(models::get).forEach(selectedModels::add);
        updateLocalPointerPosition();
    }

    public void selectAll() {
        selected = IntStream.range(0, models.size()).toArray();
        selectedModels.stream().distinct().forEach(Model::applyTransformationMatrix);
        translation.set(0);
        rotation.set(0);
        scale.set(1);
        updateTransformationMatrix();
        selectedModels.clear();
        selectedModels.addAll(models);
        updateLocalPointerPosition();
    }

    public int[] getSelected() {
        return selected;
    }

    public List<Model> getSelectedModels() {
        return Arrays.stream(selected).mapToObj(models::get).collect(Collectors.toList());
    }

    public void selectModel(Model model) {
        selectModels(new int[] {models.indexOf(model)});
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
        selectModels(selected.stream().mapToInt(i -> i).sorted().toArray());
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
        updateTransformationMatrix();
    }

    public void setTransformationCenter(boolean useGlobalPointer) {
        setTransformationCenter(useGlobalPointer ? globalPointer : localPointer);
    }

    public void setTransformationCenter(Pointer pointer) {
        selectModels(selected);
        this.transformationCenter = pointer;
    }

    public Vector3fc getTranslation() {
        return translation;
    }

    public Vector3fc getRotation() {
        return rotation;
    }

    public Vector3fc getScale() {
        return scale;
    }

    public void setTranslation(float x, float y, float z) {
        this.translation.set(x, y, z);
        updateTransformationMatrix();
    }

    public void setRotation(float x, float y, float z) {
        this.rotation.set(x, y, z);
        updateTransformationMatrix();
    }

    public void setScale(float x, float y, float z) {
        this.scale.set(x, y, z);
        updateTransformationMatrix();
    }

    private void updateTransformationMatrix() {
        Vector3fc transformationCenter = this.transformationCenter.getPosition();
        transformation.identity()
                .translate(transformationCenter)
                .translate(translation)
                .rotateZYX(
                        (float) Math.toRadians(rotation.z),
                        (float) Math.toRadians(rotation.y),
                        (float) Math.toRadians(rotation.x))
                .scale(scale)
                .translate(transformationCenter.negate(new Vector3f()));
        for (Model model : selectedModels) {
            model.setTransformationMatrix(transformation);
        }
    }

    public void serialize(String filename) {
        try (PrintWriter writer = new PrintWriter(filename)) {
            writer.println(
                    """
                    <Scene>
                    """
            );

            for (Model model : models) {
                writer.println(model.serialize());
            }

            writer.println(
                    """
                    </Scene>
                    """
            );
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void deserialize(String filename) {
        if(!new File(filename).exists()) return;
        removedModels.addAll(models);
        models.clear();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new File(filename));
            document.getDocumentElement().normalize();

            NodeList pointsList = document.getElementsByTagName("Point");

            NodeList bezierC0List = document.getElementsByTagName("BezierC0");
            NodeList bezierC2List = document.getElementsByTagName("BezierC2");
            NodeList bezierInterList = document.getElementsByTagName("BezierInter");
            NodeList torusList = document.getElementsByTagName("Torus");
            NodeList patchC0List = document.getElementsByTagName("PatchC0");
            NodeList patchC2List = document.getElementsByTagName("PatchC2");

            for (int i = 0; i < pointsList.getLength(); i++) {
                models.add(new Point().deserialize(pointsList.item(i)));
            }

            Map<String, Point> points = models.stream()
                    .map(p -> (Point)p)
                    .collect(Collectors.toMap(Point::getName, Function.identity()));

            for (int i = 0; i < patchC0List.getLength(); i++) {
                models.add(new BezierPatchC0().deserialize(patchC0List.item(i), points));
            }

            for (int i = 0; i < patchC2List.getLength(); i++) {
                models.add(new BezierPatchC2().deserialize(patchC2List.item(i), points));
            }

            for (int i = 0; i < bezierC0List.getLength(); i++) {
                models.add(new BezierC0(new ArrayList<>()).deserialize(bezierC0List.item(i), points));
            }

            for (int i = 0; i < bezierC2List.getLength(); i++) {
                models.add(new BezierC2(new ArrayList<>()).deserialize(bezierC2List.item(i), points));
            }

            for (int i = 0; i < bezierInterList.getLength(); i++) {
                models.add(new InterpolationBezierC2(new ArrayList<>()).deserialize(bezierInterList.item(i), points));
            }

            for (int i = 0; i < torusList.getLength(); i++) {
                models.add(new Torus().deserialize(torusList.item(i), points));
            }

        } catch (ParserConfigurationException | IOException | SAXException | NumberFormatException e) {
            e.printStackTrace();
        }
    }
}
