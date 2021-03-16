package pl.edu.pw.mini.mg1.collisions;

import org.joml.Vector3f;

public class Ray {
    private final Vector3f origin;
    private final Vector3f direction;

    public Ray(Vector3f origin, Vector3f direction) {
        this.origin = origin;
        this.direction = direction;
    }

    public Vector3f at(float t) {
        return origin.fma(t, direction, new Vector3f());
    }

    @Override
    public String toString() {
        return "Ray{origin=%s, direction=%s}".formatted(origin, direction);
    }
}
