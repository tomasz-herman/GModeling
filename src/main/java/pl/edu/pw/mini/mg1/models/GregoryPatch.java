package pl.edu.pw.mini.mg1.models;

import com.jogamp.opengl.GL4;
import org.apache.commons.lang3.ArrayUtils;
import org.joml.Vector3f;
import pl.edu.pw.mini.mg1.cameras.PerspectiveCamera;
import pl.edu.pw.mini.mg1.graphics.Renderer;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class GregoryPatch extends Patch {
    protected final Point[][] s1 = new Point[4][4];
    protected final Point[][] s2 = new Point[4][4];
    protected final Point[][] s3 = new Point[4][4];
    protected final List<Point> points = new ArrayList<>();

    protected GregoryMesh gregoryMesh = new GregoryMesh(points);

    public static GregoryPatch example() {
        GregoryPatch patch = new GregoryPatch();
        patch.points.addAll(List.of(
                new Point(0.0f, 0.0f, 0.0f),
                new Point(1.0f, 1.0f, 0.0f),
                new Point(2.0f, 0.0f, 0.0f),
                new Point(3.0f, 0.0f, 0.0f),

                new Point(0.0f, 1.0f, 1.0f),
                new Point(1.0f, 2.0f, 1.0f),
                new Point(1.0f, -2.0f, 1.0f),
                new Point(2.0f, 1.0f, 1.0f),
                new Point(2.0f, -3.0f, 1.0f),
                new Point(3.0f, 0.0f, 1.0f),

                new Point(0.0f, 0.0f, 2.0f),
                new Point(1.0f, 1.0f, 2.0f),
                new Point(1.0f, -1.0f, 2.0f),
                new Point(2.0f, 0.0f, 2.0f),
                new Point(2.0f, 0.0f, 2.0f),
                new Point(3.0f, -1.0f, 2.0f),

                new Point(0.0f, 0.0f, 3.0f),
                new Point(1.0f, 1.0f, 3.0f),
                new Point(2.0f, -1.0f, 3.0f),
                new Point(3.0f, -1.0f, 3.0f)
        ));
        return patch;
    }

    public static GregoryPatch gregory(List<BezierPatchC0> patches) {
        patches = patches.stream().distinct().collect(Collectors.toList());
        if(patches.size() != 3) return null;

        GregoryPatch patch = new GregoryPatch();

        Point[][] s1 = new Point[4][4];
        Point[][] s2 = new Point[4][4];
        Point[][] s3 = new Point[4][4];

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                s1[i][j] = patches.get(0).surface[i][j];
                s2[i][j] = patches.get(1).surface[i][j];
                s3[i][j] = patches.get(2).surface[i][j];
            }
        }

        outer:
        for (int ii = 0; ii < 2; ii++) {
            for (int i = 0; i < 4; i++) {
                for (int jj = 0; jj < 2; jj++) {
                    for (int j = 0; j < 4; j++) {
                        for (int kk = 0; kk < 2; kk++) {
                            for (int k = 0; k < 4; k++) {
                                if(s1[0][0] == s2[0][3] && s1[0][3] == s3[0][0] && s2[0][0] == s3[0][3]) {
                                    break outer;
                                }
                                s3 = rotate(s3);
                            }
                            s3 = flip(s3);
                        }
                        s2 = rotate(s2);
                    }
                    s2 = flip(s2);
                }
                s1 = rotate(s1);
            }
            s1 = flip(s1);
        }

        if (s1[0][0] != s2[0][3] || s1[0][3] != s3[0][0] || s2[0][0] != s3[0][3]) {
            return null;
        }

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                patch.s1[i][j] = s1[i][j];
                patch.s2[i][j] = s2[i][j];
                patch.s3[i][j] = s3[i][j];
                patch.s1[i][j].addPropertyChangeListener(patch.pcl);
                patch.s2[i][j].addPropertyChangeListener(patch.pcl);
                patch.s3[i][j].addPropertyChangeListener(patch.pcl);
            }
        }

        return patch;
    }

    private static List<Point> calculateGregory(Point[][] s1, Point[][] s2, Point[][] s3) {
        Vector3f B00ip0 = s1[0][0].getTransformedPosition().get(new Vector3f());
        Vector3f B10ip0 = s1[0][1].getTransformedPosition().get(new Vector3f());
        Vector3f B20ip0 = s1[0][2].getTransformedPosition().get(new Vector3f());
        Vector3f B30ip0 = s1[0][3].getTransformedPosition().get(new Vector3f());
        Vector3f B01ip0 = s1[1][0].getTransformedPosition().get(new Vector3f());
        Vector3f B11ip0 = s1[1][1].getTransformedPosition().get(new Vector3f());
        Vector3f B21ip0 = s1[1][2].getTransformedPosition().get(new Vector3f());
        Vector3f B31ip0 = s1[1][3].getTransformedPosition().get(new Vector3f());

        Vector3f B00ip1 = s2[0][0].getTransformedPosition().get(new Vector3f());
        Vector3f B10ip1 = s2[0][1].getTransformedPosition().get(new Vector3f());
        Vector3f B20ip1 = s2[0][2].getTransformedPosition().get(new Vector3f());
        Vector3f B30ip1 = s2[0][3].getTransformedPosition().get(new Vector3f());
        Vector3f B01ip1 = s2[1][0].getTransformedPosition().get(new Vector3f());
        Vector3f B11ip1 = s2[1][1].getTransformedPosition().get(new Vector3f());
        Vector3f B21ip1 = s2[1][2].getTransformedPosition().get(new Vector3f());
        Vector3f B31ip1 = s2[1][3].getTransformedPosition().get(new Vector3f());

        Vector3f B00ip2 = s3[0][0].getTransformedPosition().get(new Vector3f());
        Vector3f B10ip2 = s3[0][1].getTransformedPosition().get(new Vector3f());
        Vector3f B20ip2 = s3[0][2].getTransformedPosition().get(new Vector3f());
        Vector3f B30ip2 = s3[0][3].getTransformedPosition().get(new Vector3f());
        Vector3f B01ip2 = s3[1][0].getTransformedPosition().get(new Vector3f());
        Vector3f B11ip2 = s3[1][1].getTransformedPosition().get(new Vector3f());
        Vector3f B21ip2 = s3[1][2].getTransformedPosition().get(new Vector3f());
        Vector3f B31ip2 = s3[1][3].getTransformedPosition().get(new Vector3f());

        Vector3f R00ip0 = B00ip0.lerp(B10ip0, 0.5f, new Vector3f());
        Vector3f R10ip0 = B10ip0.lerp(B20ip0, 0.5f, new Vector3f());
        Vector3f R20ip0 = B20ip0.lerp(B30ip0, 0.5f, new Vector3f());
        Vector3f R01ip0 = B01ip0.lerp(B11ip0, 0.5f, new Vector3f());
        Vector3f R11ip0 = B11ip0.lerp(B21ip0, 0.5f, new Vector3f());
        Vector3f R21ip0 = B21ip0.lerp(B31ip0, 0.5f, new Vector3f());

        Vector3f R00ip1 = B00ip1.lerp(B10ip1, 0.5f, new Vector3f());
        Vector3f R10ip1 = B10ip1.lerp(B20ip1, 0.5f, new Vector3f());
        Vector3f R20ip1 = B20ip1.lerp(B30ip1, 0.5f, new Vector3f());
        Vector3f R01ip1 = B01ip1.lerp(B11ip1, 0.5f, new Vector3f());
        Vector3f R11ip1 = B11ip1.lerp(B21ip1, 0.5f, new Vector3f());
        Vector3f R21ip1 = B21ip1.lerp(B31ip1, 0.5f, new Vector3f());

        Vector3f R00ip2 = B00ip2.lerp(B10ip2, 0.5f, new Vector3f());
        Vector3f R10ip2 = B10ip2.lerp(B20ip2, 0.5f, new Vector3f());
        Vector3f R20ip2 = B20ip2.lerp(B30ip2, 0.5f, new Vector3f());
        Vector3f R01ip2 = B01ip2.lerp(B11ip2, 0.5f, new Vector3f());
        Vector3f R11ip2 = B11ip2.lerp(B21ip2, 0.5f, new Vector3f());
        Vector3f R21ip2 = B21ip2.lerp(B31ip2, 0.5f, new Vector3f());

        Vector3f S00ip0 = R00ip0.lerp(R10ip0, 0.5f, new Vector3f());
        Vector3f S10ip0 = R10ip0.lerp(R20ip0, 0.5f, new Vector3f());
        Vector3f S01ip0 = R01ip0.lerp(R11ip0, 0.5f, new Vector3f());
        Vector3f S11ip0 = R11ip0.lerp(R21ip0, 0.5f, new Vector3f());

        Vector3f S00ip1 = R00ip1.lerp(R10ip1, 0.5f, new Vector3f());
        Vector3f S10ip1 = R10ip1.lerp(R20ip1, 0.5f, new Vector3f());
        Vector3f S01ip1 = R01ip1.lerp(R11ip1, 0.5f, new Vector3f());
        Vector3f S11ip1 = R11ip1.lerp(R21ip1, 0.5f, new Vector3f());

        Vector3f S00ip2 = R00ip2.lerp(R10ip2, 0.5f, new Vector3f());
        Vector3f S10ip2 = R10ip2.lerp(R20ip2, 0.5f, new Vector3f());
        Vector3f S01ip2 = R01ip2.lerp(R11ip2, 0.5f, new Vector3f());
        Vector3f S11ip2 = R11ip2.lerp(R21ip2, 0.5f, new Vector3f());

        Vector3f T00ip0 = S00ip0.lerp(S10ip0, 0.5f, new Vector3f());
        Vector3f T01ip0 = S01ip0.lerp(S11ip0, 0.5f, new Vector3f());

        Vector3f T00ip1 = S00ip1.lerp(S10ip1, 0.5f, new Vector3f());
        Vector3f T01ip1 = S01ip1.lerp(S11ip1, 0.5f, new Vector3f());

        Vector3f T00ip2 = S00ip2.lerp(S10ip2, 0.5f, new Vector3f());
        Vector3f T01ip2 = S01ip2.lerp(S11ip2, 0.5f, new Vector3f());

        Vector3f Q00ip0 = T00ip0.add(T00ip0.sub(T01ip0, new Vector3f()).mul(0.5f), new Vector3f());
        Vector3f Q00ip1 = T00ip1.add(T00ip1.sub(T01ip1, new Vector3f()).mul(0.5f), new Vector3f());
        Vector3f Q00ip2 = T00ip2.add(T00ip2.sub(T01ip2, new Vector3f()).mul(0.5f), new Vector3f());

        Vector3f P000 = new Vector3f(T00ip0);
        Vector3f P001 = P000.add(T00ip0.sub(T01ip0, new Vector3f()), new Vector3f());
        Vector3f P004 = new Vector3f(S10ip0);
        Vector3f P005 = P004.add(S10ip0.sub(S11ip0, new Vector3f()), new Vector3f());
        Vector3f P010 = new Vector3f(R20ip0);
        Vector3f P011 = P010.add(R20ip0.sub(R21ip0, new Vector3f()), new Vector3f());
        Vector3f P016 = new Vector3f(B30ip0);
        Vector3f P017 = new Vector3f(R00ip1);
        Vector3f P012 = P017.add(R00ip1.sub(R01ip1, new Vector3f()), new Vector3f());
        Vector3f P018 = new Vector3f(S00ip1);
        Vector3f P013 = P018.add(S00ip1.sub(S01ip1, new Vector3f()), new Vector3f());
        Vector3f P019 = new Vector3f(T00ip1);
        Vector3f P015 = P019.add(T00ip1.sub(T01ip1, new Vector3f()), new Vector3f());
        Vector3f P003 = new Vector3f((Q00ip0.x + Q00ip1.x + Q00ip2.x)/3, (Q00ip0.y + Q00ip1.y + Q00ip2.y)/3, (Q00ip0.z + Q00ip1.z + Q00ip2.z)/3);
        Vector3f P002 = Q00ip0.lerp(P003, 1f / 3f, new Vector3f());
        Vector3f P009 = Q00ip1.lerp(P003, 1f / 3f, new Vector3f());
        Vector3f P006 = new Vector3f(P005);
        Vector3f P014 = new Vector3f(P013);
        Vector3f P007 = new Vector3f(P005.add(P002.sub(P001, new Vector3f()), new Vector3f()));
        Vector3f P008 = new Vector3f(P013.add(P009.sub(P015, new Vector3f()), new Vector3f()));

        Vector3f P100 = new Vector3f(T00ip1);
        Vector3f P101 = P100.add(T00ip1.sub(T01ip1, new Vector3f()), new Vector3f());
        Vector3f P104 = new Vector3f(S10ip1);
        Vector3f P105 = P104.add(S10ip1.sub(S11ip1, new Vector3f()), new Vector3f());
        Vector3f P110 = new Vector3f(R20ip1);
        Vector3f P111 = P110.add(R20ip1.sub(R21ip1, new Vector3f()), new Vector3f());
        Vector3f P116 = new Vector3f(B30ip1);
        Vector3f P117 = new Vector3f(R00ip2);
        Vector3f P112 = P117.add(R00ip2.sub(R01ip2, new Vector3f()), new Vector3f());
        Vector3f P118 = new Vector3f(S00ip2);
        Vector3f P113 = P118.add(S00ip2.sub(S01ip2, new Vector3f()), new Vector3f());
        Vector3f P119 = new Vector3f(T00ip2);
        Vector3f P115 = P119.add(T00ip2.sub(T01ip2, new Vector3f()), new Vector3f());
        Vector3f P103 = new Vector3f((Q00ip0.x + Q00ip1.x + Q00ip2.x)/3, (Q00ip0.y + Q00ip1.y + Q00ip2.y)/3, (Q00ip0.z + Q00ip1.z + Q00ip2.z)/3);
        Vector3f P102 = Q00ip1.lerp(P103, 1f / 3f, new Vector3f());
        Vector3f P109 = Q00ip2.lerp(P103, 1f / 3f, new Vector3f());
        Vector3f P106 = new Vector3f(P105);
        Vector3f P114 = new Vector3f(P113);
        Vector3f P107 = new Vector3f(P105.add(P102.sub(P101, new Vector3f()), new Vector3f()));
        Vector3f P108 = new Vector3f(P113.add(P109.sub(P115, new Vector3f()), new Vector3f()));

        Vector3f P200 = new Vector3f(T00ip2);
        Vector3f P201 = P200.add(T00ip2.sub(T01ip2, new Vector3f()), new Vector3f());
        Vector3f P204 = new Vector3f(S10ip2);
        Vector3f P205 = P204.add(S10ip2.sub(S11ip2, new Vector3f()), new Vector3f());
        Vector3f P210 = new Vector3f(R20ip2);
        Vector3f P211 = P210.add(R20ip2.sub(R21ip2, new Vector3f()), new Vector3f());
        Vector3f P216 = new Vector3f(B30ip2);
        Vector3f P217 = new Vector3f(R00ip0);
        Vector3f P212 = P217.add(R00ip0.sub(R01ip0, new Vector3f()), new Vector3f());
        Vector3f P218 = new Vector3f(S00ip0);
        Vector3f P213 = P218.add(S00ip0.sub(S01ip0, new Vector3f()), new Vector3f());
        Vector3f P219 = new Vector3f(T00ip0);
        Vector3f P215 = P219.add(T00ip0.sub(T01ip0, new Vector3f()), new Vector3f());
        Vector3f P203 = new Vector3f((Q00ip0.x + Q00ip1.x + Q00ip2.x)/3, (Q00ip0.y + Q00ip1.y + Q00ip2.y)/3, (Q00ip0.z + Q00ip1.z + Q00ip2.z)/3);
        Vector3f P202 = Q00ip2.lerp(P003, 1f / 3f, new Vector3f());
        Vector3f P209 = Q00ip0.lerp(P003, 1f / 3f, new Vector3f());
        Vector3f P206 = new Vector3f(P205);
        Vector3f P214 = new Vector3f(P213);
        Vector3f P207 = new Vector3f(P205.add(P202.sub(P201, new Vector3f()), new Vector3f()));
        Vector3f P208 = new Vector3f(P213.add(P209.sub(P215, new Vector3f()), new Vector3f()));

        return List.of(
                new Point(P000),
                new Point(P001),
                new Point(P002),
                new Point(P003),
                new Point(P004),
                new Point(P005),
                new Point(P006),
                new Point(P007),
                new Point(P008),
                new Point(P009),
                new Point(P010),
                new Point(P011),
                new Point(P012),
                new Point(P013),
                new Point(P014),
                new Point(P015),
                new Point(P016),
                new Point(P017),
                new Point(P018),
                new Point(P019),

                new Point(P100),
                new Point(P101),
                new Point(P102),
                new Point(P103),
                new Point(P104),
                new Point(P105),
                new Point(P106),
                new Point(P107),
                new Point(P108),
                new Point(P109),
                new Point(P110),
                new Point(P111),
                new Point(P112),
                new Point(P113),
                new Point(P114),
                new Point(P115),
                new Point(P116),
                new Point(P117),
                new Point(P118),
                new Point(P119),

                new Point(P200),
                new Point(P201),
                new Point(P202),
                new Point(P203),
                new Point(P204),
                new Point(P205),
                new Point(P206),
                new Point(P207),
                new Point(P208),
                new Point(P209),
                new Point(P210),
                new Point(P211),
                new Point(P212),
                new Point(P213),
                new Point(P214),
                new Point(P215),
                new Point(P216),
                new Point(P217),
                new Point(P218),
                new Point(P219)
        );
    }

    private static Point[][] rotate(Point[][] surface) {
        Point[][] rotation = new Point[4][4];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                rotation[i][j] = surface[j][3 - i];
            }
        }
        return rotation;
    }

    private static Point[][] flip(Point[][] surface) {
        Point[][] flip = new Point[4][4];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                flip[i][j] = surface[i][3 - j];
            }
        }
        return flip;
    }

    @Override
    protected void load(GL4 gl) {
        if(s1 != null && s2 != null && s3 != null && s1[0][0] != null && s2[0][0] != null && s3[0][0] != null) {
            points.clear();
            points.addAll(calculateGregory(s1, s3, s2));
        }
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
        renderer.renderGregoryPatch(gl, camera, this);
        if(isShowBezierMesh()) {
            gl.glVertexAttrib3f(1, 1, 0, 0);
            gregoryMesh.render(gl, camera, renderer);
            gl.glVertexAttrib3f(1, 1, 1, 1);
        }
    }

    @Override
    public void cleanup() {
        super.cleanup();
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if(s1[i][j] != null)
                    s1[i][j].removePropertyChangeListener(pcl);
                if(s2[i][j] != null)
                    s2[i][j].removePropertyChangeListener(pcl);
                if(s3[i][j] != null)
                    s3[i][j].removePropertyChangeListener(pcl);
            }
        }
    }

    @Override
    public void validate(GL4 gl) {
        super.validate(gl);
        gregoryMesh.validate(gl);
    }

    @Override
    public void dispose(GL4 gl) {
        super.dispose(gl);
        gregoryMesh.dispose(gl);
    }

    @Override
    public void replacePoint(Point replaced, Point replacement) {
        miniReplace(replaced, replacement, s1);
        miniReplace(replaced, replacement, s2);
        miniReplace(replaced, replacement, s3);
        reload = true;
    }

    private void miniReplace(Point replaced, Point replacement, Point[][] s) {
        if(s != null) {
            for (int i = 0; i < s.length; i++) {
                for (int j = 0; j < s[i].length; j++) {
                    if(s[i][j] == replaced) {
                        s[i][j] = replacement;
                    }
                }
            }
        }
    }
}
