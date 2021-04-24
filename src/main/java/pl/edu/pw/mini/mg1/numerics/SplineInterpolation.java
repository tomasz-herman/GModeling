package pl.edu.pw.mini.mg1.numerics;

public class SplineInterpolation {
    private static final int MAX_CACHE_SIZE = 65534;
    private static final float[] M = generateMVector();

    private final float[] S;
    private final float[] R;
    private final float[] B;
    private final int n;

    public SplineInterpolation(float... coordinates) {
        this.n = coordinates.length - 1;
        this.S = coordinates;
        this.R = new float[n + 1];
        this.B = new float[n + 1];
    }

    public float[] solve() {
        if(n < 2) return S;
        B[0] = S[0];
        B[n] = S[n];
        R[1] = 6 * S[1] - S[0];
        for (int i = 2; i < n - 1; i++) {
            R[i] = 6 * S[i] - M[i - 1] * R[i - 1];
        }
        R[n - 1] = (6 * S[n - 1] - S[n]) - M[n - 2] * R[n - 2];
        B[n - 1] = M[n - 1] * R[n - 1];
        for (int i = n - 2; i > 0; i--) {
            B[i] = M[i] * (R[i] - B[i + 1]);
        }
        return B;
    }

    private static float[] generateMVector() {
        float[] m = new float[MAX_CACHE_SIZE];
        m[1] = 0.25f;
        for (int i = 2; i < MAX_CACHE_SIZE; i++) {
            m[i] = 1.0f / (4 - m[i -1]);
        }
        return m;
    }
}
