package pl.edu.pw.mini.mg1.models;

import com.jogamp.opengl.GL4;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import pl.edu.pw.mini.mg1.cameras.PerspectiveCamera;
import pl.edu.pw.mini.mg1.graphics.Renderer;

import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BezierCurveC2 extends Curve {
    private final BezierCurveC0 curve = new BezierCurveC0(new ArrayList<>());
    private int selectedVirtualPoint = -1;

    private boolean showBezierPoints = true;

    public BezierCurveC2(List<Point> points) {
        controlPoints.addAll(points);
        controlPoints.forEach(point -> point.addPropertyChangeListener(pcl));
        reload = true;
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

    @Override
    public String serialize() {
        return """
                  <BezierC2 Name="%s">
                    <Points>
                %s
                    </Points>
                  </BezierC2>
                """.formatted(
                getName(),
                controlPoints.stream()
                        .map(p -> "      <PointRef Name=\"%s\"/>".formatted(p.getName()))
                        .collect(Collectors.joining("\n"))
        );
    }

    @Override
    public Model deserialize(Node node, Map<String, Point> points) {
        if(node.getNodeType() == Node.ELEMENT_NODE) {
            Element bezierC2Element = (Element) node;
            setName(bezierC2Element.getAttribute("Name"));

            NodeList pointsRefs = ((Element)bezierC2Element
                    .getElementsByTagName("Points").item(0))
                    .getElementsByTagName("PointRef");

            for (int i = 0; i < pointsRefs.getLength(); i++) {
                Element pointRefElement = (Element) pointsRefs.item(i);
                Point point = points.get(pointRefElement.getAttribute("Name"));
                addPoint(point);
            }
        }
        return this;
    }
}
