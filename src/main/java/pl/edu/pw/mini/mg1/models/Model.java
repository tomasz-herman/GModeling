package pl.edu.pw.mini.mg1.models;

import com.jogamp.opengl.GL4;
import org.joml.*;
import org.w3c.dom.Node;
import pl.edu.pw.mini.mg1.cameras.PerspectiveCamera;
import pl.edu.pw.mini.mg1.collisions.BoundingVolume;
import pl.edu.pw.mini.mg1.collisions.Ray;
import pl.edu.pw.mini.mg1.graphics.Renderer;
import pl.edu.pw.mini.mg1.utils.GeneratorUtils;

import java.lang.Math;
import java.util.Collections;
import java.util.Map;

public abstract class Model {
    protected Mesh mesh;
    protected BoundingVolume boundingVolume;
    private final Vector3f position;
    private final Vector3f rotation;
    private final Vector3f scale;

    private final Matrix4f modelMatrix;
    private final Matrix4f transformationMatrix;
    protected boolean reload = true;
    protected boolean selected;

    private String name;

    public Model() {
        position = new Vector3f();
        rotation = new Vector3f();
        scale = new Vector3f(1);
        modelMatrix = new Matrix4f();
        transformationMatrix = new Matrix4f();
        name = generateName();
        setupBoundingVolume();
    }

    public void render(GL4 gl, PerspectiveCamera camera, Renderer renderer) {
        renderer.render(gl, camera, this);
    }

    protected void setupBoundingVolume() {
        this.boundingVolume = null;
    }

    private void calculateModelMatrix() {
        modelMatrix.identity()
                .translate(getTransformedPosition())
                .rotateZYX(
                        (float) Math.toRadians(rotation.z),
                        (float) Math.toRadians(rotation.y),
                        (float) Math.toRadians(rotation.x))
                .scale(scale);
    }

    public Matrix4f getTransformationMatrix() {
        return transformationMatrix;
    }

    public void setTransformationMatrix(Matrix4fc transformation) {
        this.transformationMatrix.set(transformation);
        calculateModelMatrix();
    }

    public void applyTransformationMatrix() {
        this.position.set(position.mulPosition(transformationMatrix));
        calculateModelMatrix();
    }

    public Mesh getMesh() {
        return mesh;
    }

    public Vector3fc getPosition() {
        return position;
    }

    public Vector3fc getTransformedPosition() {
        return position.mulPosition(transformationMatrix, new Vector3f());
    }

    public Vector3fc getRotation() {
        return rotation;
    }

    public Vector3fc getScale() {
        return scale;
    }

    public Matrix4fc getModelMatrix() {
        return modelMatrix;
    }

    public void move(float dx, float dy, float dz) {
        position.add(dx, dy, dz);
        calculateModelMatrix();
    }

    public void rotate(float dx, float dy, float dz) {
        rotation.add(dx, dy, dz);
        calculateModelMatrix();
    }

    public void setPosition(float x, float y, float z) {
        position.set(x, y, z);
        calculateModelMatrix();
    }

    public void setRotation(float x, float y, float z) {
        rotation.set(x, y, z);
        calculateModelMatrix();
    }

    public void setScale(float x, float y, float z) {
        scale.set(x, y, z);
        calculateModelMatrix();
    }

    public void validate(GL4 gl) {
        if(reload) {
            dispose(gl);
            load(gl);
        }
        reload = false;
    }

    protected abstract void load(GL4 gl);

    public void dispose(GL4 gl) {
        if(mesh != null) mesh.dispose(gl);
        reload = true;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float test(Ray ray) {
        if(boundingVolume == null) return -1;
        Matrix4fc invModel = modelMatrix.invert(new Matrix4f());
        Ray transformedRay = new Ray(
                ray.getOrigin().mulPosition(invModel, new Vector3f()),
                ray.getDirection().mulDirection(invModel, new Vector3f()));
        float distance = boundingVolume.test(transformedRay);
        if(distance < 0) return -1;
        Vector3f hit = transformedRay.at(distance).mulPosition(modelMatrix);
        return hit.distance(ray.getOrigin());
    }

    protected String generateName() {
        return getClass().getSimpleName() + " " + GeneratorUtils.getID();
    }

    public void cleanup() { }

    public String serialize() {
        return "";
    }

    public Model deserialize(Node node) {
        return deserialize(node, Collections.emptyMap());
    }

    public Model deserialize(Node node, Map<String, Point> points) {
        return this;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
