package pl.edu.pw.mini.mg1.milling;

import org.joml.*;
import pl.edu.pw.mini.mg1.models.Intersectable;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static org.joml.Math.*;

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
        MillingTool.Cache cache = tool.new Cache(this);
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
                for (int i = -cache.getSize().x; i <= cache.getSize().x; i++) {
                    for (int j = -cache.getSize().y; j <= cache.getSize().y; j++) {
                        if(Float.isNaN(baseHeight - cache.getShape()[i + cache.getSize().x][j + cache.getSize().y])) {
                            continue;
                        }
                        float toolShapeCorrection = cache.getShape()[i + cache.getSize().x][j + cache.getSize().y];
                        float h = baseHeight + toolShapeCorrection;
                        int X = x + i;
                        int Y = y + j;
                        if (X >= 0 && Y >= 0 && X < resolution.x && Y < resolution.y) {
                            float diff = getHeight(X, Y) - h;
                            if(diff <= 0) continue;
                            if(tool.flat() && downMove) {
                                throw new MillingException("Flat tool went down");
                            }
                            float toolLengthCorrection = tool.flat() ? 0 : tool.radius() - toolShapeCorrection;
                            if(diff > tool.length() + toolLengthCorrection) {
                                throw new MillingException("Too deep, tool length was %.2f(%.2f after considering it's shape) mm, but tried to mill %.2f mm of material".formatted(tool.length(), tool.length() + toolLengthCorrection, diff));
                            }
                            float newH = min(getHeight(X, Y), h);
                            if(newH < minHeight) {
                                throw new MillingException("Too low, tried to mill to %f mm height, but limit was set to %f mm".formatted(newH, minHeight));
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

    public void renderPatches(List<Intersectable> patches) {
        Arrays.fill(heights, minHeight);
        for (Intersectable patch : patches) {
            int samples = 3000;
            for (int i = 0; i <= samples; i++) {
                float u = (float) i / samples;
                for (int j = 0; j <= samples; j++) {
                    float v = (float) j / samples;
                    Vector3f p = patch.P(u, v).mul(10);
                    int I = clamp(0, this.resolution.x - 1, (int) (((p.x + size.x / 2) / size.x) * this.resolution.x));
                    int J = clamp(0, this.resolution.y - 1, (int) (((p.z + size.y / 2) / size.y) * this.resolution.y));
                    if(getHeight(I, J) < p.y + minHeight) setHeight(I, J, p.y + minHeight);
                }
            }
        }
    }

    public float findMaxHeight(float posX, float posY, MillingTool.Cache cache) {
        int I = clamp(0, resolution.x - 1, (int) (((posX + size.x / 2) / size.x) * resolution.x));
        int J = clamp(0, resolution.y - 1, (int) (((posY + size.y / 2) / size.y) * resolution.y));
        
        float maxH = minHeight;

        for (int i = -cache.getSize().x; i <= cache.getSize().x; i++) {
            for (int j = -cache.getSize().y; j <= cache.getSize().y; j++) {
                if(Float.isNaN(cache.getShape()[i + cache.getSize().x][j + cache.getSize().y])) {
                    continue;
                }
                float toolShapeCorrection = cache.getShape()[i + cache.getSize().x][j + cache.getSize().y];
                int X = I + i;
                int Y = J + j;
                if (X >= 0 && Y >= 0 && X < resolution.x && Y < resolution.y) {
                    float h = getHeight(X, Y) - toolShapeCorrection;
                    if(h > maxH) maxH = h;
                }
            }
        }
        return maxH;
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
