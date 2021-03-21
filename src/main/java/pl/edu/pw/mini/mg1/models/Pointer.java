package pl.edu.pw.mini.mg1.models;

import com.jogamp.opengl.GL4;
import pl.edu.pw.mini.mg1.collisions.BoundingSphere;
import pl.edu.pw.mini.mg1.collisions.Ray;

public class Pointer extends Model {
    @Override
    protected void load(GL4 gl) {
        this.boundingVolume = new BoundingSphere(0.1f);
        this.mesh = new Mesh(new float[] {
                0, 0, 0,
                0.1f, 0, 0,
                0, 0, 0,
                0, 0.1f, 0,
                0, 0, 0,
                0, 0, 0.1f
        }, new float[] {
                1, 0, 0,
                1, 0, 0,
                0, 1, 0,
                0, 1, 0,
                0, 0.5f, 1,
                0, 0.5f, 1
        }, new int[] {
                0, 1, 2, 3, 4, 5
        }, GL4.GL_LINES);
        mesh.load(gl);
    }
}
