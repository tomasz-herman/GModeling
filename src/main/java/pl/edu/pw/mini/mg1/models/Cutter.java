package pl.edu.pw.mini.mg1.models;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL4;
import org.apache.commons.lang3.ArrayUtils;
import org.joml.Vector3f;
import pl.edu.pw.mini.mg1.cameras.PerspectiveCamera;
import pl.edu.pw.mini.mg1.graphics.Renderer;
import pl.edu.pw.mini.mg1.milling.MillingTool;

import java.util.ArrayList;

import static com.jogamp.opengl.math.FloatUtil.PI;
import static org.joml.Math.cos;
import static org.joml.Math.sin;

public class Cutter extends Model {

    private final MillingTool tool;

    public Cutter(MillingTool tool) {
        this.tool = tool;
    }

    @Override
    protected void load(GL4 gl) {
        float radius = tool.radius() / 100;
        float length = tool.length() / 100;
        short rings = 50;
        short sectors = 50;

        float sectorStep = (float) (2 * Math.PI / sectors);

        var unitCircleVertices = new ArrayList<Float>();
        for (int i = 0; i <= sectors; ++i) {
            var sectorAngle = i * sectorStep;
            unitCircleVertices.add(cos(sectorAngle));
            unitCircleVertices.add(sin(sectorAngle));
            unitCircleVertices.add(0.0f);
        }

        var vertices = new ArrayList<Float>();
        var normals = new ArrayList<Float>();
        var texCoords = new ArrayList<Float>();

        for (int i = 0; i < 2; ++i) {
            float h = i * length;
            if(!tool.flat()) {
                h += radius;
            }
            float t = 1.0f - i;

            for (int j = 0, k = 0; j <= sectors; ++j, k += 3) {
                float ux = unitCircleVertices.get(k);
                float uy = unitCircleVertices.get(k + 1);
                float uz = unitCircleVertices.get(k + 2);

                vertices.add(ux * radius);
                vertices.add(h);
                vertices.add(uy * radius);

                normals.add(ux);
                normals.add(uz);
                normals.add(uy);

                texCoords.add(1 - (float) j / sectors);
                texCoords.add(t);
            }
        }

        int baseCenterIndex = vertices.size() / 3;
        int topCenterIndex = baseCenterIndex + sectors + 1;

        for (int i = 0; i < 2; ++i) {
            float h = i * length;
            if(!tool.flat()) {
                h += radius;
            }
            float nz = -1 + i * 2;

            vertices.add(0.0f);
            vertices.add(h);
            vertices.add(0.0f);
            normals.add(0.0f);
            normals.add(nz);
            normals.add(0.0f);
            texCoords.add(0.5f);
            texCoords.add(0.5f);

            for (int j = 0, k = 0; j < sectors; ++j, k += 3) {
                float ux = unitCircleVertices.get(k);
                float uy = unitCircleVertices.get(k + 1);

                vertices.add(ux * radius);
                vertices.add(h);
                vertices.add(uy * radius);

                normals.add(0.0f);
                normals.add(nz);
                normals.add(0.0f);

                texCoords.add(-ux * 0.5f + 0.5f);
                texCoords.add(-uy * 0.5f + 0.5f);
            }
        }

        var indices = new ArrayList<Integer>();
        int k1 = 0;
        int k2 = sectors + 1;

        for (int i = 0; i < sectors; ++i, ++k1, ++k2) {
            indices.add(k1);
            indices.add(k1 + 1);
            indices.add(k2);

            indices.add(k2);
            indices.add(k1 + 1);
            indices.add(k2 + 1);
        }

        for (int i = 0, k = baseCenterIndex + 1; i < sectors; ++i, ++k) {
            if (i < sectors - 1) {
                indices.add(baseCenterIndex);
                indices.add(k + 1);
                indices.add(k);
            } else {
                indices.add(baseCenterIndex);
                indices.add(baseCenterIndex + 1);
                indices.add(k);
            }
        }

        for (int i = 0, k = topCenterIndex + 1; i < sectors; ++i, ++k) {
            if (i < sectors - 1) {
                indices.add(topCenterIndex);
                indices.add(k);
                indices.add(k + 1);
            } else {
                indices.add(topCenterIndex);
                indices.add(k);
                indices.add(topCenterIndex + 1);
            }
        }

        if (!tool.flat()) {
            float R = 1f / (rings - 1);
            float S = 1f / (sectors - 1);
            short r, s;
            float x, y, z;
            int offset = vertices.size() / 3;

            for (r = 0; r < rings; r++) {
                for (s = 0; s < sectors; s++) {
                    x = cos(2 * PI * s * S) * sin(PI * r * R);
                    y = sin(-PI / 2 + PI * r * R);
                    z = sin(2 * PI * s * S) * sin(PI * r * R);
                    vertices.add(x * radius);
                    vertices.add(y * radius + radius);
                    vertices.add(z * radius);
                    Vector3f normal = new Vector3f(x, y, z).normalize();
                    normals.add(normal.x);
                    normals.add(normal.y);
                    normals.add(normal.z);
                    texCoords.add(1 - s * S);
                    texCoords.add(1 - r * R);
                }
            }

            for (r = 0; r < rings - 1; r++) {
                for (s = 0; s < sectors - 1; s++) {
                    indices.add(offset + (r * sectors + s));
                    indices.add(offset + (r * sectors + (s + 1)));
                    indices.add(offset + ((r + 1) * sectors + (s + 1)));
                    indices.add(offset + ((r + 1) * sectors + (s + 1)));
                    indices.add(offset + (r * sectors + s));
                    indices.add(offset + ((r + 1) * sectors + s));
                }
            }
        }
        mesh = new Mesh(
                ArrayUtils.toPrimitive(vertices.toArray(new Float[0])),
                ArrayUtils.toPrimitive(normals.toArray(new Float[0])),
                ArrayUtils.toPrimitive(texCoords.toArray(new Float[0])),
                ArrayUtils.toPrimitive(indices.toArray(new Integer[0])),
                GL.GL_TRIANGLES);
        mesh.load(gl);
    }

    @Override
    public void render(GL4 gl, PerspectiveCamera camera, Renderer renderer) {
        renderer.renderPhong(gl, camera, this);
    }
}
