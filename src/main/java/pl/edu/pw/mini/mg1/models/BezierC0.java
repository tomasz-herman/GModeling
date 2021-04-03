package pl.edu.pw.mini.mg1.models;

import com.jogamp.opengl.GL4;
import org.apache.commons.lang3.ArrayUtils;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import pl.edu.pw.mini.mg1.cameras.PerspectiveCamera;
import pl.edu.pw.mini.mg1.graphics.Renderer;

import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class BezierC0 extends Model {
    private final List<Point> points;
    private final PropertyChangeListener pcl = e -> reload = true;

    private boolean showPolyline = true;

    public BezierC0(List<Point> points, PerspectiveCamera camera) {
        this.points = new ArrayList<>(points);
    }

    public void addPoint(Point point) {
        if (!points.contains(point)) {
            points.add(point);
            point.addPropertyChangeListener(pcl);
        }
        reload = true;
    }

    public void removePoint(Point point) {
        points.remove(point);
        point.removePropertyChangeListener(pcl);
        reload = true;
    }

    public List<Point> getPoints() {
        return points;
    }

    @Override
    protected void load(GL4 gl) {
        float[] positions = ArrayUtils.toPrimitive(points.stream()
                .flatMap(point ->
                        Stream.of(point.getTransformedPosition().x(), point.getTransformedPosition().y(), point.getTransformedPosition().z()))
                .toArray(Float[]::new));
        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < points.size(); i += 3) {
            indices.add(Math.min(i, points.size() - 1));
            indices.add(Math.min(i + 1, points.size() - 1));
            indices.add(Math.min(i + 2, points.size() - 1));
            indices.add(Math.min(i + 3, points.size() - 1));
        }
        this.mesh = new Mesh(
                positions,
                ArrayUtils.toPrimitive(indices.toArray(new Integer[0])),
                GL4.GL_LINES_ADJACENCY);
        mesh.load(gl);
    }

    @Override
    public void render(GL4 gl, PerspectiveCamera camera, Renderer renderer) {
        renderer.renderBezier(gl, camera, this);
    }

    @Override
    public Matrix4fc getModelMatrix() {
        return new Matrix4f();
    }

    @Override
    protected void setupBoundingVolume() {
        boundingVolume = null;
    }

    @Override
    public void setTransformationMatrix(Matrix4fc transformation) {
    }

    @Override
    public void applyTransformationMatrix() {
    }

    @Override
    public void setPosition(float x, float y, float z) {
    }

    @Override
    public void setRotation(float x, float y, float z) {
    }

    @Override
    public void setScale(float x, float y, float z) {
    }

    public boolean isShowPolyline() {
        return showPolyline;
    }

    public void setShowPolyline(boolean showPolyline) {
        this.showPolyline = showPolyline;
    }
}
