package pl.edu.pw.mini.mg1.models;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BezierPatchC0Test {
    @Test
    public void testGetPoint() {
        BezierPatchC0 c0 = BezierPatchC0.cylinder(1, 1, 3, 2);
        c0.P(0.8f, 0.8f);
        System.out.println();
        c0.P(0.2f, 0.8f);
        System.out.println();
        c0.P(0.3f, 0.2f);
        System.out.println(c0.N(0.5f, 0.5f));
    }
}