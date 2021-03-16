package pl.edu.pw.mini.mg1.models;

import com.jogamp.opengl.GL4;

public class Point extends Model {
    @Override
    protected void load(GL4 gl) {
        this.mesh = new Mesh(
                new float[] {0, 0, 0},
                new int[] {0},
                GL4.GL_POINTS);
        mesh.load(gl);
    }
}
