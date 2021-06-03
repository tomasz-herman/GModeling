package pl.edu.pw.mini.mg1.models;

import com.jogamp.opengl.GL4;
import org.apache.commons.lang3.ArrayUtils;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class PolyMesh extends Model {
    private final Point[][] points;

    public PolyMesh(Point[][] points) {
        this.points = points;
    }

    @Override
    protected void load(GL4 gl) {
        List<Float> positions = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();
        int idx = 0;
        for (int i = 0; i < points.length; i++) {
            for (int j = 0; j < points[i].length; j++) {
                Vector3f pos = points[i][j].getTransformedPosition().get(new Vector3f());
                positions.addAll(List.of(pos.x, pos.y, pos.z));
                if(j + 1 < points[i].length) {
                    indices.addAll(List.of(idx, idx + 1));
                }
                if(i + 1 < points.length){
                    indices.addAll(List.of(idx, idx + points[i].length));
                }
                idx++;
            }
        }
        this.mesh = new Mesh(
                ArrayUtils.toPrimitive(positions.toArray(Float[]::new)),
                ArrayUtils.toPrimitive(indices.toArray(Integer[]::new)),
                GL4.GL_LINES);
        mesh.load(gl);
    }
}
