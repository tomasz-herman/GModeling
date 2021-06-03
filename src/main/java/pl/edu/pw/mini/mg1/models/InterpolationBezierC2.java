package pl.edu.pw.mini.mg1.models;

import com.jogamp.opengl.GL4;
import org.apache.commons.lang3.ArrayUtils;
import org.joml.Vector3fc;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import pl.edu.pw.mini.mg1.cameras.PerspectiveCamera;
import pl.edu.pw.mini.mg1.graphics.Renderer;
import pl.edu.pw.mini.mg1.numerics.SplineInterpolation;

import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class InterpolationBezierC2 extends Curve {
    private final List<Point> points = new ArrayList<>();
    private final BezierCurveC2 bezierCurveC2 = new BezierCurveC2(new ArrayList<>());
    private final PropertyChangeListener pcl = e -> reload = true;
    private boolean showPolyline = true;

    public InterpolationBezierC2(List<Point> points) {
        points.forEach(this::addPoint);
    }

    @Override
    protected void fillPointsList() {

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
    public void validate(GL4 gl) {
        super.validate(gl);
        bezierCurveC2.validate(gl);
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

        bezierCurveC2.removeAllPoints();
        points.forEach(bezierCurveC2::addPoint);
        bezierCurveC2.load(gl);
    }

    @Override
    public void render(GL4 gl, PerspectiveCamera camera, Renderer renderer) {
        if(bezierCurveC2 != null) {
            bezierCurveC2.setShowPolyline(false);
            bezierCurveC2.setShowBezierPoints(false);
            bezierCurveC2.setShowBezierPolyline(showPolyline);
            bezierCurveC2.render(gl, camera, renderer);
        }
    }

    public boolean isShowPolyline() {
        return showPolyline;
    }

    public void setShowPolyline(boolean showPolyline) {
        this.showPolyline = showPolyline;
    }

    @Override
    public String serialize() {
        return """
                  <BezierInter Name="%s">
                    <Points>
                %s
                    </Points>
                  </BezierInter>
                """.formatted(
                getName(),
                points.stream()
                        .map(p -> "      <PointRef Name=\"%s\"/>".formatted(p.getName()))
                        .collect(Collectors.joining("\n"))
        );
    }

    @Override
    public Model deserialize(Node node, Map<String, Point> points) {
        if(node.getNodeType() == Node.ELEMENT_NODE) {
            Element bezierC0Element = (Element) node;
            setName(bezierC0Element.getAttribute("Name"));

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
