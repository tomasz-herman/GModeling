package pl.edu.pw.mini.mg1.models;

import com.jogamp.opengl.GL4;
import org.apache.commons.lang3.ArrayUtils;
import pl.edu.pw.mini.mg1.cameras.PerspectiveCamera;
import pl.edu.pw.mini.mg1.graphics.Renderer;

import java.util.List;
import java.util.stream.IntStream;

public class BezierPatchC0 extends Model {
    @Override
    protected void setupBoundingVolume() {
        this.boundingVolume = null;
    }

    @Override
    protected void load(GL4 gl) {
        float[] positions = {
                0.0f, 2.0f, 0.0f,
                1.0f, 1.0f, 0.0f,
                2.0f, 1.0f, 0.0f,
                3.0f, 2.0f, 0.0f,

                0.0f, 1.0f, 1.0f,
                1.0f, -2.0f, 1.0f,
                2.0f, 1.0f, 1.0f,
                3.0f, 0.0f, 1.0f,

                0.0f, 0.0f, 2.0f,
                1.0f, 1.0f, 2.0f,
                2.0f, 0.0f, 2.0f,
                3.0f, -1.0f, 2.0f,

                0.0f, 0.0f, 3.0f,
                1.0f, 1.0f, 3.0f,
                2.0f, -1.0f, 3.0f,
                3.0f, -1.0f, 3.0f
        };
        List<Integer> indices = IntStream.range(0, 16).boxed().toList();
        this.mesh = new Mesh(
                positions,
                ArrayUtils.toPrimitive(indices.toArray(new Integer[0])),
                GL4.GL_PATCHES);
        mesh.load(gl);
    }

    @Override
    public void render(GL4 gl, PerspectiveCamera camera, Renderer renderer) {
        renderer.renderPatch(gl, camera, this);
    }
}
