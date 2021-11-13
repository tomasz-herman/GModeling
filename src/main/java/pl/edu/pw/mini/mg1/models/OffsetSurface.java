package pl.edu.pw.mini.mg1.models;

import org.joml.Vector3f;
import pl.edu.pw.mini.mg1.graphics.Texture;

public class OffsetSurface implements Intersectable {
    private final Intersectable intersectable;
    private final float offset;

    public OffsetSurface(Intersectable intersectable, float offset) {
        this.intersectable = intersectable;
        this.offset = offset;
    }

    @Override
    public Vector3f P(float u, float v) {
        return intersectable.P(u, v).add(intersectable.N(u, v).mul(offset));
    }

    @Override
    public Vector3f T(float u, float v) {
        return intersectable.T(u, v);
    }

    @Override
    public Vector3f B(float u, float v) {
        return intersectable.B(u, v);
    }

    @Override
    public Vector3f N(float u, float v) {
        return intersectable.N(u, v);
    }

    @Override
    public boolean wrapsU() {
        return intersectable.wrapsU();
    }

    @Override
    public boolean wrapsV() {
        return intersectable.wrapsV();
    }

    @Override
    public void setTexture(Texture texture) {

    }

    @Override
    public Texture getTexture() {
        return null;
    }

    @Override
    public boolean isRightSide() {
        return false;
    }

    @Override
    public boolean isLeftSide() {
        return false;
    }

    @Override
    public void setRightSide(boolean value) {

    }

    @Override
    public void setLeftSide(boolean value) {

    }

    public String toString() {
        return ((Model) intersectable).getName();
    }
}
