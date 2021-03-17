package pl.edu.pw.mini.mg1.models;

import com.jogamp.opengl.GL4;
import pl.edu.pw.mini.mg1.collisions.BoundingSphere;

public class Point extends Model {
    @Override
    protected void load(GL4 gl) {
        this.boundingVolume = new BoundingSphere(0.05f);
        this.mesh = new Mesh(
                new float[] {0, 0, 0},
                new int[] {0},
                GL4.GL_POINTS);
        mesh.load(gl);
    }
}
