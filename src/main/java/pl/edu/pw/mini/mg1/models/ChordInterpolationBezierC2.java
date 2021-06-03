package pl.edu.pw.mini.mg1.models;

import org.joml.Vector3f;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import pl.edu.pw.mini.mg1.numerics.SplineSmoothInterpolation;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ChordInterpolationBezierC2 extends Curve {
    public ChordInterpolationBezierC2(List<Point> points) {
        controlPoints.addAll(points);
        controlPoints.forEach(point -> point.addPropertyChangeListener(pcl));
        showPolyLine = false;
        reload = true;
    }

    @Override
    protected void fillPointsList() {
        SplineSmoothInterpolation ssi = new SplineSmoothInterpolation(
                controlPoints.stream()
                        .map(Model::getTransformedPosition)
                        .map(Vector3f::new)
                        .toArray(Vector3f[]::new));

        var knots = ssi.solve();

        pointsList.clear();

        for (int i = 0; i < knots.size(); i+=4) {
            Vector3f knotA = knots.get(i);
            Vector3f knotB = knots.get(i + 1);
            Vector3f knotC = knots.get(i + 2);
            Vector3f knotD = knots.get(i + 3);

            pointsList.add(new Point(knotA.x, knotA.y, knotA.z));
            pointsList.add(new Point(knotB.x, knotB.y, knotB.z));
            pointsList.add(new Point(knotC.x, knotC.y, knotC.z));
            if(i + 4 == knots.size()) pointsList.add(new Point(knotD.x, knotD.y, knotD.z));
        }
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
                controlPoints.stream()
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
