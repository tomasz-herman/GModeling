package pl.edu.pw.mini.mg1.models;

import com.jogamp.opengl.GL4;
import org.apache.commons.lang3.ArrayUtils;
import pl.edu.pw.mini.mg1.cameras.PerspectiveCamera;
import pl.edu.pw.mini.mg1.graphics.Renderer;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.jogamp.opengl.math.FloatUtil.PI;
import static org.joml.Math.cos;
import static org.joml.Math.sin;

public class BezierPatchC0 extends Model {

    private final List<Point> points = new ArrayList<>();
    private int divisions = 3;

    public static BezierPatchC0 example() {
        BezierPatchC0 patch = new BezierPatchC0();
        patch.points.addAll(List.of(
                new Point(0.0f, 2.0f, 0.0f),
                new Point(1.0f, 1.0f, 0.0f),
                new Point(2.0f, 1.0f, 0.0f),
                new Point(3.0f, 2.0f, 0.0f),

                new Point(0.0f, 1.0f, 1.0f),
                new Point(1.0f, -2.0f, 1.0f),
                new Point(2.0f, 1.0f, 1.0f),
                new Point(3.0f, 0.0f, 1.0f),

                new Point(0.0f, 0.0f, 2.0f),
                new Point(1.0f, 1.0f, 2.0f),
                new Point(2.0f, 0.0f, 2.0f),
                new Point(3.0f, -1.0f, 2.0f),

                new Point(0.0f, 0.0f, 3.0f),
                new Point(1.0f, 1.0f, 3.0f),
                new Point(2.0f, -1.0f, 3.0f),
                new Point(7.0f, -1.0f, 3.0f)
        ));
        return patch;
    }

    public static BezierPatchC0 flat(float w, float h, int x, int y) {
        BezierPatchC0 patch = new BezierPatchC0();
        int xp = 4 + (x - 1) * 3;
        int yp = 4 + (y - 1) * 3;
        Point[][] surface = new Point[xp][yp];
        float wx = w / (x * 3);
        float hy = h / (y * 3);
        for (int i = 0; i < xp; i++) {
            for (int j = 0; j < yp; j++) {
                surface[i][j] = new Point(wx * i, 0, hy * j);
            }
        }
        for (int i = 0; i < xp - 1; i+=3) {
            for (int j = 0; j < yp - 1; j+=3) {
                patch.points.addAll(List.of(
                        surface[i][j],
                        surface[i + 1][j],
                        surface[i + 2][j],
                        surface[i + 3][j],

                        surface[i][j + 1],
                        surface[i + 1][j + 1],
                        surface[i + 2][j + 1],
                        surface[i + 3][j + 1],

                        surface[i][j + 2],
                        surface[i + 1][j + 2],
                        surface[i + 2][j + 2],
                        surface[i + 3][j + 2],

                        surface[i][j + 3],
                        surface[i + 1][j + 3],
                        surface[i + 2][j + 3],
                        surface[i + 3][j + 3]
                ));
            }
        }
        return patch;
    }

    public static BezierPatchC0 cylinder(float r, float h, int x, int y) {
        BezierPatchC0 patch = new BezierPatchC0();
        int xp = x * 3;
        int yp = 4 + (y - 1) * 3;
        Point[][] surface = new Point[xp][yp];
        float wx = 2 * PI / (x * 3);
        float hy = h / (y * 3);
        Function<Float, Float> fx = phi -> r * cos(phi);
        Function<Float, Float> fz = phi -> r * sin(phi);
        for (int i = 0; i < xp; i++) {
            for (int j = 0; j < yp; j++) {
                surface[i][j] = new Point(fx.apply(wx * i), hy * j, fz.apply(wx * i));
            }
        }
        Function<Integer, Integer> mod = i -> i % xp;
        for (int i = 0; i < xp; i+=3) {
            for (int j = 0; j < yp - 1; j+=3) {
                patch.points.addAll(List.of(
                        surface[mod.apply(i)][j],
                        surface[mod.apply(i + 1)][j],
                        surface[mod.apply(i + 2)][j],
                        surface[mod.apply(i + 3)][j],

                        surface[mod.apply(i)][j + 1],
                        surface[mod.apply(i + 1)][j + 1],
                        surface[mod.apply(i + 2)][j + 1],
                        surface[mod.apply(i + 3)][j + 1],

                        surface[mod.apply(i)][j + 2],
                        surface[mod.apply(i + 1)][j + 2],
                        surface[mod.apply(i + 2)][j + 2],
                        surface[mod.apply(i + 3)][j + 2],

                        surface[mod.apply(i)][j + 3],
                        surface[mod.apply(i + 1)][j + 3],
                        surface[mod.apply(i + 2)][j + 3],
                        surface[mod.apply(i + 3)][j + 3]
                ));
            }
        }
        return patch;
    }

    @Override
    protected void setupBoundingVolume() {
        this.boundingVolume = null;
    }

    @Override
    protected void load(GL4 gl) {
        Float[] positions = points.stream()
                .map(Point::getTransformedPosition)
                .flatMap(v -> Stream.of(v.x(), v.y(), v.z()))
                .toArray(Float[]::new);
        int[] indices = IntStream.range(0, positions.length).toArray();
        this.mesh = new Mesh(
                ArrayUtils.toPrimitive(positions),
                indices,
                GL4.GL_PATCHES);
        mesh.load(gl);
    }

    @Override
    public void render(GL4 gl, PerspectiveCamera camera, Renderer renderer) {
        renderer.renderPatch(gl, camera, this);
    }

    public int getDivisions() {
        return divisions;
    }

    public void setDivisions(int divisions) {
        this.divisions = divisions;
    }
}
