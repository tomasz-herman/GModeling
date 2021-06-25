package pl.edu.pw.mini.mg1.numerics;

import org.joml.Vector4f;
import org.junit.jupiter.api.Test;
import pl.edu.pw.mini.mg1.models.Torus;

import static org.junit.jupiter.api.Assertions.*;

class IntersectionStartTest {

    @Test
    public void torusTest() {
        Torus P = new Torus(1, 1, 1, 0.5f);
        Torus Q = new Torus(1, 1, 1, 0.5f);
        Q.move(3f, 0 ,0);
        IntersectionStart intersection = new IntersectionStart(P::P, Q::P, false);
        Vector4f point = intersection.solve(null);
        System.out.println(P.P(point.x, point.y));
        System.out.println(Q.P(point.z, point.w));
    }

    @Test
    public void torus2Test() {
        Torus P = new Torus(1, 1, 1, 0.5f);
        Torus Q = new Torus(1, 1, 1, 0.5f);
        Q.move(2.9f, 0 ,0);
        IntersectionStart intersection = new IntersectionStart(P::P, Q::P, false);
        Vector4f point = intersection.solve(null);
        System.out.println(P.P(point.x, point.y));
        System.out.println(Q.P(point.z, point.w));
    }

}