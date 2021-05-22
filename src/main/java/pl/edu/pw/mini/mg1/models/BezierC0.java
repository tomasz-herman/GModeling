package pl.edu.pw.mini.mg1.models;

import com.jogamp.opengl.GL4;
import org.apache.commons.lang3.ArrayUtils;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
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

public class BezierC0 extends Model implements Curve {
    private final List<Point> points = new ArrayList<>();
    private final PolyLine polyLine = new PolyLine(points);
    private final PropertyChangeListener pcl = e -> {
        polyLine.reload = true;
        reload = true;
    };

    private boolean showPolyline = true;

    public BezierC0(List<Point> points) {
        points.forEach(this::addPoint);
    }

    @Override
    public void addPoint(Point point) {
        if (!points.contains(point)) {
            points.add(point);
            point.addPropertyChangeListener(pcl);
        }
        polyLine.reload = true;
        reload = true;
    }

    @Override
    public void removePoint(Point point) {
        points.remove(point);
        point.removePropertyChangeListener(pcl);
        polyLine.reload = true;
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
    protected void load(GL4 gl) {
        points.forEach(p -> p.addPropertyChangeListener(pcl));
        float[] positions = ArrayUtils.toPrimitive(points.stream()
                .map(Model::getTransformedPosition)
                .flatMap(pos -> Stream.of(pos.x(), pos.y(), pos.z()))
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
        if(showPolyline) polyLine.render(gl, camera, renderer);
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

    @Override
    public void dispose(GL4 gl) {
        super.dispose(gl);
        points.forEach(p -> p.removePropertyChangeListener(pcl));
        polyLine.dispose(gl);
    }

    @Override
    public void validate(GL4 gl) {
        super.validate(gl);
        polyLine.validate(gl);
    }

    @Override
    public String serialize() {
        return """
                  <BezierC0 Name="%s" ShowBernsteinPoints="%d" ShowBernsteinPolygon="%d" ShowDeBoorPolygon="%d">
                    <Points>
                %s
                    </Points>
                  </BezierC0>
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

    /**
     *   <BezierC0 Name="BezierC0_001" ShowControlPolygon="0">
     *     <Points>
     *       <PointRef Name="Point_004"/>
     *       <PointRef Name="Point_001"/>
     *       <PointRef Name="Point_002"/>
     *       <PointRef Name="Point_003"/>
     *     </Points>
     *   </BezierC0>
     */
    @Override
    public Model deserialize(Node node, Map<String, Point> points) {
        if(node.getNodeType() == Node.ELEMENT_NODE) {
            Element bezierC0Element = (Element) node;
            setName(bezierC0Element.getAttribute("Name"));
            setShowPolyline(0 != Integer.parseInt(bezierC0Element.getAttribute("ShowControlPolygon")));

            NodeList pointsRefs = ((Element)bezierC0Element
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
