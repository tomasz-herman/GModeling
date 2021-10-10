package pl.edu.pw.mini.mg1.milling;

public class MillingTool {
    private final float radius;
    private final float length;
    private final boolean flat;

    public MillingTool(float radius, float length, boolean flat) {
        this.radius = radius;
        this.length = length;
        this.flat = flat;
    }

    public float getRadius() {
        return radius;
    }

    public boolean isFlat() {
        return flat;
    }

    public float getLength() {
        return length;
    }
}
