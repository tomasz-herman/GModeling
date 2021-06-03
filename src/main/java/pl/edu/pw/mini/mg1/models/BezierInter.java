package pl.edu.pw.mini.mg1.models;

import org.joml.Vector3f;
import pl.edu.pw.mini.mg1.numerics.SplineSmoothInterpolation;

import java.util.List;

public class BezierInter extends Curve {
    public BezierInter(List<Point> points) {
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
}
