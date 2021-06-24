package pl.edu.pw.mini.mg1.models;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BezierPatchC2Test {

    @Test
    public void bezierC2GetPointTest() {
        BezierPatchC2 c2 = BezierPatchC2.cylinder(1, 1, 10, 1);
//        System.out.println(c2.B(0.0f, 0.5f));
//        System.out.println(c2.B(0.1f, 0.5f));
//        System.out.println(c2.B(0.2f, 0.5f));
//        System.out.println(c2.B(0.3f, 0.5f));
//        System.out.println(c2.B(0.4f, 0.5f));
//        System.out.println(c2.B(0.5f, 0.5f));
//        System.out.println(c2.B(0.6f, 0.5f));
//        System.out.println(c2.B(0.7f, 0.5f));
//        System.out.println(c2.B(0.8f, 0.5f));
//        System.out.println(c2.B(0.9f, 0.5f));
//        System.out.println(c2.B(1.0f, 0.5f));
//        System.out.println();
//        System.out.println(c2.B(0.5f, 0.0f));
//        System.out.println(c2.B(0.5f, 0.1f));
//        System.out.println(c2.B(0.5f, 0.2f));
//        System.out.println(c2.B(0.5f, 0.3f));
//        System.out.println(c2.B(0.5f, 0.4f));
//        System.out.println(c2.B(0.5f, 0.5f));
//        System.out.println(c2.B(0.5f, 0.6f));
//        System.out.println(c2.B(0.5f, 0.7f));
//        System.out.println(c2.B(0.5f, 0.8f));
//        System.out.println(c2.B(0.5f, 0.9f));
//        System.out.println(c2.B(0.5f, 1.0f));
        System.out.println();
//        System.out.println(c2.P(0.5f, 0.0f));
//        System.out.println(c2.P(0.5f, 0.1f));
//        System.out.println(c2.P(0.5f, 0.2f));
//        System.out.println(c2.P(0.5f, 0.3f));
//        System.out.println(c2.P(0.5f, 0.4f));
//        System.out.println(c2.P(0.5f, 0.5f));
//        System.out.println(c2.P(0.5f, 0.6f));
//        System.out.println(c2.P(0.5f, 0.7f));
//        System.out.println(c2.P(0.5f, 0.8f));
//        System.out.println(c2.P(0.5f, 0.9f));
//        System.out.println(c2.P(0.5f, 1.0f));
        System.out.println(c2.B(0.0f, 0.5f));
        System.out.println(c2.B(0.1f, 0.5f));
        System.out.println(c2.B(0.2f, 0.5f));
        System.out.println(c2.B(0.3f, 0.5f));
        System.out.println(c2.B(0.4f, 0.5f));
        System.out.println(c2.B(0.5f, 0.5f));
        System.out.println(c2.B(0.6f, 0.5f));
        System.out.println(c2.B(0.7f, 0.5f));
        System.out.println(c2.B(0.8f, 0.5f));
        System.out.println(c2.B(0.9f, 0.5f));
        System.out.println(c2.B(1.0f, 0.5f));
    }
}