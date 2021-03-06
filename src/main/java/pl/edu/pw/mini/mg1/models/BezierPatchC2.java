package pl.edu.pw.mini.mg1.models;

import com.jogamp.opengl.GL4;
import org.joml.Math;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import pl.edu.pw.mini.mg1.cameras.PerspectiveCamera;
import pl.edu.pw.mini.mg1.graphics.Renderer;
import pl.edu.pw.mini.mg1.graphics.Texture;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.jogamp.opengl.math.FloatUtil.PI;
import static org.joml.Math.clamp;

public class BezierPatchC2 extends Patch implements Intersectable {

    private Texture texture;
    private boolean rightSide = true, leftSide = true;

    public static BezierPatchC2 example() {
        BezierPatchC2 patch = new BezierPatchC2();
        patch.surface = new Point[0][0];
        patch.polyMesh = new PolyMesh(patch.surface);
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
                new Point(3.0f, -1.0f, 3.0f),

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
                new Point(3.0f, -1.0f, 3.0f),

                new Point(0.0f, 1.0f, 4.0f),
                new Point(1.0f, -2.0f, 4.0f),
                new Point(2.0f, 1.0f, 4.0f),
                new Point(3.0f, 1.0f, 4.0f)
        ));
        return patch;
    }

    public static BezierPatchC2 flat(float w, float h, int x, int y) {
        BezierPatchC2 patch = new BezierPatchC2();
        int xp = 3 + x;
        int yp = 3 + y;

        float rx = w / x;
        float ry = h / y;

        patch.surface = new Point[xp][yp];
        patch.polyMesh = new PolyMesh(patch.surface);

        for (int i = 0; i < xp; i++) {
            for (int j = 0; j < yp; j++) {
                Point point = new Point(rx * (i - 1), 0, ry * (j - 1));
                point.addPropertyChangeListener(patch.pcl);
                patch.surface[i][j] = point;
            }
        }

        for (int i = 0; i < x; i++) {
            for (int j = 0; j < y; j++) {
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

        return patch;
    }

    public static BezierPatchC2 cylinder(float r, float h, int x, int y) {
        BezierPatchC2 patch = new BezierPatchC2();
        if (x < 3) x = 3;
        int xp = x;
        int yp = 3 + y;

        float rx = 2 * PI / xp;
        float ry = h / y;

        patch.surface = new Point[xp + 3][yp];
        patch.polyMesh = new PolyMesh(patch.surface);

        Function<Float, Float> fx = Math::cos;
        Function<Float, Float> fz = Math::sin;

        for (int i = 0; i < xp; i++) {
            for (int j = 0; j < yp; j++) {
                Point point = new Point(fx.apply(rx * i), ry * (j - 1), fz.apply(rx * i));
                point.addPropertyChangeListener(patch.pcl);
                patch.surface[i][j] = point;
            }
        }

        System.arraycopy(patch.surface[2], 0, patch.surface[xp + 2], 0, yp);
        System.arraycopy(patch.surface[1], 0, patch.surface[xp + 1], 0, yp);
        System.arraycopy(patch.surface[0], 0, patch.surface[xp], 0, yp);

        float R = new Vector2f(
                1.0f / 6.0f * patch.surface[0][1].getPosition().x()
                + 2.0f / 3.0f * patch.surface[1][1].getPosition().x()
                + 1.0f / 6.0f * patch.surface[2][1].getPosition().x(),
                1.0f / 6.0f * patch.surface[0][1].getPosition().z()
                + 2.0f / 3.0f * patch.surface[1][1].getPosition().z()
                + 1.0f / 6.0f * patch.surface[2][1].getPosition().z())
                .add(
                        1.0f / 6.0f * patch.surface[0][1].getPosition().x()
                        + 2.0f / 3.0f * patch.surface[1][1].getPosition().x()
                        + 1.0f / 6.0f * patch.surface[2][1].getPosition().x()
                        - 1.0f / 4.0f * patch.surface[0][1].getPosition().x()
                        + 1.0f / 4.0f * patch.surface[2][1].getPosition().x()
                        + 1.0f / 8.0f * patch.surface[0][1].getPosition().x()
                        - 1.0f / 4.0f * patch.surface[1][1].getPosition().x()
                        + 1.0f / 8.0f * patch.surface[2][1].getPosition().x()
                        - 1.0f / 24.0f * patch.surface[0][1].getPosition().x()
                        + 1.0f / 8.0f * patch.surface[1][1].getPosition().x()
                        - 1.0f / 8.0f * patch.surface[2][1].getPosition().x()
                        + 1.0f / 24.0f * patch.surface[3][1].getPosition().x()
                        ,
                        1.0f / 6.0f * patch.surface[0][1].getPosition().z()
                        + 2.0f / 3.0f * patch.surface[1][1].getPosition().z()
                        + 1.0f / 6.0f * patch.surface[2][1].getPosition().z()
                        - 1.0f / 4.0f * patch.surface[0][1].getPosition().z()
                        + 1.0f / 4.0f * patch.surface[2][1].getPosition().z()
                        + 1.0f / 8.0f * patch.surface[0][1].getPosition().z()
                        - 1.0f / 4.0f * patch.surface[1][1].getPosition().z()
                        + 1.0f / 8.0f * patch.surface[2][1].getPosition().z()
                        - 1.0f / 24.0f * patch.surface[0][1].getPosition().z()
                        + 1.0f / 8.0f * patch.surface[1][1].getPosition().z()
                        - 1.0f / 8.0f * patch.surface[2][1].getPosition().z()
                        + 1.0f / 24.0f * patch.surface[3][1].getPosition().z()
                )
                .div(2)
                .length();


        for (int i = 0; i < xp; i++) {
            for (int j = 0; j < yp; j++) {
                Vector3f p = new Vector3f(patch.surface[i][j].getPosition());
                patch.surface[i][j].setPosition(p.x * r / R, p.y, p.z * r / R);
            }
        }

        for (int i = 0; i < x; i++) {
            for (int j = 0; j < y; j++) {
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

        return patch;
    }

    @Override
    public void render(GL4 gl, PerspectiveCamera camera, Renderer renderer) {
        super.render(gl, camera, renderer);
        renderer.renderSplinePatch(gl, camera, this);
    }

    @Override
    public int U() {
        int I = surface.length;
        return I - 3;
    }

    @Override
    public int V() {
        int J = surface[0].length;
        return J - 3;
    }

    @Override
    public String serialize() {
        return """
                  <PatchC2 Name="%s" N="%d" M="%d" NSlices="%d" MSlices="%d">
                    <Points>
                %s
                    </Points>
                  </PatchC2>
                """.formatted(
                getName(), surface[0].length - 3, surface.length - 3, divisionsU, divisionsV,
                IntStream.range(0, surface.length).boxed().flatMap(
                        i -> Arrays.stream(surface[i]).map(
                                point -> "      <PointRef Name=\"%s\"/>".formatted(point.getName()))
                ).collect(Collectors.joining("\n"))
        );
    }

    @Override
    public Model deserialize(Node node, Map<String, Point> points) {
        if (node.getNodeType() == Node.ELEMENT_NODE) {
            Element patchC2Element = (Element) node;
            setName(patchC2Element.getAttribute("Name"));
            setDivisionsU(Integer.parseInt(patchC2Element.getAttribute("NSlices")));
            setDivisionsV(Integer.parseInt(patchC2Element.getAttribute("MSlices")));

            int n = Integer.parseInt(patchC2Element.getAttribute("N"));
            int m = Integer.parseInt(patchC2Element.getAttribute("M"));

            NodeList pointsRefs = ((Element) patchC2Element
                    .getElementsByTagName("Points").item(0))
                    .getElementsByTagName("PointRef");

            surface = new Point[m + 3][n + 3];


            for (int i = 0; i < m + 3; i++) {
                for (int j = 0; j < n + 3; j++) {
                    Element pointRefElement = (Element) pointsRefs.item(i * (n + 3) + j);
                    Point point = points.get(pointRefElement.getAttribute("Name"));
                    point.addPropertyChangeListener(pcl);
                    surface[i][j] = point;
                }
            }

            for (int i = 0; i < m; i++) {
                for (int j = 0; j < n; j++) {
                    this.points.addAll(List.of(
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

            polyMesh = new PolyMesh(surface);
        }
        return this;
    }

    @Override
    public Vector3f P(float u, float v) {
        int I = surface.length;
        int J = surface[0].length;
        int U = I - 3;
        int V = J - 3;
        u *= U;
        v *= V;
        I = clamp(0, U - 1, (int)u);
        J = clamp(0, V - 1, (int)v);
        u -= I;
        v -= J;
        List<Point> points = this.points.subList(16 * (I * V + J), 16 * (I * V + J + 1));
        Vector3f p00 = points.get( 0).getPosition().get(new Vector3f());
        Vector3f p10 = points.get( 1).getPosition().get(new Vector3f());
        Vector3f p20 = points.get( 2).getPosition().get(new Vector3f());
        Vector3f p30 = points.get( 3).getPosition().get(new Vector3f());
        Vector3f p01 = points.get( 4).getPosition().get(new Vector3f());
        Vector3f p11 = points.get( 5).getPosition().get(new Vector3f());
        Vector3f p21 = points.get( 6).getPosition().get(new Vector3f());
        Vector3f p31 = points.get( 7).getPosition().get(new Vector3f());
        Vector3f p02 = points.get( 8).getPosition().get(new Vector3f());
        Vector3f p12 = points.get( 9).getPosition().get(new Vector3f());
        Vector3f p22 = points.get(10).getPosition().get(new Vector3f());
        Vector3f p32 = points.get(11).getPosition().get(new Vector3f());
        Vector3f p03 = points.get(12).getPosition().get(new Vector3f());
        Vector3f p13 = points.get(13).getPosition().get(new Vector3f());
        Vector3f p23 = points.get(14).getPosition().get(new Vector3f());
        Vector3f p33 = points.get(15).getPosition().get(new Vector3f());

        Vector3f b00 = new Vector3f(p00).mul(1.0f / 6.0f).add(new Vector3f(p01).mul(2.0f / 3.0f)).add(new Vector3f(p02).mul(1.0f / 6.0f));
        Vector3f b01 = new Vector3f(p01).mul(2.0f / 3.0f).add(new Vector3f(p02).mul(1.0f / 3.0f));
        Vector3f b02 = new Vector3f(p01).mul(1.0f / 3.0f).add(new Vector3f(p02).mul(2.0f / 3.0f));
        Vector3f b03 = new Vector3f(p01).mul(1.0f / 6.0f).add(new Vector3f(p02).mul(2.0f / 3.0f)).add(new Vector3f(p03).mul(1.0f / 6.0f));

        Vector3f b10 = new Vector3f(p10).mul(1.0f / 6.0f).add(new Vector3f(p11).mul(2.0f / 3.0f)).add(new Vector3f(p12).mul(1.0f / 6.0f));
        Vector3f b11 = new Vector3f(p11).mul(2.0f / 3.0f).add(new Vector3f(p12).mul(1.0f / 3.0f));
        Vector3f b12 = new Vector3f(p11).mul(1.0f / 3.0f).add(new Vector3f(p12).mul(2.0f / 3.0f));
        Vector3f b13 = new Vector3f(p11).mul(1.0f / 6.0f).add(new Vector3f(p12).mul(2.0f / 3.0f)).add(new Vector3f(p13).mul(1.0f / 6.0f));

        Vector3f b20 = new Vector3f(p20).mul(1.0f / 6.0f).add(new Vector3f(p21).mul(2.0f / 3.0f)).add(new Vector3f(p22).mul(1.0f / 6.0f));
        Vector3f b21 = new Vector3f(p21).mul(2.0f / 3.0f).add(new Vector3f(p22).mul(1.0f / 3.0f));
        Vector3f b22 = new Vector3f(p21).mul(1.0f / 3.0f).add(new Vector3f(p22).mul(2.0f / 3.0f));
        Vector3f b23 = new Vector3f(p21).mul(1.0f / 6.0f).add(new Vector3f(p22).mul(2.0f / 3.0f)).add(new Vector3f(p23).mul(1.0f / 6.0f));

        Vector3f b30 = new Vector3f(p30).mul(1.0f / 6.0f).add(new Vector3f(p31).mul(2.0f / 3.0f)).add(new Vector3f(p32).mul(1.0f / 6.0f));
        Vector3f b31 = new Vector3f(p31).mul(2.0f / 3.0f).add(new Vector3f(p32).mul(1.0f / 3.0f));
        Vector3f b32 = new Vector3f(p31).mul(1.0f / 3.0f).add(new Vector3f(p32).mul(2.0f / 3.0f));
        Vector3f b33 = new Vector3f(p31).mul(1.0f / 6.0f).add(new Vector3f(p32).mul(2.0f / 3.0f)).add(new Vector3f(p33).mul(1.0f / 6.0f));

        float omv = 1.0f - v;
        float omv2 = omv * omv;
        float omv3 = omv2 * omv;
        float v2 = v * v;
        float v3 = v * v2;

        Vector3f d0 = b00.mul(omv3).add(b01.mul(3.0f * v * omv2)).add(b02.mul(3.0f * v2 * omv)).add(b03.mul(v3));
        Vector3f d1 = b10.mul(omv3).add(b11.mul(3.0f * v * omv2)).add(b12.mul(3.0f * v2 * omv)).add(b13.mul(v3));
        Vector3f d2 = b20.mul(omv3).add(b21.mul(3.0f * v * omv2)).add(b22.mul(3.0f * v2 * omv)).add(b23.mul(v3));
        Vector3f d3 = b30.mul(omv3).add(b31.mul(3.0f * v * omv2)).add(b32.mul(3.0f * v2 * omv)).add(b33.mul(v3));

        Vector3f b0 = new Vector3f(d0).mul(1.0f / 6.0f).add(new Vector3f(d1).mul(2.0f / 3.0f)).add(new Vector3f(d2).mul(1.0f / 6.0f));
        Vector3f b1 = new Vector3f(d1).mul(2.0f / 3.0f).add(new Vector3f(d2).mul(1.0f / 3.0f));
        Vector3f b2 = new Vector3f(d1).mul(1.0f / 3.0f).add(new Vector3f(d2).mul(2.0f / 3.0f));
        Vector3f b3 = new Vector3f(d1).mul(1.0f / 6.0f).add(new Vector3f(d2).mul(2.0f / 3.0f)).add(new Vector3f(d3).mul(1.0f / 6.0f));

        float omu = 1.0f - u;
        float omu2 = omu * omu;
        float omu3 = omu * omu2;
        float u2 = u * u;
        float u3 = u * u2;

        return b0.mul(omu3).add(b1.mul(3.0f * u * omu2)).add(b2.mul(3.0f * u2 * omu)).add(b3.mul(u3));
    }

    @Override
    public Vector3f T(float u, float v) {
        int I = surface.length;
        int J = surface[0].length;
        int U = I - 3;
        int V = J - 3;
        u *= U;
        v *= V;
        I = clamp(0, U - 1, (int)u);
        J = clamp(0, V - 1, (int)v);
        u -= I;
        v -= J;
        List<Point> points = this.points.subList(16 * (I * V + J), 16 * (I * V + J + 1));
        Vector3f p00 = points.get( 0).getPosition().get(new Vector3f());
        Vector3f p10 = points.get( 1).getPosition().get(new Vector3f());
        Vector3f p20 = points.get( 2).getPosition().get(new Vector3f());
        Vector3f p30 = points.get( 3).getPosition().get(new Vector3f());
        Vector3f p01 = points.get( 4).getPosition().get(new Vector3f());
        Vector3f p11 = points.get( 5).getPosition().get(new Vector3f());
        Vector3f p21 = points.get( 6).getPosition().get(new Vector3f());
        Vector3f p31 = points.get( 7).getPosition().get(new Vector3f());
        Vector3f p02 = points.get( 8).getPosition().get(new Vector3f());
        Vector3f p12 = points.get( 9).getPosition().get(new Vector3f());
        Vector3f p22 = points.get(10).getPosition().get(new Vector3f());
        Vector3f p32 = points.get(11).getPosition().get(new Vector3f());
        Vector3f p03 = points.get(12).getPosition().get(new Vector3f());
        Vector3f p13 = points.get(13).getPosition().get(new Vector3f());
        Vector3f p23 = points.get(14).getPosition().get(new Vector3f());
        Vector3f p33 = points.get(15).getPosition().get(new Vector3f());

        Vector3f b00 = new Vector3f(p00).mul(1.0f / 6.0f).add(new Vector3f(p01).mul(2.0f / 3.0f)).add(new Vector3f(p02).mul(1.0f / 6.0f));
        Vector3f b01 = new Vector3f(p01).mul(2.0f / 3.0f).add(new Vector3f(p02).mul(1.0f / 3.0f));
        Vector3f b02 = new Vector3f(p01).mul(1.0f / 3.0f).add(new Vector3f(p02).mul(2.0f / 3.0f));
        Vector3f b03 = new Vector3f(p01).mul(1.0f / 6.0f).add(new Vector3f(p02).mul(2.0f / 3.0f)).add(new Vector3f(p03).mul(1.0f / 6.0f));

        Vector3f b10 = new Vector3f(p10).mul(1.0f / 6.0f).add(new Vector3f(p11).mul(2.0f / 3.0f)).add(new Vector3f(p12).mul(1.0f / 6.0f));
        Vector3f b11 = new Vector3f(p11).mul(2.0f / 3.0f).add(new Vector3f(p12).mul(1.0f / 3.0f));
        Vector3f b12 = new Vector3f(p11).mul(1.0f / 3.0f).add(new Vector3f(p12).mul(2.0f / 3.0f));
        Vector3f b13 = new Vector3f(p11).mul(1.0f / 6.0f).add(new Vector3f(p12).mul(2.0f / 3.0f)).add(new Vector3f(p13).mul(1.0f / 6.0f));

        Vector3f b20 = new Vector3f(p20).mul(1.0f / 6.0f).add(new Vector3f(p21).mul(2.0f / 3.0f)).add(new Vector3f(p22).mul(1.0f / 6.0f));
        Vector3f b21 = new Vector3f(p21).mul(2.0f / 3.0f).add(new Vector3f(p22).mul(1.0f / 3.0f));
        Vector3f b22 = new Vector3f(p21).mul(1.0f / 3.0f).add(new Vector3f(p22).mul(2.0f / 3.0f));
        Vector3f b23 = new Vector3f(p21).mul(1.0f / 6.0f).add(new Vector3f(p22).mul(2.0f / 3.0f)).add(new Vector3f(p23).mul(1.0f / 6.0f));

        Vector3f b30 = new Vector3f(p30).mul(1.0f / 6.0f).add(new Vector3f(p31).mul(2.0f / 3.0f)).add(new Vector3f(p32).mul(1.0f / 6.0f));
        Vector3f b31 = new Vector3f(p31).mul(2.0f / 3.0f).add(new Vector3f(p32).mul(1.0f / 3.0f));
        Vector3f b32 = new Vector3f(p31).mul(1.0f / 3.0f).add(new Vector3f(p32).mul(2.0f / 3.0f));
        Vector3f b33 = new Vector3f(p31).mul(1.0f / 6.0f).add(new Vector3f(p32).mul(2.0f / 3.0f)).add(new Vector3f(p33).mul(1.0f / 6.0f));

        float omv = 1.0f - v;
        float omv2 = omv * omv;
        float omv3 = omv2 * omv;
        float v2 = v * v;
        float v3 = v * v2;

        Vector3f d0 = b00.mul(omv3).add(b01.mul(3.0f * v * omv2)).add(b02.mul(3.0f * v2 * omv)).add(b03.mul(v3));
        Vector3f d1 = b10.mul(omv3).add(b11.mul(3.0f * v * omv2)).add(b12.mul(3.0f * v2 * omv)).add(b13.mul(v3));
        Vector3f d2 = b20.mul(omv3).add(b21.mul(3.0f * v * omv2)).add(b22.mul(3.0f * v2 * omv)).add(b23.mul(v3));
        Vector3f d3 = b30.mul(omv3).add(b31.mul(3.0f * v * omv2)).add(b32.mul(3.0f * v2 * omv)).add(b33.mul(v3));

        Vector3f b0 = new Vector3f(d0).mul(1.0f / 6.0f).add(new Vector3f(d1).mul(2.0f / 3.0f)).add(new Vector3f(d2).mul(1.0f / 6.0f));
        Vector3f b1 = new Vector3f(d1).mul(2.0f / 3.0f).add(new Vector3f(d2).mul(1.0f / 3.0f));
        Vector3f b2 = new Vector3f(d1).mul(1.0f / 3.0f).add(new Vector3f(d2).mul(2.0f / 3.0f));
        Vector3f b3 = new Vector3f(d1).mul(1.0f / 6.0f).add(new Vector3f(d2).mul(2.0f / 3.0f)).add(new Vector3f(d3).mul(1.0f / 6.0f));

        float dbu0 = -3 * (1-u) * (1-u);
        float dbu1 =  3 * (1-u) * (1-3*u);
        float dbu2 =  3 * u * (2-3*u);
        float dbu3 =  3 * u * u;

        return b0.mul(dbu0).add(b1.mul(dbu1)).add(b2.mul(dbu2)).add(b3.mul(dbu3));
    }

    @Override
    public Vector3f B(float u, float v) {
        int I = surface.length;
        int J = surface[0].length;
        int U = I - 3;
        int V = J - 3;
        u *= U;
        v *= V;
        I = clamp(0, U - 1, (int)u);
        J = clamp(0, V - 1, (int)v);
        u -= I;
        v -= J;
        List<Point> points = this.points.subList(16 * (I * V + J), 16 * (I * V + J + 1));
        Vector3f p00 = points.get( 0).getPosition().get(new Vector3f());
        Vector3f p10 = points.get( 1).getPosition().get(new Vector3f());
        Vector3f p20 = points.get( 2).getPosition().get(new Vector3f());
        Vector3f p30 = points.get( 3).getPosition().get(new Vector3f());
        Vector3f p01 = points.get( 4).getPosition().get(new Vector3f());
        Vector3f p11 = points.get( 5).getPosition().get(new Vector3f());
        Vector3f p21 = points.get( 6).getPosition().get(new Vector3f());
        Vector3f p31 = points.get( 7).getPosition().get(new Vector3f());
        Vector3f p02 = points.get( 8).getPosition().get(new Vector3f());
        Vector3f p12 = points.get( 9).getPosition().get(new Vector3f());
        Vector3f p22 = points.get(10).getPosition().get(new Vector3f());
        Vector3f p32 = points.get(11).getPosition().get(new Vector3f());
        Vector3f p03 = points.get(12).getPosition().get(new Vector3f());
        Vector3f p13 = points.get(13).getPosition().get(new Vector3f());
        Vector3f p23 = points.get(14).getPosition().get(new Vector3f());
        Vector3f p33 = points.get(15).getPosition().get(new Vector3f());

        Vector3f b00 = new Vector3f(p00).mul(1.0f / 6.0f).add(new Vector3f(p10).mul(2.0f / 3.0f)).add(new Vector3f(p20).mul(1.0f / 6.0f));
        Vector3f b01 = new Vector3f(p10).mul(2.0f / 3.0f).add(new Vector3f(p20).mul(1.0f / 3.0f));
        Vector3f b02 = new Vector3f(p10).mul(1.0f / 3.0f).add(new Vector3f(p20).mul(2.0f / 3.0f));
        Vector3f b03 = new Vector3f(p10).mul(1.0f / 6.0f).add(new Vector3f(p20).mul(2.0f / 3.0f)).add(new Vector3f(p30).mul(1.0f / 6.0f));

        Vector3f b10 = new Vector3f(p01).mul(1.0f / 6.0f).add(new Vector3f(p11).mul(2.0f / 3.0f)).add(new Vector3f(p21).mul(1.0f / 6.0f));
        Vector3f b11 = new Vector3f(p11).mul(2.0f / 3.0f).add(new Vector3f(p21).mul(1.0f / 3.0f));
        Vector3f b12 = new Vector3f(p11).mul(1.0f / 3.0f).add(new Vector3f(p21).mul(2.0f / 3.0f));
        Vector3f b13 = new Vector3f(p11).mul(1.0f / 6.0f).add(new Vector3f(p21).mul(2.0f / 3.0f)).add(new Vector3f(p31).mul(1.0f / 6.0f));

        Vector3f b20 = new Vector3f(p02).mul(1.0f / 6.0f).add(new Vector3f(p12).mul(2.0f / 3.0f)).add(new Vector3f(p22).mul(1.0f / 6.0f));
        Vector3f b21 = new Vector3f(p12).mul(2.0f / 3.0f).add(new Vector3f(p22).mul(1.0f / 3.0f));
        Vector3f b22 = new Vector3f(p12).mul(1.0f / 3.0f).add(new Vector3f(p22).mul(2.0f / 3.0f));
        Vector3f b23 = new Vector3f(p12).mul(1.0f / 6.0f).add(new Vector3f(p22).mul(2.0f / 3.0f)).add(new Vector3f(p32).mul(1.0f / 6.0f));

        Vector3f b30 = new Vector3f(p03).mul(1.0f / 6.0f).add(new Vector3f(p13).mul(2.0f / 3.0f)).add(new Vector3f(p23).mul(1.0f / 6.0f));
        Vector3f b31 = new Vector3f(p13).mul(2.0f / 3.0f).add(new Vector3f(p23).mul(1.0f / 3.0f));
        Vector3f b32 = new Vector3f(p13).mul(1.0f / 3.0f).add(new Vector3f(p23).mul(2.0f / 3.0f));
        Vector3f b33 = new Vector3f(p13).mul(1.0f / 6.0f).add(new Vector3f(p23).mul(2.0f / 3.0f)).add(new Vector3f(p33).mul(1.0f / 6.0f));

        float omu = 1.0f - u;
        float omu2 = omu * omu;
        float omu3 = omu2 * omu;
        float u2 = u * u;
        float u3 = u * u2;

        Vector3f d0 = b00.mul(omu3).add(b01.mul(3.0f * u * omu2)).add(b02.mul(3.0f * u2 * omu)).add(b03.mul(u3));
        Vector3f d1 = b10.mul(omu3).add(b11.mul(3.0f * u * omu2)).add(b12.mul(3.0f * u2 * omu)).add(b13.mul(u3));
        Vector3f d2 = b20.mul(omu3).add(b21.mul(3.0f * u * omu2)).add(b22.mul(3.0f * u2 * omu)).add(b23.mul(u3));
        Vector3f d3 = b30.mul(omu3).add(b31.mul(3.0f * u * omu2)).add(b32.mul(3.0f * u2 * omu)).add(b33.mul(u3));

        Vector3f b0 = new Vector3f(d0).mul(1.0f / 6.0f).add(new Vector3f(d1).mul(2.0f / 3.0f)).add(new Vector3f(d2).mul(1.0f / 6.0f));
        Vector3f b1 = new Vector3f(d1).mul(2.0f / 3.0f).add(new Vector3f(d2).mul(1.0f / 3.0f));
        Vector3f b2 = new Vector3f(d1).mul(1.0f / 3.0f).add(new Vector3f(d2).mul(2.0f / 3.0f));
        Vector3f b3 = new Vector3f(d1).mul(1.0f / 6.0f).add(new Vector3f(d2).mul(2.0f / 3.0f)).add(new Vector3f(d3).mul(1.0f / 6.0f));

        float dbv0 = -3 * (1-v) * (1-v);
        float dbv1 =  3 * (1-v) * (1-3*v);
        float dbv2 =  3 * v * (2-3*v);
        float dbv3 =  3 * v * v;

        return b0.mul(dbv0).add(b1.mul(dbv1)).add(b2.mul(dbv2)).add(b3.mul(dbv3));
    }

    @Override
    public Vector3f N(float u, float v) {
        return T(u, v).cross(B(u, v)).normalize();
    }

    @Override
    public boolean wrapsU() {
        int I = surface.length - 1;
        int J = surface[0].length - 1;
        Set<Point> e1 = new HashSet<>();
        Set<Point> e2 = new HashSet<>();
        Set<Point> e3 = new HashSet<>();
        Set<Point> e4 = new HashSet<>();
        Set<Point> e5 = new HashSet<>();
        Set<Point> e6 = new HashSet<>();
        for (int j = 0; j <= J; j++) {
            e1.add(surface[0][j]);
            e2.add(surface[1][j]);
            e3.add(surface[2][j]);
            e4.add(surface[I - 2][j]);
            e5.add(surface[I - 1][j]);
            e6.add(surface[I][j]);
        }
        return e1.containsAll(e4) && e2.containsAll(e5) && e3.containsAll(e6);
    }

    @Override
    public boolean wrapsV() {
        int I = surface.length - 1;
        int J = surface[0].length - 1;
        Set<Point> e1 = new HashSet<>();
        Set<Point> e2 = new HashSet<>();
        Set<Point> e3 = new HashSet<>();
        Set<Point> e4 = new HashSet<>();
        Set<Point> e5 = new HashSet<>();
        Set<Point> e6 = new HashSet<>();
        for (int i = 0; i <= I; i++) {
            e1.add(surface[i][0]);
            e2.add(surface[i][1]);
            e3.add(surface[i][2]);
            e4.add(surface[i][J - 2]);
            e5.add(surface[i][J - 1]);
            e6.add(surface[i][J]);
        }
        return e1.containsAll(e4) && e2.containsAll(e5) && e3.containsAll(e6);
    }

    @Override
    public void setTexture(Texture texture) {
        this.texture = texture;
    }

    @Override
    public Texture getTexture() {
        return texture;
    }

    @Override
    public boolean isRightSide() {
        return rightSide;
    }

    @Override
    public boolean isLeftSide() {
        return leftSide;
    }

    @Override
    public void setRightSide(boolean value) {
        this.rightSide = value;
    }

    @Override
    public void setLeftSide(boolean value) {
        this.leftSide = value;
    }
}
