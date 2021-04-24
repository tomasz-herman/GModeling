package pl.edu.pw.mini.mg1.models;

import com.jogamp.opengl.GL4;
import org.joml.Vector3f;
import pl.edu.pw.mini.mg1.cameras.PerspectiveCamera;
import pl.edu.pw.mini.mg1.graphics.Renderer;
import pl.edu.pw.mini.mg1.numerics.SplineSmoothInterpolation;

import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

public class ChordInterpolationBezierC2 extends Model implements Curve {
    private final List<Point> points = new ArrayList<>();
    private final BezierC0 bezierC0 = new BezierC0(new ArrayList<>());
    private final PropertyChangeListener pcl = e -> reload = true;
    private boolean showPolyline = true;

    public ChordInterpolationBezierC2(List<Point> points) {
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
        bezierC0.dispose(gl);
    }

    @Override
    public void validate(GL4 gl) {
        super.validate(gl);
        bezierC0.validate(gl);
    }

    @Override
    protected void load(GL4 gl) {
        points.forEach(p -> p.addPropertyChangeListener(pcl));

        SplineSmoothInterpolation ssi = new SplineSmoothInterpolation(
                points.stream()
                .map(Model::getTransformedPosition)
                .map(Vector3f::new)
                .toArray(Vector3f[]::new));

        var knots = ssi.solve();

        List<Point> points = new ArrayList<>();

        for (int i = 0; i < knots.size(); i+=4) {
            Vector3f knotA = knots.get(i);
            Vector3f knotB = knots.get(i + 1);
            Vector3f knotC = knots.get(i + 2);
            Point point = new Point();
            point.setPosition(knotA.x, knotA.y, knotA.z);
            points.add(point);
            point = new Point();
            point.setPosition(knotB.x, knotB.y, knotB.z);
            points.add(point);
            point = new Point();
            point.setPosition(knotC.x, knotC.y, knotC.z);
            points.add(point);
            if(i + 4 == knots.size()) {
                Vector3f knotD = knots.get(i + 3);
                point = new Point();
                point.setPosition(knotD.x, knotD.y, knotD.z);
                points.add(point);
            }
        }
        bezierC0.removeAllPoints();
        points.forEach(bezierC0::addPoint);
        bezierC0.load(gl);
    }

    @Override
    public void render(GL4 gl, PerspectiveCamera camera, Renderer renderer) {
        if(bezierC0 != null) {
            bezierC0.setShowPolyline(showPolyline);
            bezierC0.render(gl, camera, renderer);
        }
    }

    public boolean isShowPolyline() {
        return showPolyline;
    }

    public void setShowPolyline(boolean showPolyline) {
        this.showPolyline = showPolyline;
    }
}