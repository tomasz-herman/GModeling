package pl.edu.pw.mini.mg1.numerics;

import org.joml.*;

import java.util.function.BiFunction;

public class Newton {
    private static final float h = 1e-3f;

    private final BiFunction<Float, Float, Vector3f> P;
    private final BiFunction<Float, Float, Vector3f> Q;
    private final BiFunction<Float, Float, Vector3f> Pn;
    private final BiFunction<Float, Float, Vector3f> Qn;
    private final Vector4f x0;
    private final float d;
    private int i;

    public Newton(BiFunction<Float, Float, Vector3f> p, BiFunction<Float, Float, Vector3f> q, BiFunction<Float, Float, Vector3f> pn, BiFunction<Float, Float, Vector3f> qn, Vector4f x0, float d, int i) {
        P = p;
        Q = q;
        Pn = pn;
        Qn = qn;
        this.x0 = x0;
        this.d = d;
        this.i = i;
    }

    public Vector4f solve() {
        Vector4f xn = new Vector4f(x0), xp;
        Vector3f tangent = Pn.apply(x0.x, x0.y).cross(Qn.apply(x0.z, x0.w)).normalize();
        QuadFunction<Float, Float, Float, Float, Vector4f> func = (u, v, s, t) -> {
            Vector3f PmQ = P.apply(u, v).sub(Q.apply(s, t));
            float val = P.apply(u, v).sub(P.apply(x0.x, x0.y)).dot(tangent) - d;
            return new Vector4f(PmQ, val);
        };
        QuadFunction<Float, Float, Float, Float, Matrix4f> J = (u, v, s, t) -> {
            Vector4f f = func.apply(u, v, s, t);
            return new Matrix4f(
                    func.apply(u + h, v, s, t).sub(f).div(h),
                    func.apply(u, v + h, s, t).sub(f).div(h),
                    func.apply(u, v, s + h, t).sub(f).div(h),
                    func.apply(u, v, s, t + h).sub(f).div(h)).invert();
        };
        do {
            xp = new Vector4f(xn);
            xn.sub(J.apply(xn.x, xn.y, xn.z, xn.w).transform(func.apply(xn.x, xn.y, xn.z, xn.w)));
        }
        while (xp.sub(xn, new Vector4f()).lengthSquared() > 1e-12 && i --> 0);
        return xn;
    }

    @FunctionalInterface
    public interface QuadFunction<U, V, S, T, R> {
        R apply(U u, V v, S s, T t);
    }
}
