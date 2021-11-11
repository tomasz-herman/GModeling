package pl.edu.pw.mini.mg1.milling;

import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;
import pl.edu.pw.mini.mg1.layout.IntersectionWizard;
import pl.edu.pw.mini.mg1.models.Intersectable;
import pl.edu.pw.mini.mg1.models.MilledBlock;
import pl.edu.pw.mini.mg1.models.OffsetSurface;
import pl.edu.pw.mini.mg1.models.Scene;
import pl.edu.pw.mini.mg1.numerics.Intersection;

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
        MillingTool.Cache cache = new MillingTool(10f, 25, false).new Cache(block);
        positions.add(new Vector3f(0, 0, 80));
        positions.add(new Vector3f(-75, 75, 80));
        float H1 = 33.0f;
        float H2 = 16.0f;
        float x = -75f;
        float y = -75f;
        boolean yUp = true;
        while(x <= 75f) {
            while(y <= 75f && y >= -75f) {
                float h = block.findMaxHeight(x, y, cache);
                positions.add(new Vector3f(x, -y, max(h, H1 + 1f)));
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
                positions.add(new Vector3f(x, -y, max(h, H2 + 1f)));
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

    public static Path generate2(Scene scene) {
        List<Vector3f> positions = new ArrayList<>();
        MaterialBlock block = new MaterialBlock(new Vector2f(150, 150), new Vector2i(1500, 1500), 50, 15);
        List<Intersectable> patches = scene.getModels().stream()
                .filter(m -> m instanceof Intersectable)
                .map(m -> (Intersectable)m)
                .collect(Collectors.toList());
        MillingTool.Cache cache = new MillingTool(6.01f, 25, true).new Cache(block);
        block.renderPatches(patches);
        scene.addModel(new MilledBlock(block));
        positions.add(new Vector3f(0, 0, 80));
        positions.add(new Vector3f(-85, 85, 80));
        float H = 16;
        float x = -85f;
        float y = -85f;
        while(x <= 85f) {
            while(y <= 85f && y >= -85f) {
                float h = block.findMaxHeight(x, y, cache);
                if(h > H) break;
                positions.add(new Vector3f(x, -y, H));
                y += 1;
            }
            positions.add(new Vector3f(x, -y - 1, 80));
            positions.add(new Vector3f(x, 85, 80));
            positions.add(new Vector3f(x, 85, H));

            y = -85;
            x += 5;
        }
        positions.add(new Vector3f(85, 85, 80));
        positions.add(new Vector3f(0, 0, 80));

        positions.add(new Vector3f(-85, -85, 80));
        x = -85f;
        y = 85f;
        while(x <= 85f) {
            while(y <= 85f && y >= -85f) {
                float h = block.findMaxHeight(x, y, cache);
                if(h > H) break;
                positions.add(new Vector3f(x, -y, H));
                y -= 1;
            }
            positions.add(new Vector3f(x, -y + 1, 80));
            positions.add(new Vector3f(x, -85, 80));
            positions.add(new Vector3f(x, -85, H));

            y = 85;
            x += 5;
        }
        positions.add(new Vector3f(85, -85, 80));
        positions.add(new Vector3f(0, 0, 80));

        positions.add(new Vector3f(-85, 85, 80));
        x = -85f;
        y = -85f;
        while(y <= 85f) {
            while(x <= 85f && x >= -85f) {
                float h = block.findMaxHeight(x, y, cache);
                if(h > H) break;
                positions.add(new Vector3f(x, -y, H));
                x += 1;
            }
            positions.add(new Vector3f(x - 1, -y, 80));
            positions.add(new Vector3f(-85, -y, 80));
            positions.add(new Vector3f(-85, -y, H));

            x = -85;
            y += 5;
        }
        positions.add(new Vector3f(-85, -85, 80));
        positions.add(new Vector3f(0, 0, 80));

        positions.add(new Vector3f(-85, -85, 80));
        x = 85f;
        y = -85f;
        while(y <= 85f) {
            while(x <= 85f && x >= -85f) {
                float h = block.findMaxHeight(x, y, cache);
                if(h > H) break;
                positions.add(new Vector3f(x, -y, H));
                x -= 1;
            }
            positions.add(new Vector3f(x + 1, -y, 80));
            positions.add(new Vector3f(85, -y, 80));
            positions.add(new Vector3f(85, -y, H));

            x = 85;
            y += 5;
        }
        positions.add(new Vector3f(-85, 85, 80));
        positions.add(new Vector3f(0, 0, 80));

        return new Path(compressPaths(positions));
    }

    public static Path generate3(Scene scene) {
        List<Vector3f> positions = new ArrayList<>();
        positions.add(new Vector3f(0, 0, 80));
        Intersectable plane = scene.getModels().stream()
                .filter(m -> m instanceof Intersectable)
                .filter(m -> m.getName().equals("Plane"))
                .map(m -> (Intersectable)m)
                .map(m -> new OffsetSurface(m, 0.1f))
                .findFirst().orElse(null);
        if(plane == null) return new Path(positions);
        List<Intersectable> surfaces = scene.getModels().stream()
                .filter(m -> m instanceof Intersectable)
                .filter(m -> !m.getName().equals("Plane"))
                .map(m -> (Intersectable)m)
                .map(m -> new OffsetSurface(m, -0.5f))
                .collect(Collectors.toList());
        Intersection intersection = new Intersection(plane, surfaces.get(0));
        var curve = intersection.find(null, scene::setPointerWorldCoords, 0.01f);
        var curvePos = curve.stream().map(c -> plane.P(c.x, c.y)).map(c -> c.mul(10).add(0, 15, 0)).collect(Collectors.toList());
        positions.add(new Vector3f(0, 0, 80));
        positions.add(new Vector3f(-85, -85, 80));
        positions.add(new Vector3f(-85, -85, 16));
        for (Vector3f pos : curvePos) {
            positions.add(new Vector3f(pos.x, -pos.z, 16));
        }
        positions.forEach(cord -> System.out.println(cord));
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
