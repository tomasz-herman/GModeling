package pl.edu.pw.mini.mg1.models;

import com.jogamp.opengl.GL4;
import com.jogamp.opengl.util.GLBuffers;
import org.apache.commons.lang3.ArrayUtils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Torus {
    private int outerSegments;
    private int innerSegments;

    private float outerRadius;
    private float innerRadius;

    private float[] vertices;
    private int[] indices;

    private IntBuffer vao;
    private final List<IntBuffer> vbos = new ArrayList<>();

    public Torus() {
        this(10, 10, 1, 0.25f);
    }

    public Torus(int outerSegments, int innerSegments, float outerRadius, float innerRadius) {
        this.outerSegments = outerSegments;
        this.innerSegments = innerSegments;
        this.outerRadius = outerRadius;
        this.innerRadius = innerRadius;
        generateGeometry();
    }

    private void generateGeometry() {
        List<Float> vertices = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();

        for (int i = 0; i < outerSegments; i++) {
            float outerAngle = (float) (2 * Math.PI * i / (outerSegments));
            for (int j = 0; j < innerSegments; j++) {
                float innerAngle = (float) (Math.PI * 2 * j / (innerSegments));

                vertices.add(x(outerAngle, innerAngle));
                vertices.add(y(outerAngle, innerAngle));
                vertices.add(z(outerAngle, innerAngle));

                indices.add(i * innerSegments + j);
                indices.add(i * innerSegments + (j + 1) % innerSegments);

                indices.add(i * innerSegments + j);
                indices.add((i + 1) % innerSegments * innerSegments + j);
            }
        }

        this.vertices = ArrayUtils.toPrimitive(vertices.toArray(new Float[0]));
        this.indices = ArrayUtils.toPrimitive(indices.toArray(new Integer[0]));
    }

    private float x(float outerAngle, float innerAngle) {
        return (float) (Math.cos(outerAngle) * (outerRadius + innerRadius * Math.cos(innerAngle)));
    }

    private float y(float outerAngle, float innerAngle) {
        return (float) (Math.sin(outerAngle) * (outerRadius + innerRadius * Math.cos(innerAngle)));
    }

    private float z(float outerAngle, float innerAngle) {
        return (float) (innerRadius * Math.sin(innerAngle));
    }

    public void load(GL4 gl) {
        vao = GLBuffers.newDirectIntBuffer(1);
        gl.glGenVertexArrays(1, vao);
        gl.glBindVertexArray(vao.get(0));
        loadData(gl, vertices, 0, 3);
        loadIndices(gl, indices);
        gl.glBindVertexArray(0);
    }

    public void loadData(GL4 gl, float[] data, int index, int size) {
        IntBuffer vbo = GLBuffers.newDirectIntBuffer(1);
        gl.glGenBuffers(1, vbo);
        gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, vbo.get(0));
        gl.glBufferData(
                GL4.GL_ARRAY_BUFFER,
                (long) data.length * Float.BYTES,
                GLBuffers.newDirectFloatBuffer(data),
                GL4.GL_STATIC_DRAW);
        gl.glEnableVertexAttribArray(index);
        gl.glVertexAttribPointer(index, size, GL4.GL_FLOAT, false, 0, 0);
        gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, 0);
        vbos.add(vbo);
    }

    public void loadIndices(GL4 gl, int[] data) {
        IntBuffer vbo = GLBuffers.newDirectIntBuffer(1);
        gl.glGenBuffers(1, vbo);
        gl.glBindBuffer(GL4.GL_ELEMENT_ARRAY_BUFFER, vbo.get(0));
        gl.glBufferData(
                GL4.GL_ELEMENT_ARRAY_BUFFER,
                (long) data.length * Integer.BYTES,
                GLBuffers.newDirectIntBuffer(data),
                GL4.GL_STATIC_DRAW);
        gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, 0);
        vbos.add(vbo);
    }

    public void dispose(GL4 gl) {
        gl.glDeleteVertexArrays(1, vao);
        for (IntBuffer vbo : vbos) {
            gl.glDeleteBuffers(1, vbo);
        }
    }

    public int getVao() {
        return vao.get(0);
    }

    public int vertexCount() {
        return indices.length;
    }

    @Override
    public String toString() {
        return "Torus{" +
                "outerSegments=" + outerSegments +
                ", innerSegments=" + innerSegments +
                ", outerRadius=" + outerRadius +
                ", innerRadius=" + innerRadius +
                ", vertices=" + Arrays.toString(vertices) +
                ", indices=" + Arrays.toString(indices) +
                '}';
    }
}
