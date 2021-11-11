package pl.edu.pw.mini.mg1.milling;

import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;
import pl.edu.pw.mini.mg1.models.Intersectable;
import pl.edu.pw.mini.mg1.models.MilledBlock;
import pl.edu.pw.mini.mg1.models.Scene;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.joml.Math.clamp;
import static org.joml.Math.max;

public class PathGenerator {
    public static Path generate1(Scene scene) {
        List<Vector3f> positions = new ArrayList<>();
        MaterialBlock block = new MaterialBlock(new Vector2f(150, 150), new Vector2i(1500, 1500), 50, 15);
        List<Intersectable> patches = scene.getModels().stream()
                .filter(m -> m instanceof Intersectable)
                .map(m -> (Intersectable)m)
                .collect(Collectors.toList());
        block.renderPatches(patches);
        scene.addModel(new MilledBlock(block));
        MillingTool.Cache cache = new MillingTool(8.01f, 25, false).new Cache(block);
        float maxHeight = block.findMaxHeight(0, 0, cache);
        positions.add(new Vector3f(0, 0, 80));
        positions.add(new Vector3f(-75, 75, 80));
        float H1 = 32.5f;
        float H2 = 15.0f;
        float x = -75f;
        float y = -75f;
        boolean yUp = true;
        while(x <= 75f) {
            while(y <= 75f && y >= -75f) {
                float h = block.findMaxHeight(x, y, cache);
                positions.add(new Vector3f(x, -y, max(h, H1 + 0.01f)));
                if(yUp) y += 1;
                else y -= 1;
            }
            y = clamp(-75, 75, y);
            yUp = !yUp;
            x += 5;
        }
        while(x >= -75f) {
            while(y <= 75f && y >= -75f) {
                float h = block.findMaxHeight(x, y, cache);
                positions.add(new Vector3f(x, -y, max(h, H2 + 0.01f)));
                if(yUp) y += 1;
                else y -= 1;
            }
            y = clamp(-75, 75, y);
            yUp = !yUp;
            x -= 5;
        }
        positions.add(new Vector3f(-75, -75, 80));
        positions.add(new Vector3f(0, 0, 80));
        return new Path(compressPaths(positions));
    }

    public static List<Vector3f> compressPaths(List<Vector3f> paths) {
        List<Vector3f> compressed = new ArrayList<>();
        compressed.add(paths.get(0));
        for (int i = 1; i < paths.size() - 1; i++) {
            Vector3f first = compressed.get(compressed.size() - 1);
            Vector3f middle = paths.get(i);
            Vector3f last = paths.get(i + 1);
            if(!middle.sub(first, new Vector3f()).normalize().equals(last.sub(middle, new Vector3f()).normalize(), 1e-5f)) {
                compressed.add(new Vector3f(middle));
            }
        }
        compressed.add(paths.get(paths.size() - 1));
        return compressed;
    }
}
