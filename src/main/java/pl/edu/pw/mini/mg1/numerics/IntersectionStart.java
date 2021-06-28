package pl.edu.pw.mini.mg1.numerics;

import org.apache.commons.lang3.tuple.Pair;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;

import static org.joml.Math.*;

public class IntersectionStart {
    private final Function<Vector2f, Vector3f> P;
    private final Function<Vector2f, Vector3f> Q;
    private final boolean self;
    Random random = new Random();

    public IntersectionStart(Function<Vector2f, Vector3f> p, Function<Vector2f, Vector3f> q, boolean self) {
        P = p;
        Q = q;
        this.self = self;
    }

    public IntersectionStart(BiFunction<Float, Float, Vector3f> p, BiFunction<Float, Float, Vector3f> q, boolean self) {
        P = vec -> p.apply(vec.x, vec.y);
        Q = vec -> q.apply(vec.x, vec.y);
        this.self = self;
    }

    private static double applyAsDouble(Pair<Pair<Vector2f, Vector3f>, Float> value) {
        return (double) value.getRight();
    }

    public Vector4f solve(Vector3f closeTo) {
        Pair<Vector2f, Vector3f> p = self ? Pair.of(new Vector2f(0.75f), P.apply(new Vector2f(0.75f))) : Pair.of(new Vector2f(0.5f), P.apply(new Vector2f(0.5f)));
        Pair<Vector2f, Vector3f> q = self ? Pair.of(new Vector2f(0.25f), P.apply(new Vector2f(0.25f))) : Pair.of(new Vector2f(0.5f), Q.apply(new Vector2f(0.5f)));
        float R = 1f;
        if(closeTo != null) {
            List<Pair<Pair<Vector2f, Vector3f>, Float>> pTemp = new ArrayList<>();
            List<Pair<Pair<Vector2f, Vector3f>, Float>> qTemp = new ArrayList<>();
            for (int j = 0; j < 65536; j++) {
                Vector2f puv = uv(new Vector2f(rand(1), rand(1)).add(p.getLeft()));
                Vector2f quv = uv(new Vector2f(rand(1), rand(1)).add(q.getLeft()));
                Vector3f ppp = P.apply(puv);
                Vector3f qqq = Q.apply(quv);
                pTemp.add(Pair.of(Pair.of(puv, ppp), ppp.distanceSquared(closeTo)));
                qTemp.add(Pair.of(Pair.of(quv, qqq), qqq.distanceSquared(closeTo)));
            }
            List<Pair<Vector2f, Vector3f>> ppTemp = pTemp.stream()
                    .sorted(Comparator.comparingDouble(IntersectionStart::applyAsDouble))
                    .limit(4096)
                    .map(Pair::getLeft)
                    .toList();
            List<Pair<Vector2f, Vector3f>> qqTemp = qTemp.stream()
                    .sorted(Comparator.comparingDouble(IntersectionStart::applyAsDouble))
                    .limit(4096)
                    .map(Pair::getLeft)
                    .toList();
            float bestDist = p.getRight().distanceSquared(q.getRight());
            for (Pair<Vector2f, Vector3f> qq : qqTemp) {
                for (Pair<Vector2f, Vector3f> pp : ppTemp) {
                    if(self && pp.getLeft().distanceSquared(qq.getLeft()) < 0.3) continue;
                    float nextDist = qq.getRight().distanceSquared(pp.getRight());
                    if (nextDist < bestDist) {
                        bestDist = nextDist;
                        q = qq;
                        p = pp;
                    }
                }
            }
            R = 0.1f;
        }
        for (int i = 0; i < 100; i++) {
            List<Pair<Vector2f, Vector3f>> pTemp = new ArrayList<>();
            List<Pair<Vector2f, Vector3f>> qTemp = new ArrayList<>();
            for (int j = 0; j < 1024; j++) {
                Vector2f puv = uv(new Vector2f(rand(R), rand(R)).add(p.getLeft()));
                Vector2f quv = uv(new Vector2f(rand(R), rand(R)).add(q.getLeft()));
                pTemp.add(Pair.of(puv, P.apply(puv)));
                qTemp.add(Pair.of(quv, Q.apply(quv)));
            }
            float lastDist = p.getRight().distanceSquared(q.getRight());
            float bestDist = lastDist;
            for (Pair<Vector2f, Vector3f> qq : qTemp) {
                for (Pair<Vector2f, Vector3f> pp : pTemp) {
                    if(self && pp.getLeft().distanceSquared(qq.getLeft()) < 0.3) continue;
                    float nextDist = qq.getRight().distanceSquared(pp.getRight());
                    if (nextDist < bestDist) {
                        bestDist = nextDist;
                        q = qq;
                        p = pp;
                    }
                }
            }
            if(lastDist > bestDist) R = R * sqrt(bestDist / lastDist) * 1.5f;
            else if(closeTo != null) R = R * 0.1f;
            if(bestDist < 1e-12f) {
                if(self && p.getLeft().distanceSquared(q.getLeft()) < 1e-3f) return null;
                return new Vector4f(p.getLeft(), q.getLeft().x, q.getLeft().y);
            }
        }
        return null;
    }

    private float rand(float r) {
        return r * (random.nextFloat() - 0.5f);
    }

    private Vector2f uv(Vector2f uv) {
        uv.x = clamp(0, 1, uv.x);
        uv.y = clamp(0, 1, uv.y);
        return uv;
    }
}
