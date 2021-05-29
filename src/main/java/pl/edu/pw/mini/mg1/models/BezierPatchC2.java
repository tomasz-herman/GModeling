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

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.jogamp.opengl.math.FloatUtil.PI;
import static org.joml.Math.cos;
import static org.joml.Math.sin;

public class BezierPatchC2 extends Patch {

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
    public String serialize() {
        return """
                  <PatchC2 Name="%s" N="%d" M="%d" NSlices="%d" MSlices="%d">
                    <Points>
                %s
                    </Points>
                  </PatchC0>
                """.formatted(
                getName(), (surface[0].length - 1) / 3, (surface.length - 1) / 3, divisionsU, divisionsV,
                IntStream.range(0, surface.length).boxed().flatMap(
                        i -> IntStream.range(0, surface[i].length)
                                .mapToObj(j -> "      <PointRef Name=\"%s\"/>".formatted(surface[i][j].getName()))
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
}
