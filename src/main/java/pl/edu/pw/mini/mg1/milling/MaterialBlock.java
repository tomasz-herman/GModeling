package pl.edu.pw.mini.mg1.milling;

import org.joml.*;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static org.joml.Math.min;
import static org.joml.Math.sqrt;

public class MaterialBlock {
    private final Vector2f size;
    private final Vector2i resolution;
    private final float depth;
    private final float maxMillingDepth;

    private final float[][] heights;

    public MaterialBlock(Vector2f size, Vector2i resolution, float depth, float maxMillingDepth) {
        this.size = new Vector2f(size);
        this.resolution = new Vector2i(resolution);
        this.depth = depth;
        this.maxMillingDepth = maxMillingDepth;

        heights = new float[resolution.x][resolution.y];
        for (int i = 0; i < resolution.x; i++) {
            for (int j = 0; j < resolution.y; j++) {
                heights[i][j] = depth;
            }
        }
    }

    public void mill(MillingTool tool, Path path, Consumer<Float> progress) {
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
        Vector3fc lastCoord = new Vector3f(0, 0, 300);
        int iter = 0;
        for (Vector3fc nextCoord : path.getCoords()) {
            Vector2i from = new Vector2i((int)(lastCoord.x() / size.x * resolution.x + resolution.x / 2), (int)(lastCoord.y() / size.y * resolution.y + resolution.y / 2));
            Vector2i to   = new Vector2i((int)(nextCoord.x() / size.x * resolution.x + resolution.x / 2), (int)(nextCoord.y() / size.y * resolution.y + resolution.y / 2));
            float totalDist = (float) from.distance(to);
            Vector3fc currentLastCoord = lastCoord;
            drawLine(from, to, (x, y) -> {
                float currDist = (float) from.distance(x, y);
                float procent = currDist / totalDist;
                float baseHeight = (1 - procent) * currentLastCoord.z() + procent * nextCoord.z();
                for (int i = -toolSize.x; i <= toolSize.x; i++) {
                    for (int j = -toolSize.y; j <= toolSize.y; j++) {
                        float h = baseHeight + toolStepCache[i + toolSize.x][j + toolSize.y];
                        int X = x + i;
                        int Y = y + j;
                        if (X >= 0 && Y >= 0 && X < heights.length && Y < heights[0].length) {
                            float diff = heights[X][Y] - h;
                            if(diff > tool.getLength()) {
                                throw new MillingException("Too deep");
                            }
                            heights[X][Y] = min(heights[X][Y], h);
                            if(heights[X][Y] < maxMillingDepth) {
                                throw new MillingException("Too low");
                            }
                        }
                    }
                }
            });
            lastCoord = nextCoord;
            iter ++;
            progress.accept((float) iter / path.getCoords().size());
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

    public float getDepth() {
        return depth;
    }

    public float getMaxMillingDepth() {
        return maxMillingDepth;
    }
}
