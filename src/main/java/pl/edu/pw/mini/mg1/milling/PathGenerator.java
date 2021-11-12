package pl.edu.pw.mini.mg1.milling;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;
import pl.edu.pw.mini.mg1.layout.IntersectionWizard;
import pl.edu.pw.mini.mg1.models.*;
import pl.edu.pw.mini.mg1.numerics.Intersection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.joml.Math.*;

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
        List<Vector3f> envelope = findBestIntersection(plane, surfaces.get(0));
        scene.addModel(new BezierC0(envelope.stream().map(Point::new).collect(Collectors.toList())));
        surfaces.remove(0);

        for (Intersectable surface : surfaces) {
            List<Vector3f> curve = findBestIntersection(plane, surface);
            scene.addModel(new BezierC0(envelope.stream().map(Point::new).collect(Collectors.toList())));
            envelope = merge(envelope, curve);
        }

        positions.add(new Vector3f(0, 0, 80));
        positions.add(new Vector3f(-85, -85, 80));
        positions.add(new Vector3f(-85, -85, 16));
        for (Vector3f pos : envelope) {
            positions.add(new Vector3f(pos.x, -pos.z, 16));
        }
        positions.add(new Vector3f(positions.get(positions.size() - 1).setComponent(2, 80)));
        positions.add(new Vector3f(0, 0, 80));
        return new Path(compressPaths(positions));
    }

    private static List<Vector3f> merge(List<Vector3f> envelope, List<Vector3f> curve) {
        List<Triple<Integer, Integer, Float>> distances = new ArrayList<>(curve.size());
        for (Vector3f vec1 : curve) {
            float best = Float.POSITIVE_INFINITY;
            int bestI = -1;
            for (int i = 0, envelopeSize = envelope.size(); i < envelopeSize; i++) {
                Vector3f vec2 = envelope.get(i);
                float dist = vec1.distance(vec2);
                if (dist < best) {
                    best = dist;
                    bestI = i;
                }
            }
            distances.add(Triple.of(distances.size(), bestI, best));
        }
        distances = distances.stream()
                .sorted((o1, o2) -> Float.compare(o1.getRight(), o2.getRight()))
                .limit(10)
                .filter(t -> t.getRight() < 0.2f)
                .collect(Collectors.toList());
        if(distances.size() < 2 || distances.get(0).getRight() > 0.2) return envelope;

        List<Triple<Integer, Integer, Float>> intersectionPoints = new ArrayList<>();
        intersectionPoints.add(distances.get(0));

        for (Triple<Integer, Integer, Float> distance : distances) {
            Vector3f vec1 = curve.get(distance.getLeft());
            boolean match = false;
            for (Triple<Integer, Integer, Float> intersectionPoint : intersectionPoints) {
                Vector3f vec2 = curve.get(intersectionPoint.getLeft());
                if(vec1.distance(vec2) > 0.4) {
                    match = true;
                    break;
                }
            }
            if(match) {
                intersectionPoints.add(distance);
                break;
            }
        }
        if(intersectionPoints.size() != 2) return envelope;

        List<Vector3f> merged = new ArrayList<>();

        int minE = min(intersectionPoints.get(0).getMiddle(), intersectionPoints.get(1).getMiddle());
        int maxE = max(intersectionPoints.get(0).getMiddle(), intersectionPoints.get(1).getMiddle());
        int minC = min(intersectionPoints.get(0).getLeft(), intersectionPoints.get(1).getLeft());
        int maxC = max(intersectionPoints.get(0).getLeft(), intersectionPoints.get(1).getLeft());

        if(maxE - minE < envelope.size() + minE - maxE) {
            for (int i = 0; i <= minE; i++) {
                merged.add(envelope.get(i));
            }

            if(minE == intersectionPoints.get(0).getMiddle()) {
                if(minC == intersectionPoints.get(0).getLeft()) {
                    for (int i = minC; i < maxC; i++) {
                        merged.add(curve.get(i));
                    }
                } else {
                    for (int i = maxC - 1; i >= minC; i--) {
                        merged.add(curve.get(i));
                    }
                }
            } else {
                if(minC == intersectionPoints.get(0).getLeft()) {
                    for (int i = maxC - 1; i >= minC; i--) {
                        merged.add(curve.get(i));
                    }
                } else {
                    for (int i = minC; i < maxC; i++) {
                        merged.add(curve.get(i));
                    }
                }
            }

            for (int i = maxE; i < envelope.size(); i++) {
                merged.add(envelope.get(i));
            }
        } else {
            System.out.println("oh noes");
        }
        return merged;
    }

    public static Path generate4(Scene scene) {
        List<Vector3f> positions = new ArrayList<>();
        positions.add(new Vector3f(0, 0, 80));
        List<Intersectable> surfaces = scene.getModels().stream()
                .filter(m -> m instanceof Intersectable)
                .filter(m -> !m.getName().equals("Plane"))
                .map(m -> (Intersectable)m)
                .map(m -> new OffsetSurface(m, -0.4f))
                .collect(Collectors.toList());

        return new Path(compressPaths(positions));
    }

    private static List<Vector3f> findBestIntersection(Intersectable plane, Intersectable surface) {
        float best = -1;
        List<Vector3f> bestCurve = List.of();
        for (int i = 0; i < 16; i++) {
            Intersection intersection = new Intersection(plane, surface);
            var curve = intersection.find(null, vec -> {}, 0.01f, i);
            var curvePos = curve.stream().map(c -> plane.P(c.x, c.y)).map(c -> c.mul(10).add(0, 15, 0)).collect(Collectors.toList());
            float heuristics = (float) curvePos.stream().mapToDouble(vec -> abs(vec.x) + abs(vec.z)).sum();
            if(heuristics > best) {
                bestCurve = curvePos;
                best = heuristics;
            }
        }
        return bestCurve;
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
