package pl.edu.pw.mini.mg1.models;

import com.jogamp.opengl.GL4;
import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.List;

public class Torus extends Model {
    private int outerSegments;
    private int innerSegments;

    private float outerRadius;
    private float innerRadius;

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
                indices.add((i + 1) % outerSegments * innerSegments + j);
            }
        }

        this.mesh = new Mesh(
                ArrayUtils.toPrimitive(vertices.toArray(new Float[0])),
                ArrayUtils.toPrimitive(indices.toArray(new Integer[0])),
                GL4.GL_LINES);
    }

    private float x(float outerAngle, float innerAngle) {
        return (float) (Math.cos(outerAngle) * (outerRadius + innerRadius * Math.cos(innerAngle)));
    }

    private float y(float outerAngle, float innerAngle) {
        return (float) (innerRadius * Math.sin(innerAngle));
    }

    private float z(float outerAngle, float innerAngle) {
        return (float) (Math.sin(outerAngle) * (outerRadius + innerRadius * Math.cos(innerAngle)));
    }

    public int getOuterSegments() {
        return outerSegments;
    }

    public void setOuterSegments(int outerSegments) {
        this.outerSegments = outerSegments;
        generateGeometry();
    }

    public int getInnerSegments() {
        return innerSegments;
    }

    public void setInnerSegments(int innerSegments) {
        this.innerSegments = innerSegments;
        generateGeometry();
    }

    public float getOuterRadius() {
        return outerRadius;
    }

    public void setOuterRadius(float outerRadius) {
        this.outerRadius = outerRadius;
        generateGeometry();
    }

    public float getInnerRadius() {
        return innerRadius;
    }

    public void setInnerRadius(float innerRadius) {
        this.innerRadius = innerRadius;
        generateGeometry();
    }
}
