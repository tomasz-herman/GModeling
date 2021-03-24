package pl.edu.pw.mini.mg1.numerics;

import org.assertj.core.data.Offset;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class DeCasteljauTest {

    private static final Offset<Float> eps = Offset.offset(10 * Math.ulp(1.0f));

    private static Stream<Arguments> parameters() {
        return Stream.of(
                Arguments.of(coords(4, 2, 0, 1), param(0.25f), value(163f / 64f)),
                Arguments.of(coords(4, 2, 0, 1), param(0), value(4)),
                Arguments.of(coords(4, 2, 0, 1), param(1), value(1)),
                Arguments.of(coords(4, -2, 1), param(1f / 3f), value(1)),
                Arguments.of(coords(4, -2, 1), param(2f / 9f), value(16f / 9f)),
                Arguments.of(coords(4, 0, -1, 1), param(1f / 3f), value(1)),
                Arguments.of(coords(4, 0, -1, 1), param(2f / 9f), value(16f / 9f)),
                Arguments.of(coords(1, 1, 0, 1), param(1f / 3f), value(7f / 9f)),
                Arguments.of(coords(1, 1, 0.5f, 0.25f, 1), param(1f / 3f), value(7f / 9f))
        );
    }

    @ParameterizedTest
    @MethodSource("parameters")
    public void deCasteljauTest(float[] coords, float t, float value) {
        DeCasteljau solver = new DeCasteljau(coords);
        float result = solver.solve(t);
        assertThat(result)
                .as("Bernstein polynomial %s for parameter %f should have value %f(%e)",
                        Arrays.toString(coords), t, value, eps.value, result)
                .isCloseTo(value, eps);
    }

    private static float[] coords(float... coords) {
        return coords;
    }

    private static float param(float param) {
        return param;
    }

    private static float value(float value) {
        return value;
    }
}