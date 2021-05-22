package pl.edu.pw.mini.mg1.models;

import com.jogamp.opengl.GL4;
import org.apache.commons.lang3.ArrayUtils;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
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

public class BezierC2 extends Model implements Curve {
    private final List<Point> points = new ArrayList<>();
    private final List<Point> bezierPoints = new ArrayList<>();
    private final List<Point> invalidBezierPoints = new ArrayList<>();
    private final PolyLine polyLine = new PolyLine(points);
    private final PolyLine bezierPolyLine = new PolyLine(bezierPoints);
    private int selectedVirtualPoint = -1;
    private final PropertyChangeListener pcl = e -> {
        polyLine.reload = true;
        bezierPolyLine.reload = true;
        reload = true;
    };

    private boolean showPolyline = false;
    private boolean showBezierPolyline = true;
    private boolean showBezierPoints = true;

    public BezierC2(List<Point> points) {
        points.forEach(this::addPoint);
    }

    @Override
    public void addPoint(Point point) {
        if (!points.contains(point)) {
            points.add(point);
            point.addPropertyChangeListener(pcl);
        }
        pcl.propertyChange(null);
    }

    @Override
    public void removePoint(Point point) {
        points.remove(point);
        point.removePropertyChangeListener(pcl);
        pcl.propertyChange(null);
    }

    @Override
    public void removeAllPoints() {
        List<Point> copy = new ArrayList<>(points);
        copy.forEach(this::removePoint);
    }

    @Override
    public List<Point> getPoints() {
        return points;
    }

    public List<Point> getBezierPoints() {
        return bezierPoints;
    }

    public void calculateBezierCurve() {
        invalidBezierPoints.addAll(bezierPoints);
        bezierPoints.clear();
        bezierPolyLine.reload = true;
        int k = 0;
        for (int i = 3; i < points.size(); i++) {
            Point p1 = points.get(i - 3);
            Point p2 = points.get(i - 2);
            Point p3 = points.get(i - 1);
            Point p4 = points.get(i);

            Vector3fc v1 = p1.getTransformedPosition();
            Vector3fc v2 = p2.getTransformedPosition();
            Vector3fc v3 = p3.getTransformedPosition();
            Vector3fc v4 = p4.getTransformedPosition();

            Point b1 = new Point();
            Point b2 = new Point();
            Point b3 = new Point();
            Point b4 = new Point();

            b1.setName("Bezier virtual point %d".formatted(k++));
            b2.setName("Bezier virtual point %d".formatted(k++));
            b3.setName("Bezier virtual point %d".formatted(k++));

            Vector3f b2Pos = v2.lerp(v3, 1.0f / 3.0f, new Vector3f());
            Vector3f b3Pos = v2.lerp(v3, 2.0f / 3.0f, new Vector3f());
            Vector3f b1Pos = v1.lerp(v2, 2.0f / 3.0f, new Vector3f());
            Vector3f b4Pos = v3.lerp(v4, 1.0f / 3.0f, new Vector3f());
            b1Pos.lerp(b2Pos, 0.5f);
            b4Pos.lerp(b3Pos, 0.5f);

            b1.setPosition(b1Pos.x, b1Pos.y, b1Pos.z);
            b2.setPosition(b2Pos.x, b2Pos.y, b2Pos.z);
            b3.setPosition(b3Pos.x, b3Pos.y, b3Pos.z);
            b4.setPosition(b4Pos.x, b4Pos.y, b4Pos.z);

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

            bezierPoints.addAll(List.of(b1, b2, b3));
            if(i == points.size() - 1) {
                b4.setName("Bezier virtual point %d".formatted(k++));
                bezierPoints.add(b4);
            }
        }
    }

    @Override
    protected void load(GL4 gl) {
        calculateBezierCurve();
        points.forEach(p -> p.addPropertyChangeListener(pcl));
        bezierPoints.forEach(p -> p.load(gl));
        bezierPoints.forEach(p -> p.addPropertyChangeListener(pcl));
        float[] positions = ArrayUtils.toPrimitive(bezierPoints.stream()
                .map(Model::getTransformedPosition)
                .flatMap(pos -> Stream.of(pos.x(), pos.y(), pos.z()))
                .toArray(Float[]::new));
        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < bezierPoints.size(); i += 3) {
            indices.add(Math.min(i, bezierPoints.size() - 1));
            indices.add(Math.min(i + 1, bezierPoints.size() - 1));
            indices.add(Math.min(i + 2, bezierPoints.size() - 1));
            indices.add(Math.min(i + 3, bezierPoints.size() - 1));
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
        if(showBezierPoints) {
            for (int i = 0; i < bezierPoints.size(); i++) {
                Point p = bezierPoints.get(i);
                if(i == selectedVirtualPoint) gl.glVertexAttrib3f(1, 1, 0.25f, 0);
                p.render(gl, camera, renderer);
                if(i == selectedVirtualPoint) gl.glVertexAttrib3f(1, 1, 1, 1);
            }
        }
        if(showPolyline) polyLine.render(gl, camera, renderer);
        if(showBezierPolyline) bezierPolyLine.render(gl, camera, renderer);
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
    public void setTransformationMatrix(Matrix4fc transformation) { }

    @Override
    public void applyTransformationMatrix() { }

    @Override
    public void setPosition(float x, float y, float z) { }

    @Override
    public void setRotation(float x, float y, float z) { }

    @Override
    public void setScale(float x, float y, float z) { }

    public boolean isShowPolyline() {
        return showPolyline;
    }

    public void setShowPolyline(boolean showPolyline) {
        this.showPolyline = showPolyline;
    }

    public boolean isShowBezierPolyline() {
        return showBezierPolyline;
    }

    public void setShowBezierPolyline(boolean showBezierPolyline) {
        this.showBezierPolyline = showBezierPolyline;
    }

    @Override
    public void dispose(GL4 gl) {
        super.dispose(gl);
        points.forEach(p -> p.removePropertyChangeListener(pcl));
        bezierPoints.forEach(p -> p.removePropertyChangeListener(pcl));
        polyLine.dispose(gl);
        bezierPolyLine.dispose(gl);
        invalidBezierPoints.forEach(p -> p.dispose(gl));
        invalidBezierPoints.clear();
    }

    @Override
    public void validate(GL4 gl) {
        super.validate(gl);
        polyLine.validate(gl);
        bezierPolyLine.validate(gl);
        invalidBezierPoints.forEach(p -> p.dispose(gl));
        invalidBezierPoints.clear();
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
    public String serialize() {
        return """
                  <BezierC2 Name="%s" ShowBernsteinPoints="%d" ShowBernsteinPolygon="%d" ShowDeBoorPolygon="%d">
                    <Points>
                %s
                    </Points>
                  </BezierC2>
                """.formatted(
                getName(),
                0,
                0,
                0,
                points.stream()
                        .map(p -> "      <PointRef Name=\"%s\"/>".formatted(p.getName()))
                        .collect(Collectors.joining("\n"))
        );
    }

    @Override
    public Model deserialize(Node node, Map<String, Point> points) {
        if(node.getNodeType() == Node.ELEMENT_NODE) {
            Element bezierC2Element = (Element) node;
            setName(bezierC2Element.getAttribute("Name"));
            setShowPolyline(0 != Integer.parseInt(bezierC2Element.getAttribute("ShowControlPolygon")));

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
