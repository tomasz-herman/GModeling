package pl.edu.pw.mini.mg1.models;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BezierPatchC0Test {
    @Test
    public void testGetPoint() {
        BezierPatchC0 c0 = BezierPatchC0.cylinder(1, 1, 3, 2);
        System.out.println(c0.T(0.0f, 0.5f));
        System.out.println(c0.T(0.1f, 0.5f));
        System.out.println(c0.T(0.2f, 0.5f));
        System.out.println(c0.T(0.3f, 0.5f));
        System.out.println(c0.T(0.4f, 0.5f));
        System.out.println(c0.T(0.5f, 0.5f));
        System.out.println(c0.T(0.6f, 0.5f));
        System.out.println(c0.T(0.7f, 0.5f));
        System.out.println(c0.T(0.8f, 0.5f));
        System.out.println(c0.T(0.9f, 0.5f));
        System.out.println(c0.T(1.0f, 0.5f));
        System.out.println(c0.T(0.5f, 0.0f));
        System.out.println(c0.T(0.5f, 0.1f));
        System.out.println(c0.T(0.5f, 0.2f));
        System.out.println(c0.T(0.5f, 0.3f));
        System.out.println(c0.T(0.5f, 0.4f));
        System.out.println(c0.T(0.5f, 0.5f));
        System.out.println(c0.T(0.5f, 0.6f));
        System.out.println(c0.T(0.5f, 0.7f));
        System.out.println(c0.T(0.5f, 0.8f));
        System.out.println(c0.T(0.5f, 0.9f));
        System.out.println(c0.T(0.5f, 1.0f));
    }
}