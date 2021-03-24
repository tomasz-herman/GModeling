package pl.edu.pw.mini.mg1.numerics;

public class DeCasteljau {
    private final float[] coordinates;
    private final float[] temp;
    private final int length;
    
    public DeCasteljau(float... coordinates) {
        this.length = coordinates.length;
        this.coordinates = new float[length];
        this.temp = new float[length];
        System.arraycopy(coordinates, 0, this.coordinates, 0, length);
    }
    
    public float solve(float t) {
        System.arraycopy(coordinates, 0, temp, 0, length);
        float tt = 1.0f - t;
        for (int i = 1; i < length; i++) {
            for (int j = 0; j < length - i; j++) {
                temp[j] = temp[j] * tt + temp[j + 1] * t;
            }
        }
        return temp[0];
    }
}
