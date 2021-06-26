package pl.edu.pw.mini.mg1.models;

import org.joml.Vector3f;
import pl.edu.pw.mini.mg1.graphics.Texture;

public interface Intersectable {
    Vector3f P(float u, float v);
    Vector3f T(float u, float v);
    Vector3f B(float u, float v);
    Vector3f N(float u, float v);
    boolean wrapsU();
    boolean wrapsV();
    void setTexture(Texture texture);
    Texture getTexture();
    boolean isRightSide();
    boolean isLeftSide();
    void setRightSide(boolean value);
    void setLeftSide(boolean value);
}
