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
        final BiFunction<Float, Float, Vector3f> P = (u, v) -> new Vector3f(cos(u) * sin(v), sin(u) * sin(v), cos(v));
        final BiFunction<Float, Float, Vector3f> Q = (u, v) -> new Vector3f(cos(u) * sin(v) + 1, sin(u) * sin(v), cos(v));
        final BiFunction<Float, Float, Vector3f> Pn = P.andThen(Vector3f::negate);
        final BiFunction<Float, Float, Vector3f> Qn = P.andThen(Vector3f::negate);
        Vector4f x0 = new Vector4f(PI, PI * 7/6, 0, PI * 7/6);
        Vector4f x = new Vector4f(x0);
        final float d = 0.01f;
        int i = 1000;
        do {
            Newton newton = new Newton(P, Q, Pn, Qn, x, d, i);
            x = newton.solve();
            System.out.println(x);
        } while(x.sub(x0, new Vector4f()).lengthSquared() > 0.0001f);
    }
}