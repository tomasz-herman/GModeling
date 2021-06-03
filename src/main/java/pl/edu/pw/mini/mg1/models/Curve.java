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

public abstract class Curve extends Model {
    protected final PropertyChangeListener pcl = e -> reload = true;

    protected final List<Point> controlPoints = new ArrayList<>();
    protected final List<Point> pointsList = new ArrayList<>();

    protected PolyLine polyLine = new PolyLine(controlPoints);
    protected boolean showPolyLine = false;

    public Curve(List<Point> points) {
        controlPoints.addAll(points);
        controlPoints.forEach(point -> point.addPropertyChangeListener(pcl));
        reload = true;
    }

    protected abstract void fillPointsList();

    public void addPoint(Point point) {
        if (!controlPoints.contains(point)) {
            controlPoints.add(point);
            point.addPropertyChangeListener(pcl);
        }
        reload = true;
    }

    public void removePoint(Point point) {
        point.removePropertyChangeListener(pcl);
        controlPoints.remove(point);
        reload = true;
    }

    public void removeAllPoints() {
        controlPoints.forEach(point -> point.removePropertyChangeListener(pcl));
        controlPoints.clear();
        reload = true;
    }

    public Stream<Point> getPoints() {
        return controlPoints.stream();
    }

    public void replacePoint(Point replaced, Point replacement) {
        controlPoints.replaceAll(point -> point == replaced ? replacement : point);
        replaced.removePropertyChangeListener(pcl);
        replacement.addPropertyChangeListener(pcl);
        reload = true;
    }

    @Override
    public Matrix4fc getModelMatrix() {
        return new Matrix4f();
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

    @Override
    protected void load(GL4 gl) {
        fillPointsList();
        float[] positions = ArrayUtils.toPrimitive(pointsList.stream()
                .map(Model::getTransformedPosition)
                .flatMap(pos -> Stream.of(pos.x(), pos.y(), pos.z()))
                .toArray(Float[]::new));
        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < pointsList.size(); i += 3) {
            indices.add(Math.min(i, pointsList.size() - 1));
            indices.add(Math.min(i + 1, pointsList.size() - 1));
            indices.add(Math.min(i + 2, pointsList.size() - 1));
            indices.add(Math.min(i + 3, pointsList.size() - 1));
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
        if(showPolyLine) polyLine.render(gl, camera, renderer);
    }

    public boolean isShowPolyline() {
        return showPolyLine;
    }

    public void setShowPolyline(boolean showPolyline) {
        this.showPolyLine = showPolyline;
    }

    @Override
    public void validate(GL4 gl) {
        super.validate(gl);
        polyLine.validate(gl);
    }

    @Override
    public void dispose(GL4 gl) {
        super.dispose(gl);
        polyLine.dispose(gl);
        pointsList.clear();
    }

    @Override
    public void cleanup() {
        controlPoints.forEach(point -> point.removePropertyChangeListener(pcl));
    }

    @Override
    public String serialize() {
        return """
                  <%s Name="%s">
                    <Points>
                %s
                    </Points>
                  </%s>
                """.formatted(
                getClass().getSimpleName(),
                getName(),
                controlPoints.stream()
                        .map(p -> "      <PointRef Name=\"%s\"/>".formatted(p.getName()))
                        .collect(Collectors.joining("\n")),
                getClass().getSimpleName()
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
            Element curveElement = (Element) node;
            setName(curveElement.getAttribute("Name"));

            NodeList pointsRefs = ((Element)curveElement
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
