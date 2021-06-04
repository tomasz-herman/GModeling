package pl.edu.pw.mini.mg1.models;

import com.jogamp.opengl.GL4;
import org.apache.commons.lang3.ArrayUtils;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class GregoryMesh extends Model {
    private final List<Point> points;

    public GregoryMesh(List<Point> points) {
        this.points = points;
    }

    @Override
    protected void load(GL4 gl) {
        List<Float> positions = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();
        for (Point point : points) {
            Vector3f pos = point.getTransformedPosition().get(new Vector3f());
            positions.addAll(List.of(pos.x, pos.y, pos.z));
        }
        indices.addAll(createIndices(0));
        indices.addAll(createIndices(20));
        indices.addAll(createIndices(40));
        this.mesh = new Mesh(
                ArrayUtils.toPrimitive(positions.toArray(Float[]::new)),
                ArrayUtils.toPrimitive(indices.toArray(Integer[]::new)),
                GL4.GL_LINES);
        mesh.load(gl);
    }

    private List<Integer> createIndices(int offset) {
        return List.of(
                0 + offset, 1 + offset,
                1 + offset, 2 + offset,
                2 + offset, 3 + offset,
                0 + offset, 4 + offset,
                1 + offset, 6 + offset,
                2 + offset, 7 + offset,
                3 + offset, 9 + offset,
                4 + offset, 5 + offset,
                8 + offset, 9 + offset,
                4 + offset, 10 + offset,
                9 + offset, 15 + offset,
                10 + offset, 11 + offset,
                14 + offset, 15 + offset,
                10 + offset, 16 + offset,
                12 + offset, 17 + offset,
                13 + offset, 18 + offset,
                15 + offset, 19 + offset,
                16 + offset, 17 + offset,
                17 + offset, 18 + offset,
                18 + offset, 19 + offset
        );
    }
}
