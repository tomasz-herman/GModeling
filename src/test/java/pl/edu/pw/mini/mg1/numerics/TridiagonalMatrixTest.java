package pl.edu.pw.mini.mg1.numerics;

import org.assertj.core.data.Offset;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class TridiagonalMatrixTest {
    private static final Offset<Float> eps = Offset.offset(10 * Math.ulp(1.0f));

    private static Stream<Arguments> parameters() {
        return Stream.of(
                Arguments.of(
                        a(-1, -1, -1, -1, -1),
                        b(3, 3, 3, 3, 3, 3),
                        c(-1, -1, -1, -1, -1),
                        d(2, 1, 1, 1, 1, 2),
                        x(1, 1, 1, 1, 1, 1)),
                Arguments.of(
                        a(-3, -3, -3, -3, -3),
                        b(5, 5, 5, 5, 5, 5),
                        c(1, 1, 1, 1, 1),
                        d(6, 3, 3, 3, 3, 2),
                        x(1, 1, 1, 1, 1, 1))
        );
    }

    @ParameterizedTest
    @MethodSource("parameters")
    public void tridiagonalMatrixTest(float[] a, float[] b, float[] c, float[] d, float[] x) {
        TridiagonalMatrix solver = new TridiagonalMatrix(a, b, c, d);
        float[] result = solver.solve();
        assertThat(result).containsExactly(x, eps);
    }

    private static float[] a(float... coords) {
        return coords;
    }

    private static float[] b(float... coords) {
        return coords;
    }

    private static float[] c(float... coords) {
        return coords;
    }

    private static float[] d(float... coords) {
        return coords;
    }

    private static float[] x(float... coords) {
        return coords;
    }

}