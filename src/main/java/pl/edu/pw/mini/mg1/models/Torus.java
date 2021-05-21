package pl.edu.pw.mini.mg1.models;

import com.jogamp.opengl.GL4;
import org.apache.commons.lang3.ArrayUtils;
import org.w3c.dom.Node;
import pl.edu.pw.mini.mg1.collisions.BoundingSphere;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Torus extends Model {
    private int outerSegments;
    private int innerSegments;

    private float outerRadius;
    private float innerRadius;

    public Torus() {
        this(10, 10, 1, 0.25f);
    }

    @Override
    protected void setupBoundingVolume() {
        this.boundingVolume = new BoundingSphere(outerRadius + innerRadius);
    }

    @Override
    protected void load(GL4 gl) {
        generateGeometry();
        mesh.load(gl);
    }

    public Torus(int outerSegments, int innerSegments, float outerRadius, float innerRadius) {
        this.outerSegments = outerSegments;
        this.innerSegments = innerSegments;
        this.outerRadius = outerRadius;
        this.innerRadius = innerRadius;
        setupBoundingVolume();
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
        reload = true;
    }

    public int getInnerSegments() {
        return innerSegments;
    }

    public void setInnerSegments(int innerSegments) {
        this.innerSegments = innerSegments;
        reload = true;
    }

    public float getOuterRadius() {
        return outerRadius;
    }

    public void setOuterRadius(float outerRadius) {
        this.outerRadius = outerRadius;
        setupBoundingVolume();
        reload = true;
    }

    public float getInnerRadius() {
        return innerRadius;
    }

    public void setInnerRadius(float innerRadius) {
        this.innerRadius = innerRadius;
        setupBoundingVolume();
        reload = true;
    }

    @Override
    public String serialize() {
        return
                """
                      <Torus Name="%s" MajorRadius="%f" MinorRadius="%f" VerticalSlices="%d" HorizontalSlices="%d">
                        <Position X="%f" Y="%f" Z="%f"/>
                        <Rotation X="%f" Y="%f" Z="%f"/>
                        <Scale X="%f" Y="%f" Z="%f"/>
                      </Torus>
                """.formatted(getName(),
                getOuterRadius(),
                getInnerRadius(),
                getOuterSegments(),
                getInnerSegments(),
                getPosition().x(),
                getPosition().y(),
                getPosition().z(),
                getRotation().x(),
                getRotation().y(),
                getRotation().z(),
                getScale().x(),
                getScale().y(),
                getScale().z());
    }

    @Override
    public Model deserialize(Node node, Map<String, Point> points) {
        return this;
    }
}
