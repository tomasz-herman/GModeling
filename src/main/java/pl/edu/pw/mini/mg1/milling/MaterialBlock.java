package pl.edu.pw.mini.mg1.milling;

import org.joml.*;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static com.jogamp.opengl.math.FloatUtil.abs;
import static org.joml.Math.min;
import static org.joml.Math.sqrt;

public class MaterialBlock {
    private final Vector2f size;
    private final Vector2i resolution;
    private final float minHeight;

    private final float[] heights;
    private final Vector2f[] positions;
    private final float originalHeight;

    private void setHeight(int i, int j, float val) {
        final int width = resolution.x;
        heights[j * width + i] = val;
    }

    private float getHeight(int i, int j) {
        final int width = resolution.x;
        return heights[j * width + i];
    }

    public MaterialBlock(Vector2f size, Vector2i resolution, float height, float minHeight) {
        this.size = new Vector2f(size);
        this.resolution = new Vector2i(resolution);
        this.minHeight = minHeight;
        this.originalHeight = height;

        heights = new float[resolution.x * resolution.y];
        positions = new Vector2f[resolution.x * resolution.y];
        for (int i = 0; i < resolution.x; i++) {
            for (int j = 0; j < resolution.y; j++) {
                setHeight(i, j, height);
                positions[j * resolution.x + i] = new Vector2f(
                        (float) i / resolution.x * size.x - size.x * 0.5f,
                        (float) j / resolution.y * size.y - size.y * 0.5f);
            }
        }
    }

    public void mill(MillingTool tool, Path path, Consumer<Integer> progress, Consumer<Vector3f> moveTool, Runnable updateTexture) {
        Vector2i toolSize = new Vector2i((int)(tool.getRadius() * resolution.x / size.x), (int)(tool.getRadius() * resolution.y / size.y));
        float[][] toolStepCache = new float[1 + toolSize.x * 2][1 + toolSize.y * 2];
        for (int i = 0; i < toolStepCache.length; i++) {
            for (int j = 0; j < toolStepCache[i].length; j++) {
                float x = (i - toolSize.x) * size.x / resolution.x;
                float y = (j - toolSize.y) * size.y / resolution.y;
                if(x * x + y * y > tool.getRadius() * tool.getRadius()) {
                    toolStepCache[i][j] = Float.NaN;
                } else {
                    if(tool.isFlat()) {
                        toolStepCache[i][j] = 0.0f;
                    } else {
                        toolStepCache[i][j] = tool.getRadius() - sqrt(tool.getRadius() * tool.getRadius() - x * x - y * y);
                    }
                }
            }
        }
        Vector3fc lastCoord = path.getCoords().get(0);
        int iter = 0;
        int lastReportedProgress = -1;
        for (Vector3fc nextCoord : path.getCoords()) {
            Vector2i from = new Vector2i((int)(lastCoord.x() / size.x * resolution.x + resolution.x / 2), (int)(lastCoord.y() / size.y * resolution.y + resolution.y / 2));
            Vector2i to   = new Vector2i((int)(nextCoord.x() / size.x * resolution.x + resolution.x / 2), (int)(nextCoord.y() / size.y * resolution.y + resolution.y / 2));
            float totalDist = (float) from.distance(to);
            boolean downMove = nextCoord.z() < lastCoord.z();
            Vector3fc currentLastCoord = lastCoord;
            drawLine(from, to, (x, y) -> {
                float currDist = (float) from.distance(x, y);
                float procent = currDist / totalDist;
                float baseHeight = totalDist == 0 ? nextCoord.z() : (1 - procent) * currentLastCoord.z() + procent * nextCoord.z();
                if(moveTool != null) {
                    if (x >= 0 && y >= 0 && x < resolution.x && y < resolution.y) {
                        Vector2f pos = positions[y * resolution.x + x];
                        moveTool.accept(new Vector3f(pos.x, baseHeight, pos.y));
                    } else {
                        moveTool.accept(new Vector3f((float) x / resolution.x * size.x - size.x * 0.5f,
                                baseHeight, (float) y / resolution.y * size.y - size.y * 0.5f));
                    }
                }
                for (int i = -toolSize.x; i <= toolSize.x; i++) {
                    for (int j = -toolSize.y; j <= toolSize.y; j++) {
                        if(Float.isNaN(baseHeight - toolStepCache[i + toolSize.x][j + toolSize.y])) {
                            continue;
                        }
                        float toolShapeCorrection = toolStepCache[i + toolSize.x][j + toolSize.y];
                        float h = baseHeight + toolShapeCorrection;
                        int X = x + i;
                        int Y = y + j;
                        if (X >= 0 && Y >= 0 && X < resolution.x && Y < resolution.y) {
                            float diff = getHeight(X, Y) - h;
                            if(diff <= 0) continue;
                            if(tool.isFlat() && downMove) {
                                throw new MillingException("Flat tool went down");
                            }
                            float toolLengthCorrection = tool.isFlat() ? 0 : tool.getRadius() - toolShapeCorrection;
                            if(diff > tool.getLength() + toolLengthCorrection) {
                                throw new MillingException("Too deep, tool length was %.2f(%.2f after considering it's shape) mm, but tried to mill %.2f mm of material".formatted(tool.getLength(), tool.getLength() + toolLengthCorrection, diff));
                            }
                            float newH = min(getHeight(X, Y), h);
                            if(newH < minHeight) {
                                throw new MillingException("Too low, tried to mill to %.2f mm height, but limit was set to %.2f mm".formatted(getHeight(X, Y), getMinHeight()));
                            }
                            setHeight(X, Y, newH);
                        }
                    }
                }
            });
            lastCoord = nextCoord;
            iter ++;
            float currProgress = 100 * ((float)iter / path.getCoords().size());
            if((int)currProgress != lastReportedProgress) {
                lastReportedProgress = (int)currProgress;
                progress.accept((int)currProgress);
            }
            if(updateTexture != null) updateTexture.run();
        }
    }

    private void drawLine(Vector2i from, Vector2i to, BiConsumer<Integer, Integer> mid) {
        int x1 = from.x;
        int y1 = from.y;
        int x2 = to.x;
        int y2 = to.y;
        int d, dx, dy, ai, bi, xi, yi;
        int x = x1, y = y1;
        if (x1 < x2) {
            xi = 1;
            dx = x2 - x1;
        } else {
            xi = -1;
            dx = x1 - x2;
        }
        if (y1 < y2) {
            yi = 1;
            dy = y2 - y1;
        } else {
            yi = -1;
            dy = y1 - y2;
        }
        mid.accept(x, y);
        if (dx > dy) {
            ai = (dy - dx) * 2;
            bi = dy * 2;
            d = bi - dx;
            while (x != x2) {
                if (d >= 0) {
                    x += xi;
                    y += yi;
                    d += ai;
                } else {
                    d += bi;
                    x += xi;
                }
                mid.accept(x, y);
            }
        } else {
            ai = ( dx - dy ) * 2;
            bi = dx * 2;
            d = bi - dy;
            while (y != y2) {
                if (d >= 0) {
                    x += xi;
                    y += yi;
                    d += ai;
                } else {
                    d += bi;
                    y += yi;
                }
                mid.accept(x, y);
            }
        }
    }

    public Vector2fc getSize() {
        return size;
    }

    public Vector2ic getResolution() {
        return resolution;
    }

    public float getMinHeight() {
        return minHeight;
    }

    public float[] getHeights() {
        return heights;
    }

    public float getOriginalHeight() {
        return originalHeight;
    }
}
