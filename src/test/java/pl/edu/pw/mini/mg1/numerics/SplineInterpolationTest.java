package pl.edu.pw.mini.mg1.numerics;

import org.assertj.core.data.Offset;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class SplineInterpolationTest {
    private static final Offset<Float> eps = Offset.offset(10 * Math.ulp(1.0f));

    private static Stream<Arguments> parameters() {
        return Stream.of(
                Arguments.of(coords(4, 2, 0, 1), output(4, 2.2f, -0.8f, 1)),
                Arguments.of(coords(-1, 0, 3), output(-1, -0.75f, 3)),
                Arguments.of(coords(1, 3), output(1, 3)),
                Arguments.of(coords(3), output(3))
        );
    }

    @ParameterizedTest
    @MethodSource("parameters")
    public void splineInterpolationTest(float[] coords, float[] output) {
        SplineInterpolation solver = new SplineInterpolation(coords);
        float[] result = solver.solve();
        assertThat(result).containsExactly(output, eps);
    }

    private static float[] coords(float... coords) {
        return coords;
    }

    private static float[] output(float... output) {
        return output;
    }
}