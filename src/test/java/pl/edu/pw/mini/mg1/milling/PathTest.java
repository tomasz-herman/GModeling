package pl.edu.pw.mini.mg1.milling;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.io.InputStream;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

class PathTest {
    private static Stream<Arguments> parameters() {
        return Stream.of(
                Arguments.of(stream("/p1/t1.k16")),
                Arguments.of(stream("/p1/t2.f12")),
                Arguments.of(stream("/p1/t3.f12")),
                Arguments.of(stream("/p1/t4.k16")),

                Arguments.of(stream("/p2/1.k16")),
                Arguments.of(stream("/p2/2.f12")),
                Arguments.of(stream("/p2/3.f10")),
                Arguments.of(stream("/p2/4.k08")),
                Arguments.of(stream("/p2/5.k01"))
                );
    }

    @ParameterizedTest
    @MethodSource("parameters")
    public void readFromFileTest(InputStream stream) {
        try {
            Path path = new Path(stream);
            assertThat(path.getCoords()).isNotEmpty();
        } catch (IOException e) {
            fail("Shouldn't happen", e);
        }
    }

    static InputStream stream(String path) {
        return PathTest.class.getResourceAsStream(path);
    }
}