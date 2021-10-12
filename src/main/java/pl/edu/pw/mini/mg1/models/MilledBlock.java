package pl.edu.pw.mini.mg1.models;

import com.jogamp.opengl.GL4;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.joml.*;
import pl.edu.pw.mini.mg1.cameras.PerspectiveCamera;
import pl.edu.pw.mini.mg1.graphics.Renderer;
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
        List<Vector3f> gridPositions = new ArrayList<>();
        for (int i = 0; i < resolution.x(); i++) {
            for (int j = 0; j < resolution.y(); j++) {
                gridPositions.add(new Vector3f(step.x() * i - halfSize.x(), heights[i][j], step.y() * j - halfSize.y()));
            }
        }
        List<Vector3f> listPositions = new ArrayList<>();
        List<Triple<Integer, Integer, Integer>> triangles = new ArrayList<>();
        int k = 0;
        for (int i = 0; i < resolution.x() - 1; i++) {
            for (int j = 0; j < resolution.y() - 1; j++) {
                int x00 = i * resolution.y() + j;
                int x01 = x00 + 1;
                int x10 = x00 + resolution.y();
                int x11 = x10 + 1;

                listPositions.add(gridPositions.get(x00));
                listPositions.add(gridPositions.get(x01));
                listPositions.add(gridPositions.get(x11));
                listPositions.add(gridPositions.get(x11));
                listPositions.add(gridPositions.get(x10));
                listPositions.add(gridPositions.get(x00));

                triangles.add(Triple.of(k, k + 1, k + 2));
                triangles.add(Triple.of(k + 3, k + 4, k + 5));

                k += 6;
            }
        }

        float[] positions = ArrayUtils.toPrimitive(listPositions.stream().flatMap(vec -> Stream.of(vec.x, vec.y, vec.z)).map(val -> val / 100).toArray(Float[]::new));

        int[] indices = triangles.stream().flatMapToInt(tri -> IntStream.of(tri.getLeft(), tri.getMiddle(), tri.getRight())).toArray();
        float[] normals = new float[positions.length];
        for (int i = 0; i < indices.length; i+=3) {
            Vector3f v0 = listPositions.get(indices[i]);
            Vector3f v1 = listPositions.get(indices[i + 1]);
            Vector3f v2 = listPositions.get(indices[i + 2]);

            Vector3f n = v2.sub(v0, new Vector3f()).cross(v1.sub(v0, new Vector3f())).normalize();
            normals[indices[i] * 3] = n.x;
            normals[indices[i] * 3 + 1] = n.y;
            normals[indices[i] * 3 + 2] = n.z;
            normals[indices[i + 1] * 3] = n.x;
            normals[indices[i + 1] * 3 + 1] = n.y;
            normals[indices[i + 1] * 3 + 2] = n.z;
            normals[indices[i + 2] * 3] = n.x;
            normals[indices[i + 2] * 3 + 1] = n.y;
            normals[indices[i + 2] * 3 + 2] = n.z;
        }

        this.mesh = new Mesh(positions, normals, null, indices, GL4.GL_TRIANGLES);
        mesh.load(gl);
    }

    @Override
    public void render(GL4 gl, PerspectiveCamera camera, Renderer renderer) {
        renderer.renderPhong(gl, camera, this);
    }

}
