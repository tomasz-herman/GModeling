package pl.edu.pw.mini.mg1.models;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL4;
import org.apache.commons.lang3.ArrayUtils;
import org.joml.Vector3f;
import pl.edu.pw.mini.mg1.cameras.PerspectiveCamera;
import pl.edu.pw.mini.mg1.graphics.Renderer;
import pl.edu.pw.mini.mg1.milling.MillingTool;

import java.util.ArrayList;
import java.util.List;

import static com.jogamp.opengl.math.FloatUtil.PI;
import static org.joml.Math.cos;
import static org.joml.Math.sin;

public class Cutter extends Model {

    private final MillingTool tool;

    public Cutter(MillingTool tool) {
        this.tool = tool;
    }

    @Override
    protected void load(GL4 gl) {
        float radius = tool.getRadius() / 100;
        float length = tool.getLength() / 100;
        if (tool.isFlat()) {

        } else {
            short rings = 50;
            short sectors = 50;
            float R = 1f / (rings - 1);
            float S = 1f / (sectors - 1);
            short r, s;
            float x, y, z;

            List<Float> positions = new ArrayList<>(rings * sectors * 3);
            List<Float> normals = new ArrayList<>(rings * sectors * 3);
            List<Float> texCoords = new ArrayList<>(rings * sectors * 2);

            for (r = 0; r < rings; r++) {
                for (s = 0; s < sectors; s++) {
                    x = cos(2 * PI * s * S) * sin(PI * r * R);
                    y = sin(-PI / 2 + PI * r * R);
                    z = sin(2 * PI * s * S) * sin(PI * r * R);
                    positions.add(x * radius);
                    positions.add(y * radius + radius);
                    positions.add(z * radius);
                    Vector3f normal = new Vector3f(x, y, z).normalize();
                    normals.add(normal.x);
                    normals.add(normal.y);
                    normals.add(normal.z);
                    texCoords.add(1 - s * S);
                    texCoords.add(1 - r * R);
                }
            }

            List<Integer> indices = new ArrayList<>(rings * sectors * 6);

            for (r = 0; r < rings - 1; r++) {
                for (s = 0; s < sectors - 1; s++) {
                    indices.add((r * sectors + s));
                    indices.add((r * sectors + (s + 1)));
                    indices.add(((r + 1) * sectors + (s + 1)));
                    indices.add(((r + 1) * sectors + (s + 1)));
                    indices.add((r * sectors + s));
                    indices.add(((r + 1) * sectors + s));
                }
            }
            mesh = new Mesh(
                    ArrayUtils.toPrimitive(positions.toArray(new Float[0])),
                    ArrayUtils.toPrimitive(normals.toArray(new Float[0])),
                    ArrayUtils.toPrimitive(texCoords.toArray(new Float[0])),
                    ArrayUtils.toPrimitive(indices.toArray(new Integer[0])),
                    GL.GL_TRIANGLES);
            mesh.load(gl);
        }

    }

    @Override
    public void render(GL4 gl, PerspectiveCamera camera, Renderer renderer) {
        renderer.renderPhong(gl, camera, this);
    }
}
