package pl.edu.pw.mini.mg1.models;

import com.jogamp.opengl.GL4;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.joml.Vector4f;
import pl.edu.pw.mini.mg1.graphics.Texture;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.function.Function;
import java.util.stream.Collectors;

public class IntersectionCurve extends Curve {
    private final Intersectable P, Q;
    private final List<Vector2f> p, q;
    private final DiscreteParametersSpace pSpace, qSpace;
    private DiscreteParametersSpace pFill, qFill;

    public IntersectionCurve(List<Vector4f> parameters, Intersectable P, Intersectable Q) {
        super(parameters.stream().map(vec4 -> new Point(P.P(vec4.x, vec4.y))).collect(Collectors.toList()));
        setShowPolyline(true);
        this.p = parameters.stream().map(v -> new Vector2f(v.x, v.y)).collect(Collectors.toList());
        this.q = parameters.stream().map(v -> new Vector2f(v.z, v.w)).collect(Collectors.toList());
        this.P = P;
        this.Q = Q;
        pSpace = new DiscreteParametersSpace(p);
        pFill = pSpace.filled(P.wrapsU(), P.wrapsV());
        qSpace = new DiscreteParametersSpace(q);
        qFill = qSpace.filled(Q.wrapsU(), Q.wrapsV());
        if(P == Q) pFill = qFill = new DiscreteParametersSpace(pFill, qFill);
    }

    @Override
    protected void fillPointsList() {

    }

    @Override
    protected void load(GL4 gl) {
        super.load(gl);
        P.setTexture(pFill.toTexture(gl, P.wrapsU(), P.wrapsV()));
        if(P != Q) Q.setTexture(qFill.toTexture(gl, Q.wrapsU(), Q.wrapsV()));
    }

    @Override
    public void dispose(GL4 gl) {
        super.dispose(gl);
        if(P.getTexture() != null) P.getTexture().dispose(gl);
        if(P != Q) if(Q.getTexture() != null) Q.getTexture().dispose(gl);
    }

    private static class DiscreteParametersSpace {
        private static final int RESOLUTION = 1024;

        private final boolean[][] array = new boolean[RESOLUTION][RESOLUTION];

        private DiscreteParametersSpace(DiscreteParametersSpace other) {
            for (int i = 0; i < RESOLUTION; i++) {
                System.arraycopy(other.array[i], 0, array[i], 0, RESOLUTION);
            }
        }

        public DiscreteParametersSpace(DiscreteParametersSpace s, DiscreteParametersSpace t) {
            for (int i = 0; i < RESOLUTION; i++) {
                for (int j = 0; j < RESOLUTION; j++) {
                    if((s.array[i][j] && !t.array[i][j]) || (!s.array[i][j] && t.array[i][j])) {
                        array[i][j] = true;
                    }
                }
            }
        }

        public DiscreteParametersSpace(List<Vector2f> p) {
            List<Vector2f> unwrapped = new ArrayList<>();
            float wrapX = 0;
            float wrapY = 0;
            unwrapped.add(new Vector2f(p.get(0)).add(wrapX, wrapY));
            for (int i = 0; i < p.size() - 1; i++) {
                Vector2f p1 = p.get(i);
                Vector2f p2 = p.get(i + 1);
                if(p1.x - p2.x >  0.75f) wrapX += 1;
                if(p1.x - p2.x < -0.75f) wrapX -= 1;
                if(p1.y - p2.y >  0.75f) wrapY += 1;
                if(p1.y - p2.y < -0.75f) wrapY -= 1;
                unwrapped.add(new Vector2f(p2).add(wrapX, wrapY));
            }
            for (int i = 0; i < unwrapped.size() - 1; i++) {
                Vector2f p1 = unwrapped.get(i);
                Vector2f p2 = unwrapped.get(i + 1);
                drawLine(p1, p2);
            }
        }

        public DiscreteParametersSpace filled(boolean wrapU, boolean wrapV) {
            DiscreteParametersSpace filled = new DiscreteParametersSpace(this);
            Function<Integer, Integer> wrap = i -> i < 0 ? RESOLUTION + i : i >= RESOLUTION ? i - RESOLUTION : i;
            Function<Integer, Integer> clamp = i -> i < 0 ? 0 : i >= RESOLUTION ? RESOLUTION - 1 : i;
            for (int i = 0; i < RESOLUTION; i++) {
                for (int j = 0; j < RESOLUTION; j++) {
                    if(!filled.array[i][j]) {
                        filled.floodFill(i, j, wrapU ? wrap : clamp, wrapV ? wrap : clamp);
                        return filled;
                    }
                }
            }
            return filled;
        }

        private void floodFill(int x, int y, Function<Integer, Integer> wrapU,  Function<Integer, Integer> wrapV) {
            Queue<Vector2i> Q = new LinkedList<>();
            Q.add(new Vector2i(x, y));
            while(!Q.isEmpty()) {
                Vector2i n = Q.poll();
                if(!array[n.x][n.y]) {
                    array[n.x][n.y] = true;
                    Q.add(new Vector2i(wrapU.apply(n.x + 1), n.y));
                    Q.add(new Vector2i(wrapU.apply(n.x - 1), n.y));
                    Q.add(new Vector2i(n.x, wrapV.apply(n.y + 1)));
                    Q.add(new Vector2i(n.x, wrapV.apply(n.y - 1)));
                }
            }
        }

        private void drawLine(Vector2f from, Vector2f to) {
            int x1 = (int)(from.x * RESOLUTION);
            int y1 = (int)(from.y * RESOLUTION);
            int x2 = (int)(to.x * RESOLUTION);
            int y2 = (int)(to.y * RESOLUTION);
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
            drawPixel(x, y);
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
                    drawPixel(x, y);
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
                    drawPixel(x, y);
                }
            }
        }

        private void drawPixel(int i, int j) {
            while(i < 0) i += RESOLUTION;
            while(i >= RESOLUTION) i -= RESOLUTION;
            while(j < 0) j += RESOLUTION;
            while(j >= RESOLUTION) j -= RESOLUTION;
            array[i][j] = true;
        }

        public Texture toTexture(GL4 gl, boolean wrapU, boolean wrapV) {
            return new Texture(gl, RESOLUTION, (i, j) -> array[i][j] ? new Vector3f(1) : new Vector3f(), wrapU, wrapV);
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < RESOLUTION; i++) {
                for (int j = 0; j < RESOLUTION; j++) {
                    builder.append(array[i][j] ? '%' : ' ');
                }
                builder.append('\n');
            }
            return builder.toString();
        }
    }
}
