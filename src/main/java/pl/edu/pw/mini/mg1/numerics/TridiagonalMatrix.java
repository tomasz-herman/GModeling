package pl.edu.pw.mini.mg1.numerics;

public class TridiagonalMatrix {
    private final float[] a;
    private final float[] b;
    private final float[] c;
    private final float[] d;
    private final int n;

    public TridiagonalMatrix(float[] a, float[] b, float[] c, float[] d) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
        this.n = d.length;
    }

    public float[] solve() {
        float[] x = new float[n];
        float[] cc = new float[n-1];
        float[] dd = new float[n];
        cc[0] = c[0] / b[0];
        for (int i = 1; i < n - 1; i++) {
            cc[i] = c[i] / (b[i] - a[i - 1] * cc[i - 1]);
        }
        dd[0] = d[0] / b[0];
        for (int i = 1; i < n; i++) {
            dd[i] = (d[i] - a[i - 1] * dd[i - 1]) / (b[i] - a[i - 1] * cc[i - 1]);
        }
        x[n - 1] = dd[n - 1];
        for (int i = n - 2; i >= 0; i--) {
            x[i] = dd[i] - cc[i] * x[i + 1];
        }
        return x;
    }
}
