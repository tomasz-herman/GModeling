package pl.edu.pw.mini.mg1.models;

import com.jogamp.opengl.GL4;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.joml.*;
import pl.edu.pw.mini.mg1.milling.MaterialBlock;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class MilledBlock extends Model {

    private final MaterialBlock block;

    public MilledBlock(MaterialBlock block) {
        this.block = block;
    }

    @Override
    protected void load(GL4 gl) {
        float[][] heights = block.getHeights();
        Vector2ic resolution = block.getResolution();
        Vector2fc size = block.getSize();
        Vector2fc halfSize = size.div(2, new Vector2f());
        Vector2fc step = size.div(new Vector2f(resolution), new Vector2f());
        List<Vector3f> topPositions = new ArrayList<>();
        for (int i = 0; i < resolution.x(); i++) {
            for (int j = 0; j < resolution.y(); j++) {
                topPositions.add(new Vector3f(step.x() * i - halfSize.x(), heights[i][j], step.y() * j - halfSize.y()));
            }
        }
        List<Triple<Integer, Integer, Integer>> triangles = new ArrayList<>();
        for (int i = 0; i < resolution.x() - 1; i++) {
            for (int j = 0; j < resolution.y() - 1; j++) {
                int x00 = i * resolution.y() + j;
                int x01 = x00 + 1;
                int x10 = x00 + resolution.y();
                int x11 = x10 + 1;
                triangles.add(Triple.of(x00, x01, x11));
                triangles.add(Triple.of(x11, x10, x00));
            }
        }
        float[] positions = ArrayUtils.toPrimitive(topPositions.stream().flatMap(vec -> Stream.of(vec.x, vec.y, vec.z)).map(val -> val / 100).toArray(Float[]::new));
        int[] indices = triangles.stream().flatMapToInt(tri -> IntStream.of(tri.getLeft(), tri.getMiddle(), tri.getRight())).toArray();
        this.mesh = new Mesh(positions, indices, GL4.GL_TRIANGLES);
        mesh.load(gl);
    }

}
