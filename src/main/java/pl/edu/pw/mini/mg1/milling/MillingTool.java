package pl.edu.pw.mini.mg1.milling;

import org.joml.Vector2i;

import java.util.Arrays;

import static org.joml.Math.sqrt;

public record MillingTool(float radius, float length, boolean flat) {
    public class Cache {
        private final Vector2i size;
        private final float[][] shape;

        public Cache(MaterialBlock block) {
            size = new Vector2i((int) (radius * block.getResolution().x() / block.getSize().x()), (int) (radius * block.getResolution().y() / block.getSize().y()));
            shape = new float[1 + size.x * 2][1 + size.y * 2];
            for (int i = 0; i < shape.length; i++) {
                for (int j = 0; j < shape[i].length; j++) {
                    float x = (i - size.x) * block.getSize().x() / block.getResolution().x();
                    float y = (j - size.y) * block.getSize().y() / block.getResolution().y();
                    if (x * x + y * y > radius * radius) {
                        shape[i][j] = Float.NaN;
                    } else {
                        if (flat) {
                            shape[i][j] = 0.0f;
                        } else {
                            shape[i][j] = radius - sqrt(radius * radius - x * x - y * y);
                        }
                    }
                }
            }
        }

        public Vector2i getSize() {
            return size;
        }

        public float[][] getShape() {
            return shape;
        }

        @Override
        public String toString() {
            StringBuilder string = new StringBuilder();
            for (int i = -size.x; i <= size.x; i++) {
                for (int j = -size.y; j <= size.y; j++) {
                    float toolShapeCorrection = shape[i + size.x][j + size.y];
                    string.append("%.2f ".formatted(toolShapeCorrection));
                }
                string.append("\n");
            }

            return string.toString();
        }
    }
}
