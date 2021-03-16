package pl.edu.pw.mini.mg1.models;

import com.jogamp.opengl.GL4;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import pl.edu.pw.mini.mg1.utils.GeneratorUtils;

public abstract class Model {
    protected Mesh mesh;
    private final Vector3f position;
    private final Vector3f rotation;
    private final Vector3f scale;

    private final Matrix4f modelMatrix;
    protected boolean reload = true;

    private String name;

    public Model() {
        position = new Vector3f();
        rotation = new Vector3f();
        scale = new Vector3f(1);
        modelMatrix = new Matrix4f();
        name = getClass().getSimpleName() + " " + GeneratorUtils.getID();
    }

    private void calculateModelMatrix() {
        modelMatrix.identity()
                .translate(position)
                .rotateXYZ(
                        (float) Math.toRadians(rotation.x),
                        (float) Math.toRadians(rotation.y),
                        (float) Math.toRadians(rotation.z))
                .scale(scale);
    }

    public Mesh getMesh() {
        return mesh;
    }

    public Vector3fc getPosition() {
        return position;
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
}
