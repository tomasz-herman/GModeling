package pl.edu.pw.mini.mg1.models;

import com.jogamp.opengl.GL4;
import org.apache.commons.lang3.ArrayUtils;
import pl.edu.pw.mini.mg1.cameras.PerspectiveCamera;
import pl.edu.pw.mini.mg1.graphics.Renderer;

import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public abstract class Patch extends Model {
    protected final PropertyChangeListener pcl = e -> reload = true;

    protected Point[][] surface;
    protected final List<Point> points = new ArrayList<>();
    protected int divisionsU = 3;
    protected int divisionsV = 3;

    protected PolyMesh polyMesh;
    protected boolean showBezierMesh = false;

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
        if (showBezierMesh) polyMesh.render(gl, camera, renderer);
    }

    public void replacePoint(Point replaced, Point replacement) {
        boolean replacements = false;
        for (int i = 0; i < surface.length; i++) {
            for (int j = 0; j < surface[i].length; j++) {
                if(surface[i][j] == replaced) {
                    replacements = true;
                    surface[i][j] = replacement;
                }
            }
        }
        if(replacements) {
            reload = true;
            replaced.removePropertyChangeListener(pcl);
            replacement.addPropertyChangeListener(pcl);
            points.replaceAll(point -> point == replaced ? replacement : point);
        }
    }

    public int getDivisionsU() {
        return divisionsU;
    }

    public void setDivisionsU(int divisionsU) {
        this.divisionsU = divisionsU;
    }

    public int getDivisionsV() {
        return divisionsV;
    }

    public void setDivisionsV(int divisionsV) {
        this.divisionsV = divisionsV;
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
