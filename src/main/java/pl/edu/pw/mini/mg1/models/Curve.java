package pl.edu.pw.mini.mg1.models;

import java.util.List;

public interface Curve {
    void addPoint(Point point);
    void removePoint(Point point);
    void removeAllPoints();
    List<Point> getPoints();
}
