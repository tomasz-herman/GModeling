package pl.edu.pw.mini.mg1.models;

import com.jogamp.opengl.GL4;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import pl.edu.pw.mini.mg1.cameras.PerspectiveCamera;
import pl.edu.pw.mini.mg1.graphics.Renderer;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.jogamp.opengl.math.FloatUtil.PI;
import static com.jogamp.opengl.math.FloatUtil.tan;
import static org.joml.Math.cos;
import static org.joml.Math.sin;

public class BezierPatchC0 extends Patch implements Intersectable {

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
        for (int i = 0; i < xp - 1; i += 3) {
            for (int j = 0; j < yp - 1; j += 3) {
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
        patch.surface = new Point[xp + 1][yp];
        float wx = 2 * PI / (x * 3);
        float dx = 2 * PI / (x * 4);
        float hy = h / (y * 3);
        Function<Float, Float> fx = phi -> r * cos(phi);
        Function<Float, Float> fz = phi -> r * sin(phi);
        Function<Float, Vector2f> fxz2 = x > 1 ? phi -> {
            Vector2f fxz1 = new Vector2f(fx.apply(phi - wx), fz.apply(phi - wx));
            Vector2f fxz1t = new Vector2f(-fxz1.y, fxz1.x);
            return fxz1.fma(1.33333f * tan(dx), fxz1t);
        } : phi -> {
            Vector2f fxz1 = new Vector2f(fx.apply(phi - wx), fz.apply(phi - wx)).negate();
            Vector2f fxz11 = new Vector2f(-fxz1.y, fxz1.x);
            return fxz1.fma(3, fxz11).mul(1.42f);
        };
        Function<Float, Vector2f> fxz3 = x > 1 ? phi -> {
            Vector2f fxz4 = new Vector2f(fx.apply(phi + wx), fz.apply(phi + wx));
            Vector2f fxz4t = new Vector2f(fxz4.y, -fxz4.x);
            return fxz4.fma(1.33333f * tan(dx), fxz4t);
        } : phi -> {
            Vector2f fxz4 = new Vector2f(fx.apply(phi + wx), fz.apply(phi + wx)).negate();
            Vector2f fxz44 = new Vector2f(fxz4.y, -fxz4.x);
            return fxz4.fma(3, fxz44).mul(1.42f);
        };
        for (int i = 0; i < xp; i += 3) {
            for (int j = 0; j < yp; j++) {
                Point point = new Point(fx.apply(wx * i), hy * j, fz.apply(wx * i));
                point.addPropertyChangeListener(patch.pcl);
                patch.surface[i][j] = point;

                Vector2f xz = fxz2.apply(wx * (i + 1));
                point = new Point(xz.x, hy * j, xz.y);
                point.addPropertyChangeListener(patch.pcl);
                patch.surface[i + 1][j] = point;

                xz = fxz3.apply(wx * (i + 2));
                point = new Point(xz.x, hy * j, xz.y);
                point.addPropertyChangeListener(patch.pcl);
                patch.surface[i + 2][j] = point;
            }
        }
        Function<Integer, Integer> mod = i -> i % xp;
        for (int i = 0; i < xp; i += 3) {
            for (int j = 0; j < yp - 1; j += 3) {
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
        System.arraycopy(patch.surface[0], 0, patch.surface[xp], 0, yp);
        patch.polyMesh = new PolyMesh(patch.surface);
        return patch;
    }

    @Override
    public void render(GL4 gl, PerspectiveCamera camera, Renderer renderer) {
        super.render(gl, camera, renderer);
        renderer.renderPatch(gl, camera, this);
    }

    public List<Point[]> getEdges() {
        int I = surface.length - 1;
        int J = surface[0].length - 1;
        List<Point[]> edges = new ArrayList<>();
        for (int i = 0; i < I; i+=3) {
            edges.add(new Point[] {surface[i][0], surface[i + 1][0], surface[i + 2][0], surface[i + 3][0]});
            edges.add(new Point[] {surface[i][I], surface[i + 1][I], surface[i + 2][I], surface[i + 3][I]});
        }
        for (int j = 0; j < J; j+=3) {
            edges.add(new Point[] {surface[0][j], surface[0][j + 1], surface[0][j + 2], surface[0][j + 3]});
            edges.add(new Point[] {surface[J][j], surface[J][j + 1], surface[J][j + 2], surface[J][j + 3]});
        }
        return edges;
    }

    public List<Point[][]> miniPatches() {
        List<Point[][]> patches = new ArrayList<>();
        for (int i = 0; i < surface.length - 1; i+=3) {
            for (int j = 0; j < surface[0].length - 1; j+=3) {
                patches.add(new Point[][] {
                        {surface[i][j    ], surface[i + 1][j    ], surface[i + 2][j    ], surface[i + 3][j    ]},
                        {surface[i][j + 1], surface[i + 1][j + 1], surface[i + 2][j + 1], surface[i + 3][j + 1]},
                        {surface[i][j + 2], surface[i + 1][j + 2], surface[i + 2][j + 2], surface[i + 3][j + 2]},
                        {surface[i][j + 3], surface[i + 1][j + 3], surface[i + 2][j + 3], surface[i + 3][j + 3]}
                });
            }
        }
        return patches;
    }

    @Override
    public String serialize() {
        return """
                  <PatchC0 Name="%s" N="%d" M="%d" NSlices="%d" MSlices="%d">
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
        if(node.getNodeType() == Node.ELEMENT_NODE) {
            Element patchC0Element = (Element) node;
            setName(patchC0Element.getAttribute("Name"));
            setDivisionsU(Integer.parseInt(patchC0Element.getAttribute("NSlices")));
            setDivisionsV(Integer.parseInt(patchC0Element.getAttribute("MSlices")));

            int n = Integer.parseInt(patchC0Element.getAttribute("N"));
            int m = Integer.parseInt(patchC0Element.getAttribute("M"));

            NodeList pointsRefs = ((Element)patchC0Element
                    .getElementsByTagName("Points").item(0))
                    .getElementsByTagName("PointRef");

            surface = new Point[3 * m + 1][3 * n + 1];

            for (int i = 0; i < 3 * m + 1; i++) {
                for (int j = 0; j < 3 * n + 1; j++) {
                    Element pointRefElement = (Element) pointsRefs.item(i * (3 * n + 1) + j);
                    Point point = points.get(pointRefElement.getAttribute("Name"));
                    point.addPropertyChangeListener(pcl);
                    surface[i][j] = point;
                }
            }

            for (int i = 0; i < m; i ++) {
                for (int j = 0; j < n; j++) {
                    this.points.addAll(List.of(
                            surface[3 * i][3 * j],
                            surface[3 * i + 1][3 * j],
                            surface[3 * i + 2][3 * j],
                            surface[3 * i + 3][3 * j],

                            surface[3 * i][3 * j +  1],
                            surface[3 * i + 1][3 * j + 1],
                            surface[3 * i + 2][3 * j + 1],
                            surface[3 * i + 3][3 * j + 1],

                            surface[3 * i][3 * j + 2],
                            surface[3 * i + 1][3 * j + 2],
                            surface[3 * i + 2][3 * j + 2],
                            surface[3 * i + 3][3 * j + 2],

                            surface[3 * i][3 * j + 3],
                            surface[3 * i + 1][3 * j + 3],
                            surface[3 * i + 2][3 * j + 3],
                            surface[3 * i + 3][3 * j + 3]
                    ));
                }
            }
            polyMesh = new PolyMesh(surface);
        }
        return this;
    }

    @Override
    public Vector3f P(float u, float v) {
        return null;
    }

    @Override
    public Vector3f T(float u, float v) {
        return null;
    }

    @Override
    public Vector3f B(float u, float v) {
        return null;
    }

    @Override
    public Vector3f N(float u, float v) {
        return null;
    }

    @Override
    public boolean wrapsU() {
        int I = surface.length - 1;
        int J = surface[0].length - 1;
        Set<Point> e1 = new HashSet<>();
        Set<Point> e2 = new HashSet<>();
        for (int i = 0; i <= I; i++) {
            e1.add(surface[i][0]);
            e2.add(surface[i][J]);
        }
        return e1.containsAll(e2);
    }

    @Override
    public boolean wrapsV() {
        int I = surface.length - 1;
        int J = surface[0].length - 1;
        Set<Point> e1 = new HashSet<>();
        Set<Point> e2 = new HashSet<>();
        for (int j = 0; j <= J; j++) {
            e1.add(surface[0][j]);
            e2.add(surface[I][j]);
        }
        return e1.containsAll(e2);
    }
}
