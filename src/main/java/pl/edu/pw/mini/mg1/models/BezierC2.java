package pl.edu.pw.mini.mg1.models;

import com.jogamp.opengl.GL4;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import pl.edu.pw.mini.mg1.cameras.PerspectiveCamera;
import pl.edu.pw.mini.mg1.graphics.Renderer;

import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class BezierC2 extends Curve {
    private final BezierC0 curve = new BezierC0(new ArrayList<>());
    private int selectedVirtualPoint = -1;

    private boolean showBezierPoints = true;

    public BezierC2(List<Point> points) {
        super(points);
    }

    public Stream<Point> getVirtualPoints() {
        return curve.getPoints();
    }

    private Point createVirtualPoint(int[] k, Vector3f pos) {
        return new Point(pos.x, pos.y, pos.z) {
            @Override
            protected String generateName() {
                return "Bezier virtual point %d".formatted(k[0]++);
            }
        };
    }

    @Override
    protected void fillPointsList() {
        curve.removeAllPoints();
        final int[] k = {0};
        for (int i = 3; i < controlPoints.size(); i++) {
            Point p1 = controlPoints.get(i - 3);
            Point p2 = controlPoints.get(i - 2);
            Point p3 = controlPoints.get(i - 1);
            Point p4 = controlPoints.get(i);

            Vector3fc v1 = p1.getTransformedPosition();
            Vector3fc v2 = p2.getTransformedPosition();
            Vector3fc v3 = p3.getTransformedPosition();
            Vector3fc v4 = p4.getTransformedPosition();


            Vector3f b2Pos = v2.lerp(v3, 1.0f / 3.0f, new Vector3f());
            Vector3f b3Pos = v2.lerp(v3, 2.0f / 3.0f, new Vector3f());
            Vector3f b1Pos = v1.lerp(v2, 2.0f / 3.0f, new Vector3f());
            Vector3f b4Pos = v3.lerp(v4, 1.0f / 3.0f, new Vector3f());
            b1Pos.lerp(b2Pos, 0.5f);
            b4Pos.lerp(b3Pos, 0.5f);

            Point b1 = createVirtualPoint(k, b1Pos);
            Point b2 = createVirtualPoint(k, b2Pos);
            Point b3 = createVirtualPoint(k, b3Pos);
            Point b4 = createVirtualPoint(k, b4Pos);

            PropertyChangeListener p2Change = evt -> {
                Vector3fc oldValue = (Vector3fc) evt.getOldValue();
                Vector3fc newValue = (Vector3fc) evt.getNewValue();
                Vector3fc delta = newValue.sub(oldValue, new Vector3f()).mul(3.0f / 2.0f);
                p2.move(delta.x(), delta.y(), delta.z());
            };

            PropertyChangeListener p3Change = evt -> {
                Vector3fc oldValue = (Vector3fc) evt.getOldValue();
                Vector3fc newValue = (Vector3fc) evt.getNewValue();
                Vector3fc delta = newValue.sub(oldValue, new Vector3f()).mul(3.0f / 2.0f);
                p3.move(delta.x(), delta.y(), delta.z());
            };

            b1.addPropertyChangeListener(p2Change);
            b2.addPropertyChangeListener(p2Change);

            b3.addPropertyChangeListener(p3Change);
            b4.addPropertyChangeListener(p3Change);

            curve.addPoint(b1);
            curve.addPoint(b2);
            curve.addPoint(b3);
            if(i == controlPoints.size() - 1) curve.addPoint(b4);
        }
    }

    @Override
    public void render(GL4 gl, PerspectiveCamera camera, Renderer renderer) {
        if(showPolyLine) polyLine.render(gl, camera, renderer);
        curve.render(gl, camera, renderer);
    }

    public boolean isShowBezierPolyline() {
        return curve.isShowPolyline();
    }

    public void setShowBezierPolyline(boolean showBezierPolyline) {
        curve.setShowPolyline(showBezierPolyline);
    }

    @Override
    public void dispose(GL4 gl) {
        super.dispose(gl);
        curve.dispose(gl);
    }

    @Override
    public void validate(GL4 gl) {
        super.validate(gl);
        curve.validate(gl);
    }

    public int getSelectedVirtualPoint() {
        return selectedVirtualPoint;
    }

    public void setSelectedVirtualPoint(int selectedVirtualPoint) {
        this.selectedVirtualPoint = selectedVirtualPoint;
    }

    public boolean isShowBezierPoints() {
        return showBezierPoints;
    }

    public void setShowBezierPoints(boolean showBezierPoints) {
        this.showBezierPoints = showBezierPoints;
    }

    @Override
    public void cleanup() {
        super.cleanup();
        curve.cleanup();
    }
}
