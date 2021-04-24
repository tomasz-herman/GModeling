package pl.edu.pw.mini.mg1.numerics;

import org.apache.commons.lang3.ArrayUtils;
import org.assertj.core.data.Offset;
import org.joml.Vector3f;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class SplineSmoothInterpolationTest {
    private static final Offset<Float> eps = Offset.offset(0.01f);

    private static Stream<Arguments> parameters() {
        return Stream.of(
                Arguments.of(
                        input(
                            new Vector3f(1.11f, 0.99f, 0.28f),
                            new Vector3f(-0.89f, 1.10f, 0.86f),
                            new Vector3f(-0.82f, 0.7f, 1.05f),
                            new Vector3f(0.99f, 0.63f, 0.51f)),
                        output(
                            new Vector3f(1.11f, 0.99f, 0.28f),
                            new Vector3f(0.14f, 1.32f, 0.41f),
                            new Vector3f(-0.82f, 1.66f, 0.54f),
                            new Vector3f(-0.89f, 1.10f, 0.86f),
                            new Vector3f(-0.89f, 1.10f, 0.86f),
                            new Vector3f(-0.90f, 0.98f, 0.93f),
                            new Vector3f(-0.88f, 0.82f, 1.00f),
                            new Vector3f(-0.82f, 0.7f, 1.05f),
                            new Vector3f(-0.82f, 0.7f, 1.05f),
                            new Vector3f(-0.58f, 0.19f, 1.24f),
                            new Vector3f(0.21f, 0.41f, 0.88f),
                            new Vector3f(0.99f, 0.63f, 0.51f)
                        )),
                Arguments.of(
                        input(
                                new Vector3f(1.11f, 0.99f, 0.28f),
                                new Vector3f(-0.89f, 1.10f, 0.86f),
                                new Vector3f(-0.82f, 0.7f, 1.05f)),
                        output(
                                new Vector3f(1.11f, 0.99f, 0.28f),
                                new Vector3f(0.12f, 1.30f, 0.43f),
                                new Vector3f(-0.86f, 1.60f, 0.58f),
                                new Vector3f(-0.89f, 1.10f, 0.86f),
                                new Vector3f(-0.89f, 1.10f, 0.86f),
                                new Vector3f(-0.90f, 1.00f, 0.92f),
                                new Vector3f(-0.86f, 0.85f, 0.98f),
                                new Vector3f(-0.82f, 0.7f, 1.05f)
                        )),
                Arguments.of(
                        input(
                                new Vector3f(1.11f, 0.99f, 0.28f),
                                new Vector3f(-0.89f, 1.10f, 0.86f)),
                        output(
                                new Vector3f(1.11f, 0.99f, 0.28f),
                                new Vector3f(0.44f, 1.03f, 0.47f),
                                new Vector3f(-0.22f, 1.06f, 0.67f),
                                new Vector3f(-0.89f, 1.10f, 0.86f)
                        )),
                Arguments.of(
                        input(
                                new Vector3f(1.11f, 0.99f, 0.28f)),
                        output()),
                Arguments.of(
                        input(),
                        output()
                )
        );
    }

    @ParameterizedTest
    @MethodSource("parameters")
    public void splineInterpolationTest(Vector3f[] coords, List<Vector3f> output) {
        SplineSmoothInterpolation solver = new SplineSmoothInterpolation(coords);
        float[] result = primitive(solver.solve());
        assertThat(result).containsExactly(primitive(output), eps);
    }

    private static Vector3f[] input(Vector3f... input) {
        return input;
    }

    private static List<Vector3f> output(Vector3f... output) {
        return Arrays.stream(output).collect(Collectors.toList());
    }

    private float[] primitive(List<Vector3f> list) {
        return ArrayUtils.toPrimitive(list.stream().flatMap(v -> Stream.of(v.x, v.y, v.z)).toArray(Float[]::new));
    }
}