package pl.edu.pw.mini.mg1.models;

import com.jogamp.opengl.GL4;
import org.apache.commons.lang3.ArrayUtils;

import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class PolyLine extends Model {
    private final List<Point> points;

    public PolyLine(List<Point> points) {
        this.points = points;
    }

    @Override
    protected void load(GL4 gl) {
        float[] positions = ArrayUtils.toPrimitive(points.stream()
                .map(Model::getTransformedPosition)
                .flatMap(pos -> Stream.of(pos.x(), pos.y(), pos.z()))
                .toArray(Float[]::new));
        int[] indices = IntStream.range(0, points.size()).toArray();
        this.mesh = new Mesh(
                positions,
                indices,
                GL4.GL_LINE_STRIP);
        mesh.load(gl);
    }
}
