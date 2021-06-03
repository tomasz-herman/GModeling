package pl.edu.pw.mini.mg1.models;

import java.util.List;

public class BezierC0 extends Curve {
    public BezierC0(List<Point> points) {
        super(points);
    }

    @Override
    protected void fillPointsList() {
        pointsList.addAll(controlPoints);
    }
}
