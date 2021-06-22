package pl.edu.pw.mini.mg1.models;

import com.jogamp.opengl.GL4;
import org.apache.commons.lang3.ArrayUtils;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import pl.edu.pw.mini.mg1.cameras.PerspectiveCamera;
import pl.edu.pw.mini.mg1.collisions.BoundingSphere;
import pl.edu.pw.mini.mg1.graphics.Renderer;
import pl.edu.pw.mini.mg1.graphics.Texture;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static org.joml.Math.toDegrees;
import static org.joml.Math.toRadians;

public class Torus extends Model {
    private int outerSegments;
    private int innerSegments;

    private float outerRadius;
    private float innerRadius;

    private Texture texture;

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
        texture = new Texture(gl, 1024, (u, v) -> u > v ? new Vector3f(0, 0, 0) : new Vector3f(1, 1, 1));
    }

    public Torus(int outerSegments, int innerSegments, float outerRadius, float innerRadius) {
        this.outerSegments = outerSegments;
        this.innerSegments = innerSegments;
        this.outerRadius = outerRadius;
        this.innerRadius = innerRadius;
        setupBoundingVolume();
    }

    private void generateGeometry() {
        List<Float> vertices = List.of(0f, 0f, 0f);
        List<Integer> indices = List.of(0);

        this.mesh = new Mesh(
                ArrayUtils.toPrimitive(vertices.toArray(new Float[0])),
                ArrayUtils.toPrimitive(indices.toArray(new Integer[0])),
                GL4.GL_PATCHES);
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
    public void render(GL4 gl, PerspectiveCamera camera, Renderer renderer) {
        renderer.renderTorus(gl, camera, this);
    }

    @Override
    public void dispose(GL4 gl) {
        super.dispose(gl);
        if(texture != null) texture.dispose(gl);
    }

    public Texture getTexture() {
        return texture;
    }

    @Override
    public String serialize() {
        Quaternionf rotation = new Quaternionf().rotateZYX(
                toRadians(getRotation().z()),
                toRadians(getRotation().y()),
                toRadians(getRotation().x()));
        return
                """
                      <Torus Name="%s" MajorRadius="%f" MinorRadius="%f" MajorSegments="%d" MinorSegments="%d">
                        <Position X="%f" Y="%f" Z="%f"/>
                        <Rotation X="%f" Y="%f" Z="%f" W="%f"/>
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
                        rotation.x,
                        rotation.y,
                        rotation.z,
                        rotation.w,
                getScale().x(),
                getScale().y(),
                getScale().z());
    }

    /**
     *   <Torus Name="Torus_001" MajorRadius="10.10" MinorRadius="9.9" MajorSegments="5" MinorSegments="8">
     *     <Position X="10.123" Y="13.37" Z="123.456"/>
     *     <Rotation X="-0.235337" Y="0.748570" Z="0.010133" W="0.619803"/>
     *     <Scale X="1" Y="1" Z="1"/>
     *   </Torus>
     */
    @Override
    public Model deserialize(Node node, Map<String, Point> points) {
        if(node.getNodeType() == Node.ELEMENT_NODE) {
            Element torusElement = (Element) node;
            setName(torusElement.getAttribute("Name"));
            setInnerRadius(Float.parseFloat(torusElement.getAttribute("MinorRadius")));
            setOuterRadius(Float.parseFloat(torusElement.getAttribute("MajorRadius")));
            setInnerSegments(Integer.parseInt(torusElement.getAttribute("MinorSegments")));
            setOuterSegments(Integer.parseInt(torusElement.getAttribute("MajorSegments")));

            Element positionElement = (Element) torusElement
                    .getElementsByTagName("Position").item(0);
            float x = Float.parseFloat(positionElement.getAttribute("X"));
            float y = Float.parseFloat(positionElement.getAttribute("Y"));
            float z = Float.parseFloat(positionElement.getAttribute("Z"));
            float w;
            setPosition(x, y, z);

            Element scaleElement = (Element) torusElement
                    .getElementsByTagName("Scale").item(0);
            x = Float.parseFloat(scaleElement.getAttribute("X"));
            y = Float.parseFloat(scaleElement.getAttribute("Y"));
            z = Float.parseFloat(scaleElement.getAttribute("Z"));
            setScale(x, y, z);

            Element rotationElement = (Element) torusElement
                    .getElementsByTagName("Rotation").item(0);
            x = Float.parseFloat(rotationElement.getAttribute("X"));
            y = Float.parseFloat(rotationElement.getAttribute("Y"));
            z = Float.parseFloat(rotationElement.getAttribute("Z"));
            w = Float.parseFloat(rotationElement.getAttribute("W"));
            Quaternionf rotation = new Quaternionf(x, y, z, w);
            Vector3f angles = new Matrix4f().rotation(rotation).getEulerAnglesZYX(new Vector3f());
            Function<Float, Float> normalize = a -> {
                a = (float)toDegrees(a);
                if(a < 0) a += 360;
                if(a >= 360) a -= 360;
                return a;
            };
            setRotation(normalize.apply(angles.x), normalize.apply(angles.y), normalize.apply(angles.z));
        }
        return this;
    }
}
