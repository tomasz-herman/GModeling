package pl.edu.pw.mini.mg1.models;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BezierCurveC0 extends Curve {
    public BezierCurveC0(List<Point> points) {
        controlPoints.addAll(points);
        controlPoints.forEach(point -> point.addPropertyChangeListener(pcl));
        reload = true;
    }

    @Override
    protected void fillPointsList() {
        pointsList.addAll(controlPoints);
    }

    @Override
    public String serialize() {
        return """
                  <BezierC0 Name="%s">
                    <Points>
                %s
                    </Points>
                  </BezierC0>
                """.formatted(
                getName(),
                controlPoints.stream()
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
