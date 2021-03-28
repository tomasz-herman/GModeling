package pl.edu.pw.mini.mg1.models;

import com.jogamp.opengl.GL4;
import org.apache.commons.lang3.ArrayUtils;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import pl.edu.pw.mini.mg1.cameras.PerspectiveCamera;
import pl.edu.pw.mini.mg1.numerics.DeCasteljau;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class BezierC0 extends Model {
    private final List<Point> points;
    private final List<BezierC0Segment> segments = new ArrayList<>();
    private final PerspectiveCamera camera;

    private boolean showPolyline = true;

    public BezierC0(List<Point> points, PerspectiveCamera camera) {
        this.points = points;
        this.camera = camera;
        recalculate();
    }

    public void addPoint(Point point) {
        points.add(point);
        recalculate();
    }

    public void removePoint(Point point) {
        points.remove(point);
        recalculate();
    }

    private void recalculate() {
        segments.clear();
        List<Point> segment = new ArrayList<>();
        for (Point point : points) {
            segment.add(point);
            if (segment.size() == 4) {
                segments.add(new BezierC0Segment(segment));
                segment.clear();
                segment.add(point);
            }
        }
        if (segment.size() > 1) {
            segments.add(new BezierC0Segment(segment));
        }
    }

    @Override
    protected void load(GL4 gl) {
        Stream<Float> vertices = Stream.empty();
        for (var segment : segments) vertices = Stream.concat(vertices, segment.getPositions());
        if(showPolyline) {
            vertices = Stream.concat(
                    IntStream.range(0, points.size())
                            .mapToObj(i -> points.get(points.size() - i - 1)) // reverse order
                            .map(Model::getTransformedPosition)
                            .flatMap(v -> Stream.of(v.x(), v.y(), v.z())),
                    vertices);
        }
        List<Float> positions = vertices.collect(Collectors.toList());
        mesh = new Mesh(
                ArrayUtils.toPrimitive(positions.toArray(new Float[0])),
                ArrayUtils.toPrimitive(IntStream.concat(
                        IntStream.concat(
                                IntStream.of(0),
                                IntStream.range(1, positions.size() / 3  - 1)
                                        .flatMap(i -> IntStream.of(i, i))),
                        IntStream.of(positions.size() / 3  - 1))
                        .boxed().toArray(Integer[]::new)),
                GL4.GL_LINES);
        mesh.load(gl);
    }

    @Override
    public void validate(GL4 gl) {
        recalculate();
        dispose(gl);
        load(gl);
    }

    @Override
    public Matrix4fc getModelMatrix() {
        return new Matrix4f();
    }

    @Override
    public void setTransformationMatrix(Matrix4fc transformation) {
    }

    @Override
    public void applyTransformationMatrix() {
    }

    @Override
    public void setPosition(float x, float y, float z) {
    }

    @Override
    public void setRotation(float x, float y, float z) {
    }

    @Override
    public void setScale(float x, float y, float z) {
    }

    public boolean isShowPolyline() {
        return showPolyline;
    }

    public void setShowPolyline(boolean showPolyline) {
        this.showPolyline = showPolyline;
    }

    private class BezierC0Segment {
        public static final int MAX_DIVISIONS = 10000;
        private final List<Point> points;
        private final DeCasteljau[] solvers;

        private BezierC0Segment(List<Point> points) {
            this.points = new ArrayList<>(points);
            solvers = new DeCasteljau[]{
                    new DeCasteljau(ArrayUtils.toPrimitive(points.stream().map(p -> p.getTransformedPosition().x()).toArray(Float[]::new))),
                    new DeCasteljau(ArrayUtils.toPrimitive(points.stream().map(p -> p.getTransformedPosition().y()).toArray(Float[]::new))),
                    new DeCasteljau(ArrayUtils.toPrimitive(points.stream().map(p -> p.getTransformedPosition().z()).toArray(Float[]::new)))
            };
        }

        private Stream<Float> getPositions() {
            var cords = points.stream()
                    .map(p -> camera.project(p.getTransformedPosition()))
                    .collect(Collectors.toList());
            var width = cords.stream()
                    .mapToInt(c -> (int)c.x)
                    .summaryStatistics();
            var height = cords.stream()
                    .mapToInt(c -> (int)c.y)
                    .summaryStatistics();
            int res = Math.min(Math.max(width.getMax() - width.getMin(), height.getMax() - height.getMin()), MAX_DIVISIONS);
            if (res == 0) return Stream.empty();
            return IntStream.rangeClosed(0, res)
                    .mapToObj(i -> (float) i / res)
                    .flatMap(t -> Stream.of(solvers[0].solve(t), solvers[1].solve(t), solvers[2].solve(t)));
        }
    }
}
