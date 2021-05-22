package pl.edu.pw.mini.mg1.models;

import com.jogamp.opengl.GL4;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import pl.edu.pw.mini.mg1.cameras.PerspectiveCamera;
import pl.edu.pw.mini.mg1.graphics.Renderer;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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

    @Override
    public void render(GL4 gl, PerspectiveCamera camera, Renderer renderer) {
        super.render(gl, camera, renderer);
        renderer.renderSplinePatch(gl, camera, this);
    }

    @Override
    public String serialize() {
        return """
                  <PatchC2 Name="%s" ShowControlPolygon="%d" WrapDirection="None" RowSlices="%d" ColumnSlices="%d">
                    <Points>
                %s
                    </Points>
                  </PatchC2>
                """.formatted(
                getName(), 0, divisionsU, divisionsV,
                IntStream.range(0, surface.length).boxed().flatMap(
                        i -> IntStream.range(0, surface[i].length)
                                .mapToObj(j -> "      <PointRef Name=\"%s\" Row=\"%d\" Column=\"%d\"/>".formatted(surface[i][j].getName(), i, j))
                ).collect(Collectors.joining("\n"))
        );
    }

    @Override
    public Model deserialize(Node node, Map<String, Point> points) {
        if(node.getNodeType() == Node.ELEMENT_NODE) {
            Element patchC2Element = (Element) node;
            setName(patchC2Element.getAttribute("Name"));
            String wrap = patchC2Element.getAttribute("WrapDirection");

            NodeList pointsRefs = ((Element)patchC2Element
                    .getElementsByTagName("Points").item(0))
                    .getElementsByTagName("PointRef");

            int rows = 0;
            int cols = 0;
            for (int i = 0; i < pointsRefs.getLength(); i++) {
                Element pointRefElement = (Element) pointsRefs.item(i);
                int row = Integer.parseInt(pointRefElement.getAttribute("Row")) + 1;
                int col = Integer.parseInt(pointRefElement.getAttribute("Column")) + 1;
                if(row > rows) rows = row;
                if(col > cols) cols = col;
            }

            switch (wrap) {
                case "Row" -> rows+=3;
                case "Column" -> cols+=3;
            }

            surface = new Point[rows][cols];

            for (int i = 0; i < pointsRefs.getLength(); i++) {
                Element pointRefElement = (Element) pointsRefs.item(i);
                Point point = points.get(pointRefElement.getAttribute("Name"));
                point.addPropertyChangeListener(pcl);
                int row = Integer.parseInt(pointRefElement.getAttribute("Row"));
                int col = Integer.parseInt(pointRefElement.getAttribute("Column"));
                surface[row][col] = point;
            }

            switch (wrap) {
                case "Row" -> {
                    System.arraycopy(surface[0], 0, surface[rows - 3], 0, cols);
                    System.arraycopy(surface[1], 0, surface[rows - 2], 0, cols);
                    System.arraycopy(surface[2], 0, surface[rows - 1], 0, cols);
                }
                case "Column" -> {
                    for (int j = 0; j < rows; j++) {
                        surface[j][cols - 3] = surface[j][0];
                    }
                    for (int j = 0; j < rows; j++) {
                        surface[j][cols - 2] = surface[j][1];
                    }
                    for (int j = 0; j < rows; j++) {
                        surface[j][cols - 1] = surface[j][2];
                    }
                }
            }

            for (int i = 0; i < rows - 3; i ++) {
                for (int j = 0; j < cols - 3; j ++) {
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
