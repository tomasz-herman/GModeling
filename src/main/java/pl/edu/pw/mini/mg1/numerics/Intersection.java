package pl.edu.pw.mini.mg1.numerics;

import org.joml.Vector3f;
import org.joml.Vector4f;
import pl.edu.pw.mini.mg1.models.Intersectable;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class Intersection {

    private final Intersectable P;
    private final Intersectable Q;

    public Intersection(Intersectable P, Intersectable Q) {
        this.P = P;
        this.Q = Q;
    }

    public List<Vector4f> find(Vector3f closeTo, Consumer<Vector3f> markStart, float step) {
        IntersectionStart start = new IntersectionStart(P::P, Q::P, P == Q);
        Vector4f s = start.solve(closeTo);
        LinkedList<Vector4f> parameters = new LinkedList<>();
        if (s == null) return parameters;
        boolean pWrapsU = P.wrapsU();
        boolean pWrapsV = P.wrapsV();
        boolean qWrapsU = Q.wrapsU();
        boolean qWrapsV = Q.wrapsV();
        Vector3f found = P.P(s.x, s.y);
        markStart.accept(found);
        Vector4f next = new Vector4f(s);
        Function<Float, Float> wrap = val -> val < 0 ? val + 1 : val > 1 ? val - 1 : val;
        parameters.addLast(s);
        int i = 10000;
        while (i-- > 0) {
            Newton newton = new Newton(P::P, Q::P, P::N, Q::N, next, step, 100);
            next = newton.solve();
            if (!next.isFinite()) return parameters;
            parameters.addLast(next);
            if (!pWrapsU && (next.x > 1 || next.x < 0)) break;
            else next.x = wrap.apply(next.x);
            if (!pWrapsV && (next.y > 1 || next.y < 0)) break;
            else next.y = wrap.apply(next.y);
            if (!qWrapsU && (next.z > 1 || next.z < 0)) break;
            else next.z = wrap.apply(next.z);
            if (!qWrapsV && (next.w > 1 || next.w < 0)) break;
            else next.w = wrap.apply(next.w);
            if (found.distance(P.P(next.x, next.y)) < step && i < 9995) {
                parameters.addLast(s);
                return parameters;
            }
        }
        i = 10000;
        next = new Vector4f(s);
        while (i-- > 0) {
            Newton newton = new Newton(P::P, Q::P, P::N, Q::N, next, -step, 100);
            next = newton.solve();
            if (!next.isFinite()) return parameters;
            parameters.addFirst(next);
            if (!pWrapsU && (next.x > 1 || next.x < 0)) break;
            else next.x = wrap.apply(next.x);
            if (!pWrapsV && (next.y > 1 || next.y < 0)) break;
            else next.y = wrap.apply(next.y);
            if (!qWrapsU && (next.z > 1 || next.z < 0)) break;
            else next.z = wrap.apply(next.z);
            if (!qWrapsV && (next.w > 1 || next.w < 0)) break;
            else next.w = wrap.apply(next.w);
        }
        return parameters;
    }
}
