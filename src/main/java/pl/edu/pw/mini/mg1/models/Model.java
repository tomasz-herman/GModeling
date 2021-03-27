package pl.edu.pw.mini.mg1.models;

import com.jogamp.opengl.GL4;
import org.joml.*;
import pl.edu.pw.mini.mg1.collisions.BoundingVolume;
import pl.edu.pw.mini.mg1.collisions.Ray;
import pl.edu.pw.mini.mg1.utils.GeneratorUtils;

import java.lang.Math;

public abstract class Model {
    protected Mesh mesh;
    protected BoundingVolume boundingVolume;
    private final Vector3f position;
    private final Vector3f rotation;
    private final Vector3f scale;

    private final Matrix4f modelMatrix;
    private final Matrix4f transformationMatrix;
    protected boolean reload = true;

    private String name;

    public Model() {
        position = new Vector3f();
        rotation = new Vector3f();
        scale = new Vector3f(1);
        modelMatrix = new Matrix4f();
        transformationMatrix = new Matrix4f();
        name = getClass().getSimpleName() + " " + GeneratorUtils.getID();
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
            if(mesh != null) mesh.dispose(gl);
            load(gl);
        }
        reload = false;
    }

    protected abstract void load(GL4 gl);

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
}
