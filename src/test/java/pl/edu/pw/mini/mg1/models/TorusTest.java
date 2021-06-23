package pl.edu.pw.mini.mg1.models;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TorusTest {

    @Test
    public void normalVectorTest() {
        Torus t = new Torus(1, 1, 1, 0.25f);
        t.rotate(0, 0, 90);
        System.out.println(t.P(0.2f, 0.25f));
        System.out.println(t.N(0.2f, 0.25f));
    }

}