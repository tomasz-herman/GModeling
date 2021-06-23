package pl.edu.pw.mini.mg1.numerics;

import org.joml.Vector3f;
import org.joml.Vector4f;
import org.junit.jupiter.api.Test;

import java.util.function.BiFunction;

import static com.jogamp.opengl.math.FloatUtil.PI;
import static org.joml.Math.cos;
import static org.joml.Math.sin;

class NewtonTest {
    @Test
    public void test() {
        final BiFunction<Float, Float, Vector3f> P = (u, v) -> new Vector3f(cos(u * 2 * PI) * sin(v * 2 * PI), sin(u * 2 * PI) * sin(v * 2 * PI), cos(v * 2 * PI));
        final BiFunction<Float, Float, Vector3f> Q = (u, v) -> new Vector3f(cos(u * 2 * PI) * sin(v * 2 * PI) + 1, sin(u * 2 * PI) * sin(v * 2 * PI), cos(v * 2 * PI));
        final BiFunction<Float, Float, Vector3f> Pn = P.andThen(Vector3f::negate);
        final BiFunction<Float, Float, Vector3f> Qn = P.andThen(Vector3f::negate);
//        Vector4f x0 = new Vector4f(0.5f, 0.5f * 7/6, 0, 0.5f * 7/6);
        Vector4f x0 = new IntersectionStart(P, Q).solve(null);
//        Vector4f x0 = new IntersectionStart(P, Q).solve(P.apply(0.5f, 0.5f * 7/6));
        Vector4f x = new Vector4f(x0);
        final float d = 0.01f;
        int i = 1000;
        do {
            Newton newton = new Newton(P, Q, Pn, Qn, x, d, i);
            System.out.println(x + " " + P.apply(x.x, x.y) + " " + Q.apply(x.z, x.w));
            x = newton.solve();
        } while(x.sub(x0, new Vector4f()).lengthSquared() > 0.000001f);
    }
}