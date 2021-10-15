package pl.edu.pw.mini.mg1.models;

import com.jogamp.opengl.GL4;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.joml.*;
import pl.edu.pw.mini.mg1.cameras.PerspectiveCamera;
import pl.edu.pw.mini.mg1.graphics.Renderer;
import pl.edu.pw.mini.mg1.graphics.Texture;
import pl.edu.pw.mini.mg1.milling.MaterialBlock;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class MilledBlock extends Model {

    private final MaterialBlock block;
    private Texture texture;

    public MilledBlock(MaterialBlock block) {
        this.block = block;
    }

    @Override
    protected void load(GL4 gl) {
        float[] heights = block.getHeights();
        Vector2ic resolution = block.getResolution();
        texture = new Texture(gl, heights, resolution.x(), resolution.y());
        Vector2fc size = block.getSize();
        Vector2fc halfSize = size.div(2, new Vector2f());
        Vector2fc step = size.div(new Vector2f(resolution), new Vector2f());
        List<Vector2f> positions = new ArrayList<>();
        List<Vector2f> textures = new ArrayList<>();
        for (int i = 0; i < resolution.x(); i++) {
            for (int j = 0; j < resolution.y(); j++) {
                positions.add(new Vector2f(step.x() * i - halfSize.x(), step.y() * j - halfSize.y()));
                textures.add(new Vector2f(i, j));
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


        float[] pos = ArrayUtils.toPrimitive(positions.stream().flatMap(vec -> Stream.of(vec.x, 0f, vec.y)).map(val -> val / 100).toArray(Float[]::new));
        float[] tex = ArrayUtils.toPrimitive(textures.stream().flatMap(vec -> Stream.of(vec.x / (resolution.x() - 1), vec.y / (resolution.y() - 1))).toArray(Float[]::new));
        int[] ind = triangles.stream().flatMapToInt(tri -> IntStream.of(tri.getLeft(), tri.getMiddle(), tri.getRight())).toArray();

        this.mesh = new Mesh(pos, null, tex, ind, GL4.GL_TRIANGLES);
        mesh.load(gl);
    }

    @Override
    public void render(GL4 gl, PerspectiveCamera camera, Renderer renderer) {
        renderer.renderTerrain(gl, camera, this, texture);
    }

    @Override
    public void dispose(GL4 gl) {
        super.dispose(gl);
        if(texture != null) texture.dispose(gl);
    }

    public Texture getTexture() {
        return texture;
    }
}
