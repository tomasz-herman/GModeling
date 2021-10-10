package pl.edu.pw.mini.mg1.milling;

import org.joml.Vector2f;
import org.joml.Vector2i;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThatCode;
import static pl.edu.pw.mini.mg1.milling.PathTest.stream;

class MaterialBlockTest {

    @Test
    public void millingTest() throws IOException {
        MaterialBlock block = new MaterialBlock(new Vector2f(100, 100), new Vector2i(1000, 1000), 50, 16);
        MillingTool tool = new MillingTool(16, 20, false);
        Path path = new Path(stream("/p2/1.k16"));
        assertThatCode(() -> block.mill(tool, path, progress -> System.out.printf("%.2f%%%n", progress * 100))).doesNotThrowAnyException();
    }

}