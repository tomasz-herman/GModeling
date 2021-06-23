package pl.edu.pw.mini.mg1.models;

import org.joml.Vector3f;

public interface Intersectable {
    Vector3f P(float u, float v);
    Vector3f T(float u, float v);
    Vector3f B(float u, float v);
    Vector3f N(float u, float v);
    boolean wrapsU();
    boolean wrapsV();
}
