package pl.edu.pw.mini.mg1.models;

import com.jogamp.opengl.GL4;
import org.apache.commons.lang3.ArrayUtils;
import pl.edu.pw.mini.mg1.cameras.PerspectiveCamera;
import pl.edu.pw.mini.mg1.graphics.Renderer;

import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.jogamp.opengl.math.FloatUtil.PI;
import static org.joml.Math.cos;
import static org.joml.Math.sin;

public class BezierPatchC0 extends Model {

    private final PropertyChangeListener pcl = e -> reload = true;

    private Point[][] surface;
    private final List<Point> points = new ArrayList<>();
    private int divisions = 3;

    private PolyMesh polyMesh;
    private boolean showBezierMesh = false;

    public static BezierPatchC0 flat(float w, float h, int x, int y) {
        BezierPatchC0 patch = new BezierPatchC0();
        int xp = 4 + (x - 1) * 3;
        int yp = 4 + (y - 1) * 3;
        patch.surface = new Point[xp][yp];
        float wx = w / (x * 3);
        float hy = h / (y * 3);
        for (int i = 0; i < xp; i++) {
            for (int j = 0; j < yp; j++) {
                Point point = new Point(wx * i, 0, hy * j);
                point.addPropertyChangeListener(patch.pcl);
                patch.surface[i][j] = point;
            }
        }
        for (int i = 0; i < xp - 1; i+=3) {
            for (int j = 0; j < yp - 1; j+=3) {
                patch.points.addAll(List.of(
                        patch.surface[i][j],
                        patch.surface[i + 1][j],
                        patch.surface[i + 2][j],
                        patch.surface[i + 3][j],

                        patch.surface[i][j + 1],
                        patch.surface[i + 1][j + 1],
                        patch.surface[i + 2][j + 1],
                        patch.surface[i + 3][j + 1],

                        patch.surface[i][j + 2],
                        patch.surface[i + 1][j + 2],
                        patch.surface[i + 2][j + 2],
                        patch.surface[i + 3][j + 2],

                        patch.surface[i][j + 3],
                        patch.surface[i + 1][j + 3],
                        patch.surface[i + 2][j + 3],
                        patch.surface[i + 3][j + 3]
                ));
            }
        }
        patch.polyMesh = new PolyMesh(patch.surface);
        return patch;
    }

    public static BezierPatchC0 cylinder(float r, float h, int x, int y) {
        BezierPatchC0 patch = new BezierPatchC0();
        int xp = x * 3;
        int yp = 4 + (y - 1) * 3;
        patch.surface = new Point[xp][yp];
        float wx = 2 * PI / (x * 3);
        float hy = h / (y * 3);
        Function<Float, Float> fx = phi -> r * cos(phi);
        Function<Float, Float> fz = phi -> r * sin(phi);
        for (int i = 0; i < xp; i++) {
            for (int j = 0; j < yp; j++) {
                Point point = new Point(fx.apply(wx * i), hy * j, fz.apply(wx * i));
                point.addPropertyChangeListener(patch.pcl);
                patch.surface[i][j] = point;
            }
        }
        Function<Integer, Integer> mod = i -> i % xp;
        for (int i = 0; i < xp; i+=3) {
            for (int j = 0; j < yp - 1; j+=3) {
                patch.points.addAll(List.of(
                        patch.surface[mod.apply(i)][j],
                        patch.surface[mod.apply(i + 1)][j],
                        patch.surface[mod.apply(i + 2)][j],
                        patch.surface[mod.apply(i + 3)][j],

                        patch.surface[mod.apply(i)][j + 1],
                        patch.surface[mod.apply(i + 1)][j + 1],
                        patch.surface[mod.apply(i + 2)][j + 1],
                        patch.surface[mod.apply(i + 3)][j + 1],

                        patch.surface[mod.apply(i)][j + 2],
                        patch.surface[mod.apply(i + 1)][j + 2],
                        patch.surface[mod.apply(i + 2)][j + 2],
                        patch.surface[mod.apply(i + 3)][j + 2],

                        patch.surface[mod.apply(i)][j + 3],
                        patch.surface[mod.apply(i + 1)][j + 3],
                        patch.surface[mod.apply(i + 2)][j + 3],
                        patch.surface[mod.apply(i + 3)][j + 3]
                ));
            }
        }
        patch.polyMesh = new PolyMesh(patch.surface);
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
        if(showBezierMesh) polyMesh.render(gl, camera, renderer);
    }

    public int getDivisions() {
        return divisions;
    }

    public void setDivisions(int divisions) {
        this.divisions = divisions;
    }

    public Stream<Point> getPoints() {
        return points.stream();
    }

    public boolean isShowBezierMesh() {
        return showBezierMesh;
    }

    public void setShowBezierMesh(boolean showBezierMesh) {
        this.showBezierMesh = showBezierMesh;
    }

    @Override
    public void validate(GL4 gl) {
        super.validate(gl);
        polyMesh.validate(gl);
    }

    @Override
    public void dispose(GL4 gl) {
        super.dispose(gl);
        polyMesh.dispose(gl);
    }
}
