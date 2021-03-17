package pl.edu.pw.mini.mg1.collisions;

import org.joml.Vector3f;

public class BoundingSphere implements BoundingVolume {
    private final float radius;

    public BoundingSphere(float radius) {
        this.radius = radius;
    }

    @Override
    public float test(Ray ray) {
        Vector3f distance = new Vector3f(ray.getOrigin());

        float a = ray.getDirection().lengthSquared();
        float bHalf = distance.dot(ray.getDirection());
        float c = distance.lengthSquared() - radius * radius;
        float disc = bHalf * bHalf - a * c;

        if (disc < 0) return -1;

        float discSq = (float) Math.sqrt(disc);

        float root = (-bHalf - discSq) / a;
        if (root < 0) {
            root = (-bHalf + discSq) / a;
            if (root < 0) return -1;
        }

        return root;
    }
}
