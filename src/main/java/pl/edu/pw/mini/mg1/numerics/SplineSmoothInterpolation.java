package pl.edu.pw.mini.mg1.numerics;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import pl.edu.pw.mini.mg1.utils.StreamUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class SplineSmoothInterpolation {
    private final int n;
    private float[] d;
    private float[] a;
    private float[] b;
    private float[] e;
    private float[] cx, cy, cz;
    private Vector3f[] A;

    public SplineSmoothInterpolation(Vector3f[] points) {
        this.A = points;
        this.n = points.length;
        if(n < 3) return;
        d = new float[n - 1];
        for (int i = 0; i < n - 1; i++) {
            d[i] = points[i].distance(points[i + 1]);
        }
        cx = new float[n - 2];
        for (int i = 1; i < n - 1; i++) {
            cx[i - 1] = 3 * ((points[i + 1].x - points[i].x) / d[i] - (points[i].x - points[i - 1].x) / d[i - 1]) / (d[i - 1] + d[i]);
        }
        cy = new float[n - 2];
        for (int i = 1; i < n - 1; i++) {
            cy[i - 1] = 3 * ((points[i + 1].y - points[i].y) / d[i] - (points[i].y - points[i - 1].y) / d[i - 1]) / (d[i - 1] + d[i]);
        }
        cz = new float[n - 2];
        for (int i = 1; i < n - 1; i++) {
            cz[i - 1] = 3 * ((points[i + 1].z - points[i].z) / d[i] - (points[i].z - points[i - 1].z) / d[i - 1]) / (d[i - 1] + d[i]);
        }
        e = new float[n - 2];
        for (int i = 0; i < n - 2; i++) {
            e[i] = 2;
        }
        if(n == 3) return;
        a = new float[n - 3];
        for (int i = 2; i < n - 1; i++) {
            a[i - 2] = d[i - 1] / (d[i - 1] + d[i]);
        }
        b = new float[n - 3];
        for (int i = 1; i < n - 2; i++) {
            b[i - 1] = d[i] / (d[i - 1] + d[i]);
        }
    }

    private float[] solveX() {
        TridiagonalMatrix tridiagonalMatrix = new TridiagonalMatrix(a, e, b, cx);
        return tridiagonalMatrix.solve();
    }

    private float[] solveY() {
        TridiagonalMatrix tridiagonalMatrix = new TridiagonalMatrix(a, e, b, cy);
        return tridiagonalMatrix.solve();
    }

    private float[] solveZ() {
        TridiagonalMatrix tridiagonalMatrix = new TridiagonalMatrix(a, e, b, cz);
        return tridiagonalMatrix.solve();
    }

    @SuppressWarnings("SuspiciousNameCombination")
    public List<Vector3f> solve() {
        if(n < 2) {
            return Collections.emptyList();
        } else if(n == 2) {
            return List.of(
                    A[0],
                    A[0].lerp(A[1], 1.0f / 3.0f, new Vector3f()),
                    A[0].lerp(A[1], 2.0f / 3.0f, new Vector3f()),
                    A[1]);
        }
        Vector3f[] C;
        if(n == 3) {
            C = new Vector3f[] {
                    new Vector3f(0, 0, 0),
                    new Vector3f(cx[0]/2, cy[0]/2, cz[0]/2),
                    new Vector3f(0, 0, 0)
            };
        } else {
            float[] x = solveX();
            float[] y = solveY();
            float[] z = solveZ();
            C = StreamUtils.concat(
                    Stream.of(new Vector3f(0, 0, 0)),
                    IntStream.range(0, x.length).mapToObj(i -> new Vector3f(x[i], y[i], z[i])),
                    Stream.of(new Vector3f(0, 0, 0))
            ).toArray(Vector3f[]::new);
        }

        Vector3f[] B = new Vector3f[C.length];
        Vector3f[] D = new Vector3f[C.length];
        for (int i = 1; i < C.length; i++) {
            D[i - 1] = new Vector3f();
            B[i - 1] = new Vector3f();
            D[i - 1].x = (C[i].x - C[i - 1].x) / (3 * d[i - 1]);
            B[i - 1].x = (A[i].x - A[i - 1].x - C[i - 1].x * d[i - 1] * d[i - 1] - D[i - 1].x * d[i - 1] * d[i - 1] * d[i - 1]) / d[i - 1];
            D[i - 1].y = (C[i].y - C[i - 1].y) / (3 * d[i - 1]);
            B[i - 1].y = (A[i].y - A[i - 1].y - C[i - 1].y * d[i - 1] * d[i - 1] - D[i - 1].y * d[i - 1] * d[i - 1] * d[i - 1]) / d[i - 1];
            D[i - 1].z = (C[i].z - C[i - 1].z) / (3 * d[i - 1]);
            B[i - 1].z = (A[i].z - A[i - 1].z - C[i - 1].z * d[i - 1] * d[i - 1] - D[i - 1].z * d[i - 1] * d[i - 1] * d[i - 1]) / d[i - 1];
        }
        Matrix4f baseChange = new Matrix4f(
                1, 1, 1, 1,
                0, 1/3f, 2/3f, 1,
                0, 0, 1/3f, 1,
                0, 0, 0, 1);
        List<Vector3f> BC = new ArrayList<>();
        for (int i = 0; i < C.length - 1; i++) {
            Vector4f px = new Vector4f(A[i].x, B[i].x, C[i].x, D[i].x);
            Vector4f py = new Vector4f(A[i].y, B[i].y, C[i].y, D[i].y);
            Vector4f pz = new Vector4f(A[i].z, B[i].z, C[i].z, D[i].z);
            baseChange.transform(px);
            baseChange.transform(py);
            baseChange.transform(pz);
            float t1 = d[i];
            float t2 = t1 * t1;
            float t3 = t2 * t1;
            float omt1 = 1 - t1;
            float omt2 = omt1 * omt1;
            float omt3 = omt2 * omt1;
            float X1 = px.x;
            float X2 = px.x * omt1 + px.y * t1;
            float X3 = px.x * omt2 + 2.0f * px.y * t1 * omt1 + px.z * t2;
            float X4 = px.x * omt3 + 3.0f * px.y * t1 * omt2 + 3.0f * px.z * t2 * omt1 + px.w * t3;
            float Y1 = py.x;
            float Y2 = py.x * omt1 + py.y * t1;
            float Y3 = py.x * omt2 + 2.0f * py.y * t1 * omt1 + py.z * t2;
            float Y4 = py.x * omt3 + 3.0f * py.y * t1 * omt2 + 3.0f * py.z * t2 * omt1 + py.w * t3;
            float Z1 = pz.x;
            float Z2 = pz.x * omt1 + pz.y * t1;
            float Z3 = pz.x * omt2 + 2.0f * pz.y * t1 * omt1 + pz.z * t2;
            float Z4 = pz.x * omt3 + 3.0f * pz.y * t1 * omt2 + 3.0f * pz.z * t2 * omt1 + pz.w * t3;
            BC.add(new Vector3f(X1, Y1, Z1));
            BC.add(new Vector3f(X2, Y2, Z2));
            BC.add(new Vector3f(X3, Y3, Z3));
            BC.add(new Vector3f(X4, Y4, Z4));
        }
        return BC;
    }
}
