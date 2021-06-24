package pl.edu.pw.mini.mg1.models;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BezierPatchC2Test {

    @Test
    public void bezierC2GetPointTest() {
        BezierPatchC2 c2 = BezierPatchC2.flat(1, 1, 2, 3);
        System.out.println(c2.N(0, 0));
        System.out.println(c2.N(0.5f, 0.5f));
        System.out.println(c2.N(1, 1));
    }
}