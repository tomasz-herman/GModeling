package pl.edu.pw.mini.mg1.models;

import com.jogamp.opengl.GL4;
import org.apache.commons.lang3.ArrayUtils;
import org.joml.Vector3fc;
import pl.edu.pw.mini.mg1.cameras.PerspectiveCamera;
import pl.edu.pw.mini.mg1.graphics.Renderer;
import pl.edu.pw.mini.mg1.numerics.SplineInterpolation;

import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

public class InterpolationBezierC2 extends Model implements Curve {
    private final List<Point> points = new ArrayList<>();
    private final BezierC2 bezierC2 = new BezierC2(new ArrayList<>());
    private final PropertyChangeListener pcl = e -> reload = true;
    private boolean showPolyline = true;

    public InterpolationBezierC2(List<Point> points) {
        points.forEach(this::addPoint);
    }

    @Override
    public void addPoint(Point point) {
        if (!points.contains(point)) {
            points.add(point);
            point.addPropertyChangeListener(pcl);
        }
        reload = true;
    }

    @Override
    public void removePoint(Point point) {
        points.remove(point);
        point.removePropertyChangeListener(pcl);
        reload = true;
    }

    @Override
    public void removeAllPoints() {
        List<Point> copy = new ArrayList<>(points);
        copy.forEach(this::removePoint);
    }

    public List<Point> getPoints() {
        return points;
    }

    @Override
    protected void setupBoundingVolume() {
        boundingVolume = null;
    }

    @Override
    public void dispose(GL4 gl) {
        super.dispose(gl);
        points.forEach(p -> p.removePropertyChangeListener(pcl));
        bezierC2.dispose(gl);
    }

    @Override
    public void validate(GL4 gl) {
        super.validate(gl);
        bezierC2.validate(gl);
    }

    @Override
    protected void load(GL4 gl) {
        points.forEach(p -> p.addPropertyChangeListener(pcl));

        SplineInterpolation xInterpolation = new SplineInterpolation(
                ArrayUtils.toPrimitive(points.stream()
                .map(Model::getTransformedPosition)
                .map(Vector3fc::x)
                .toArray(Float[]::new)));
        SplineInterpolation yInterpolation = new SplineInterpolation(
                ArrayUtils.toPrimitive(points.stream()
                        .map(Model::getTransformedPosition)
                        .map(Vector3fc::y)
                        .toArray(Float[]::new)));
        SplineInterpolation zInterpolation = new SplineInterpolation(
                ArrayUtils.toPrimitive(points.stream()
                        .map(Model::getTransformedPosition)
                        .map(Vector3fc::z)
                        .toArray(Float[]::new)));

        float[] x = xInterpolation.solve();
        float[] y = yInterpolation.solve();
        float[] z = zInterpolation.solve();

        List<Point> points = new ArrayList<>();

        for (int i = 0; i < x.length; i++) {
            Point point = new Point();
            point.setPosition(x[i], y[i], z[i]);
            if(i == 0 || i == x.length - 1) {
                Point p2 = new Point();
                Point p3 = new Point();
                p2.setPosition(x[i], y[i], z[i]);
                p3.setPosition(x[i], y[i], z[i]);
                points.add(p2);
                points.add(p3);
            }
            points.add(point);
        }

        bezierC2.removeAllPoints();
        points.forEach(bezierC2::addPoint);
        bezierC2.load(gl);
    }

    @Override
    public void render(GL4 gl, PerspectiveCamera camera, Renderer renderer) {
        if(bezierC2 != null) {
            bezierC2.setShowPolyline(false);
            bezierC2.setShowBezierPoints(false);
            bezierC2.setShowBezierPolyline(showPolyline);
            bezierC2.render(gl, camera, renderer);
        }
    }

    public boolean isShowPolyline() {
        return showPolyline;
    }

    public void setShowPolyline(boolean showPolyline) {
        this.showPolyline = showPolyline;
    }
}
