package pl.edu.pw.mini.mg1.models;

import com.jogamp.opengl.GL4;
import org.joml.Matrix4fc;
import org.joml.Vector3fc;
import pl.edu.pw.mini.mg1.collisions.BoundingSphere;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class Point extends Model {
    private final PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);

    public Point() { }

    public Point(float x, float y, float z) {
        setPosition(x, y, z);
    }

    @Override
    protected void setupBoundingVolume() {
        this.boundingVolume = new BoundingSphere(0.05f);
    }

    @Override
    protected void load(GL4 gl) {
        this.mesh = new Mesh(
                new float[] {0, 0, 0},
                new int[] {0},
                GL4.GL_POINTS);
        mesh.load(gl);
    }

    public void addPropertyChangeListener(PropertyChangeListener pcl) {
        changeSupport.addPropertyChangeListener(pcl);
    }

    public void removePropertyChangeListener(PropertyChangeListener pcl) {
        changeSupport.removePropertyChangeListener(pcl);
    }

    @Override
    public void setPosition(float x, float y, float z) {
        Vector3fc oldPosition = getTransformedPosition();
        super.setPosition(x, y, z);
        changeSupport.firePropertyChange("position", oldPosition, getTransformedPosition());
    }

    @Override
    public void move(float dx, float dy, float dz) {
        Vector3fc oldPosition = getTransformedPosition();
        super.move(dx, dy, dz);
        changeSupport.firePropertyChange("position", oldPosition, getTransformedPosition());
    }

    @Override
    public void setTransformationMatrix(Matrix4fc transformation) {
        Vector3fc oldPosition = getTransformedPosition();
        super.setTransformationMatrix(transformation);
        changeSupport.firePropertyChange("position", oldPosition, getTransformedPosition());
    }

}
