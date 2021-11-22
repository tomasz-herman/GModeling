package pl.edu.pw.mini.mg1.milling;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;
import pl.edu.pw.mini.mg1.layout.IntersectionWizard;
import pl.edu.pw.mini.mg1.models.*;
import pl.edu.pw.mini.mg1.numerics.Intersection;
import pl.edu.pw.mini.mg1.numerics.IntersectionStart;

import java.util.*;
import java.util.function.Consumer;
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
        float x = -75f;
        float y = -85f;
        while(x <= 75f) {
            while(y <= 85f && y >= -85f) {
                float h = block.findMaxHeight(x, y, cache);
                if(h > H) break;
                positions.add(new Vector3f(x, -y, H));
                y += 1;
            }
            positions.add(new Vector3f(x, 85, H));

            y = -85;
            x += 10;
        }
        positions.add(new Vector3f(85, 85, 80));
        positions.add(new Vector3f(0, 0, 80));

        positions.add(new Vector3f(-55, -85, 80));
        x = -55f;
        y = 85f;
        while(x <= 35f) {
            while(y <= 85f && y >= -85f) {
                float h = block.findMaxHeight(x, y, cache);
                if(h > H) break;
                positions.add(new Vector3f(x, -y, H));
                y -= 1;
            }
            positions.add(new Vector3f(x, -85, H));

            y = 85;
            x += 10;
        }
        positions.add(new Vector3f(35, -85, 80));
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
            System.err.println("anyway");
        }
        return merged;
    }

    public static Path generate4(Scene scene) {
        List<Vector3f> positions = new ArrayList<>();
        positions.add(new Vector3f(0, 0, 80));
        Intersectable plane = scene.getModels().stream()
                .filter(m -> m instanceof Intersectable)
                .filter(m -> m.getName().equals("Plane"))
                .map(m -> (Intersectable)m)
                .map(m -> new OffsetSurface(m, 0.1f))
                .findFirst().orElseThrow();

        Intersectable body = scene.getModels().stream()
                .filter(m -> m instanceof Intersectable)
                .filter(m -> m.getName().equals("BezierPatchC2 2286"))
                .map(m -> (Intersectable)m)
                .map(m -> new OffsetSurface(m, -0.4f))
                .findFirst().orElseThrow();

        Intersectable top = scene.getModels().stream()
                .filter(m -> m instanceof Intersectable)
                .filter(m -> m.getName().equals("BezierPatchC2 118136"))
                .map(m -> (Intersectable)m)
                .map(m -> new OffsetSurface(m, -0.4f))
                .findFirst().orElseThrow();

        Intersectable topWing = scene.getModels().stream()
                .filter(m -> m instanceof Intersectable)
                .filter(m -> m.getName().equals("BezierPatchC2 8295"))
                .map(m -> (Intersectable)m)
                .map(m -> new OffsetSurface(m, -0.4f))
                .findFirst().orElseThrow();

        Intersectable topHorizontalWing = scene.getModels().stream()
                .filter(m -> m instanceof Intersectable)
                .filter(m -> m.getName().equals("BezierPatchC2 13327a"))
                .map(m -> (Intersectable)m)
                .map(m -> new OffsetSurface(m, -0.4f))
                .findFirst().orElseThrow();

        Intersectable decor = scene.getModels().stream()
                .filter(m -> m instanceof Intersectable)
                .filter(m -> m.getName().equals("BezierPatchC2 17903"))
                .map(m -> (Intersectable)m)
                .map(m -> new OffsetSurface(m, -0.4f))
                .findFirst().orElseThrow();

        Intersectable wing = scene.getModels().stream()
                .filter(m -> m instanceof Intersectable)
                .filter(m -> m.getName().equals("BezierPatchC2 31973"))
                .map(m -> (Intersectable)m)
                .map(m -> new OffsetSurface(m, -0.4f))
                .findFirst().orElseThrow();

        var bodyPaths = generateBodyPaths(body, wing, top, topWing, plane);
        var wingPaths = generateWingPaths(wing, body);
        var topPaths = generateTopPaths(top, decor, topWing, body);
        var topWingPaths = generateTopWingPaths(topWing, top, body, decor, topHorizontalWing);
        var topHorizontalWingPaths = generateTopHorizontalWing(topHorizontalWing, topWing, wing);
        var decorPaths = generateDecorPaths(decor, topWing, top);
        var holePaths = generateHole(decor, top, topWing);

        positions.addAll(bodyPaths.getKey().stream().map(v -> new Vector3f(v.x, -v.z, v.y)).collect(Collectors.toList()));
        positions.addAll(wingPaths.getKey().stream().map(v -> new Vector3f(v.x, -v.z, v.y)).collect(Collectors.toList()));
        positions.addAll(topPaths.getKey().stream().map(v -> new Vector3f(v.x, -v.z, v.y)).collect(Collectors.toList()));
        positions.addAll(topWingPaths.getKey().stream().map(v -> new Vector3f(v.x, -v.z, v.y)).collect(Collectors.toList()));
        positions.addAll(topHorizontalWingPaths.getKey().stream().map(v -> new Vector3f(v.x, -v.z, v.y)).collect(Collectors.toList()));
        positions.addAll(decorPaths.getKey().stream().map(v -> new Vector3f(v.x, -v.z, v.y)).collect(Collectors.toList()));

        positions.addAll(holePaths.getKey().stream().map(v -> new Vector3f(v.x, -v.z, v.y)).collect(Collectors.toList()));

        positions.addAll(bodyPaths.getValue().stream().map(v -> new Vector3f(v.x, -v.z, v.y)).collect(Collectors.toList()));
        positions.addAll(wingPaths.getValue().stream().map(v -> new Vector3f(v.x, -v.z, v.y)).collect(Collectors.toList()));
        positions.addAll(topPaths.getValue().stream().map(v -> new Vector3f(v.x, -v.z, v.y)).collect(Collectors.toList()));
        positions.addAll(topWingPaths.getValue().stream().map(v -> new Vector3f(v.x, -v.z, v.y)).collect(Collectors.toList()));
        positions.addAll(topHorizontalWingPaths.getValue().stream().map(v -> new Vector3f(v.x, -v.z, v.y)).collect(Collectors.toList()));
        positions.addAll(decorPaths.getValue().stream().map(v -> new Vector3f(v.x, -v.z, v.y)).collect(Collectors.toList()));

        positions.addAll(holePaths.getValue().stream().map(v -> new Vector3f(v.x, -v.z, v.y)).collect(Collectors.toList()));

        positions.add(new Vector3f(0, 0, 80));
        return new Path(compressPaths(positions));
    }

    private static Pair<List<Vector3f>, List<Vector3f>> generateHole(Intersectable decor, Intersectable top, Intersectable topWing) {
        List<Vector3f> result = new ArrayList<>();
        List<Vector3f> envelope = new ArrayList<>();

        List<Vector2f> decorTop;
        List<Vector2f> topTopWing;
        List<Vector2f> topWingDecor;

        Intersection finder = new Intersection(decor, top);
        decorTop = finder.find(null, vec -> {}, -0.01f, 543).stream().map(v -> new Vector2f(v.x, v.y)).collect(Collectors.toList());

        finder = new Intersection(top, topWing);
        topTopWing = finder.find(null, vec -> {}, -0.01f, 543).stream().map(v -> new Vector2f(v.x, v.y)).collect(Collectors.toList());

        finder = new Intersection(topWing, decor);
        topWingDecor = finder.find(null, vec -> {}, -0.01f, 543).stream().map(v -> new Vector2f(v.x, v.y)).collect(Collectors.toList());

        List<Vector3f> sceneCurve = decorTop.stream()
                .map(v -> decor.P(v.x, v.y).mul(10).add(0, 15 - 4, 0))
                .collect(Collectors.toList());
        for (int i = 0; i < sceneCurve.size(); i++) {
            Vector3f vec = sceneCurve.get(i);
            if(vec.y <= 16.00f) {
                sceneCurve.set(i, null);
            }
        }
        Vector3f P1 = sceneCurve.stream().filter(Objects::nonNull).findFirst().orElseThrow();

        sceneCurve = topTopWing.stream()
                .skip(60)
                .map(v -> top.P(v.x, v.y).mul(10).add(0, 15 - 4, 0))
                .collect(Collectors.toList());
        for (int i = 0; i < sceneCurve.size(); i++) {
            Vector3f vec = sceneCurve.get(i);
            if(vec.y <= 16.00f) {
                sceneCurve.set(i, null);
            }
        }
        Vector3f P2 = sceneCurve.stream().filter(Objects::nonNull).findFirst().orElseThrow();

        sceneCurve = topWingDecor.stream()
                .map(v -> topWing.P(v.x, v.y).mul(10).add(0, 15 - 4, 0))
                .collect(Collectors.toList());
        for (int i = 0; i < sceneCurve.size(); i++) {
            Vector3f vec = sceneCurve.get(i);
            if(vec.y <= 16.00f) {
                sceneCurve.set(i, null);
            }
        }
        Vector3f P3 = sceneCurve.stream().filter(Objects::nonNull).findFirst().orElseThrow();

        result.add(new Vector3f(P1).setComponent(1, 80));

        for (int i = 0; i <= 100; i++) {
            for (int j = i; j <= 100; j++) {
                float a = (float) i / 100;
                float b = 1.0f - (float) j / 100;
                float c = 1.0f - a - b;
                Vector3f A = new Vector3f(P1).mul(a);
                Vector3f B = new Vector3f(P2).mul(b);
                Vector3f C = new Vector3f(P3).mul(c);
                result.add(A.add(B).add(C));
            }
        }

        result.add(new Vector3f(P1).setComponent(1, 80));

        return Pair.of(result, envelope);
    }

    private static Pair<List<Vector3f>, List<Vector3f>> generateDecorPaths(Intersectable decor, Intersectable... intersections) {
        List<Vector3f> result = new ArrayList<>();
        List<Vector3f> envelope = new ArrayList<>();
        List<List<Vector2f>> curves = new ArrayList<>();
        for (Intersectable intersection : intersections) {
            Intersection finder = new Intersection(decor, intersection);
            curves.add(finder.find(null, vec -> {}, 0.01f, 543).stream().map(v -> new Vector2f(v.x, v.y)).collect(Collectors.toList()));
        }

        for (int i = 0; i <= 100; i++) {
            List<Pair<Vector2f, Vector3f>> tempPath = new ArrayList<>();
            for (int j = 0; j <= 200; j++) {
                float u = (float) i / 100;
                float v = (float) j / 200;
                Vector3f vec = decor.P(u, v).mul(10).add(0, 15 - 4, 0);
                if(vec.isFinite() && vec.y > 16) tempPath.add(Pair.of(new Vector2f(u, v), vec));
            }
            if(tempPath.size() < 3) continue;

            Set<List<Vector2f>> set = new HashSet<>();

            for (int j = 1; j < tempPath.size(); j++) {
                Vector2f vec1 = tempPath.get(j - 1).getKey();
                Vector2f vec2 = tempPath.get(j).getKey();

                boolean before = j != 1 && set.size() == 1;

                for (List<Vector2f> fs : curves) {
                    if(doIntersect(vec1, vec2, fs)) {
                        set.add(fs);
                    }
                }

                boolean after = j != (tempPath.size() - 1) && set.size() == 1;

                if(before != after) {
                    result.add(new Vector3f(tempPath.get(j).getValue()).setComponent(1, 80));
                }

                if(before && after) result.add(tempPath.get(j).getValue());
            }
        }
        return Pair.of(result, envelope);
    }

    private static Pair<List<Vector3f>, List<Vector3f>> generateTopHorizontalWing(Intersectable topHorizontalWing, Intersectable topWing, Intersectable wing) {
        List<Vector3f> result = new ArrayList<>();
        List<Vector3f> envelope = new ArrayList<>();
        Intersection finder = new Intersection(topHorizontalWing, topWing);
        List<Vector2f> curve = finder.find(null, vec -> {
                }, 0.01f, 1).stream()
                .map(v -> new Vector2f(v.x, v.y)).collect(Collectors.toList());

        for (int i = 1; i <= 99; i++) {
            List<Pair<Vector2f, Vector3f>> tempPath = new ArrayList<>();
            for (int j = 26; j <= 199; j++) {
                float u = (float) i / 100;
                float v = (float) j / 200;
                Vector3f vec = topHorizontalWing.P(u, v).mul(10).add(0, 15 - 4, 0);
                Vector3f n = topHorizontalWing.N(u, v);
                if(n.equals(new Vector3f(1, 0, 0), 1e-3f) ||
                   n.equals(new Vector3f(-1, 0, 0), 1e-3f)
                   ||n.equals(new Vector3f(0, -1, 0), 1e-3f) ||
                   n.equals(new Vector3f(0, 1, 0), 1e-3f)) continue;
                if(vec.isFinite() && vec.y > 16) tempPath.add(Pair.of(new Vector2f(u, v), vec));
            }
            if(tempPath.size() < 3) continue;

            for (int j = 1; j < tempPath.size(); j++) {
                Vector2f vec1 = tempPath.get(j - 1).getKey();
                Vector2f vec2 = tempPath.get(j).getKey();

                if(j == 1) {
                    result.add(tempPath.get(1).getValue().setComponent(1, 50));
                }

                if(doIntersect(vec1, vec2, curve)) {
                    result.add(tempPath.get(j - 1).getValue().setComponent(1, 50));
                    break;
                }

                if(tempPath.get(j).getValue().y > 20)
                    result.add(tempPath.get(j).getValue());
                else System.out.println(i);
            }
        }
        return Pair.of(result, envelope);
    }

    private static Pair<List<Vector3f>, List<Vector3f>> generateTopWingPaths(Intersectable topWing, Intersectable... intersections) {
        List<Vector3f> result = new ArrayList<>();
        List<Vector3f> envelope = new ArrayList<>();
        List<List<Vector2f>> curves = new ArrayList<>();
        for (Intersectable intersection : intersections) {
            Intersection finder = new Intersection(topWing, intersection);
            curves.add(finder.find(null, vec -> {}, 0.01f, 3423).stream().map(v -> new Vector2f(v.x, v.y)).collect(Collectors.toList()));
         }

        Intersection finder = new Intersection(topWing, intersections[0]);
        curves.add(finder.find(null, vec -> {}, 0.01f, 65).stream().map(v -> new Vector2f(v.x, v.y)).collect(Collectors.toList()));

        for (List<Vector2f> curve : curves.subList(2, 4)) {
            List<Vector3f> sceneCurve = curve.stream()
                    .map(v -> topWing.P(v.x, v.y).mul(10).add(0, 15 - 4, 0))
                    .collect(Collectors.toList());
            for (int i = 0; i < sceneCurve.size(); i++) {
                Vector3f vec = sceneCurve.get(i);
                if(vec.y <= 16.00f) {
                    sceneCurve.set(i, null);
                }
            }
            for (int i = 1; i < sceneCurve.size() - 1; i++) {
                Vector3f vec1 = sceneCurve.get(i - 1);
                Vector3f vec2 = sceneCurve.get(i);
                Vector3f vec3 = sceneCurve.get(i + 1);

                if(vec1 == null && vec2 != null) {
                    envelope.add(new Vector3f(vec2.x, 80, vec2.z));
                    envelope.add(vec2);
                }

                if(vec3 != null && vec2 != null && vec1 != null) {
                    envelope.add(vec2);
                }

                if(vec3 == null && vec2 != null) {
                    envelope.add(vec2);
                    envelope.add(new Vector3f(vec2.x, 80, vec2.z));
                }
            }
        }

        envelope.add(new Vector3f(envelope.get(envelope.size() - 1)).setComponent(1, 80));

        for (int i = 0; i <= 100; i++) {
            if(i == 8) continue;
            List<Pair<Vector2f, Vector3f>> tempPath = new ArrayList<>();
            for (int j = 0; j <= 200; j++) {
                float u = (float) i / 100;
                float v = 1.0f - (float) j / 200;
                Vector3f vec = topWing.P(u, v).mul(10).add(0, 15 - 4, 0);
                if(vec.isFinite() && vec.y > 16) tempPath.add(Pair.of(new Vector2f(u, v), vec));
            }
            if(tempPath.size() < 3) continue;

            Set<List<Vector2f>> set = new HashSet<>();

            outer:
            for (int j = 1; j < tempPath.size(); j++) {
                Vector2f vec1 = tempPath.get(j - 1).getKey();
                Vector2f vec2 = tempPath.get(j).getKey();

                boolean before = j != 1 && set.isEmpty();

                for (List<Vector2f> fs : curves.subList(2, 4)) {
                    if(doIntersect(vec1, vec2, fs)) {
                        if(set.contains(fs)) set.remove(fs);
                        else set.add(fs);
                    }
                }

                for (List<Vector2f> fs : List.of(curves.get(0), curves.get(1), curves.get(4))) {
                    if(preIntersect(vec1, vec2, fs)) {
                        result.add(new Vector3f(tempPath.get(j).getValue()).setComponent(1, 80));
                        break outer;
                    }
                }

                boolean after = j != (tempPath.size() - 1) && set.isEmpty();

                if(before != after) {
                    result.add(new Vector3f(tempPath.get(j).getValue()).setComponent(1, 80));
                }

                if(before && after) result.add(tempPath.get(j).getValue());
            }
        }
        return Pair.of(result, envelope);
    }

    private static Pair<List<Vector3f>, List<Vector3f>> generateTopPaths(Intersectable top, Intersectable... intersections) {
        List<Vector3f> result = new ArrayList<>();
        List<Vector3f> envelope = new ArrayList<>();
        List<Pair<Intersectable, List<Vector2f>>> curves = new ArrayList<>();

        for (Intersectable intersection : intersections) {
            Intersection finder = new Intersection(top, intersection);
            curves.add(Pair.of(intersection, finder.find(null, vec -> {}, 0.01f, 3).stream().map(v -> new Vector2f(v.x, v.y)).collect(Collectors.toList())));
        }

        Intersection finder = new Intersection(top, intersections[1]);
        curves.add(Pair.of(intersections[1], finder.find(null, vec -> {}, 0.01f, 6).stream().map(v -> new Vector2f(v.x, v.y)).collect(Collectors.toList())));

        int k = 0;
        for (var pairCurve : curves) {
            List<Vector2f> curve = pairCurve.getRight();
            if(k == 2) {
                k++;
                continue;
            }
            List<Vector3f> sceneCurve = curve.stream()
                    .map(v -> top.P(v.x, v.y).mul(10).add(0, 15 - 4, 0))
                    .collect(Collectors.toList());
            for (int i = 0; i < sceneCurve.size(); i++) {
                Vector3f vec = sceneCurve.get(i);
                if(vec.y <= 16.00f) {
                    sceneCurve.set(i, null);
                }
                if(k == 1) {
                    if(i > 210) sceneCurve.set(i, null);
                }
                if(k == 3) {
                    if(i < 256) sceneCurve.set(i, null);
                }
            }
            for (int i = 1; i < sceneCurve.size() - 1; i++) {
                Vector3f vec1 = sceneCurve.get(i - 1);
                Vector3f vec2 = sceneCurve.get(i);
                Vector3f vec3 = sceneCurve.get(i + 1);

                if(vec1 == null && vec2 != null) {
                    envelope.add(new Vector3f(vec2.x, 80, vec2.z));
                    envelope.add(vec2);
                }

                if(vec3 != null && vec2 != null && vec1 != null) {
                    envelope.add(vec2);
                }

                if(vec3 == null && vec2 != null) {
                    envelope.add(vec2);
                    envelope.add(new Vector3f(vec2.x, 80, vec2.z));
                }
            }
            k++;
        }

        for (int i = 33; i <= 43; i++) {
            List<Pair<Vector2f, Vector3f>> tempPath = new ArrayList<>();
            for (int j = 0; j <= 200; j++) {
                float u = (float) i / 100;
                float v = (float) j / 200;
                Vector3f vec = top.P(u, v).mul(10).add(0, 15 - 4, 0);
                if(vec.isFinite()) tempPath.add(Pair.of(new Vector2f(u, v), vec));
            }
            if(tempPath.size() < 3) continue;

            Set<Intersectable> set = new HashSet<>();

            for (int j = 1; j < tempPath.size(); j++) {
                Vector2f vec1 = tempPath.get(j - 1).getKey();
                Vector2f vec2 = tempPath.get(j).getKey();

                boolean before = j != 1 && set.size() == 1;

                for (var fs : curves) {
                    if(doIntersect(vec1, vec2, fs.getRight())) {
                        if(set.contains(fs.getLeft())) set.remove(fs.getLeft());
                        else set.add(fs.getLeft());
                    }
                }

                boolean after = j != (tempPath.size() - 1) && set.size() == 1;


                if(before != after) {
                    result.add(new Vector3f(tempPath.get(j).getValue()).setComponent(1, 80));
                }

                if(before && after) {
                    Vector3f v = tempPath.get(j).getValue();
                    if(v.y < 16) v.y = 16;
                    result.add(v);
                }
            }
        }

        return Pair.of(result, envelope);
    }

    private static Pair<List<Vector3f>, List<Vector3f>> generateWingPaths(Intersectable wing, Intersectable body) {
        List<Vector3f> result = new ArrayList<>();
        List<Vector3f> envelope = new ArrayList<>();
        Intersection finder = new Intersection(wing, body);
        List<Vector2f> curve = finder.find(null, vec -> {
                }, 0.01f, 1).stream()
                .map(v -> new Vector2f(v.x, v.y)).collect(Collectors.toList());

        for (int i = 1; i <= 99; i++) {
            List<Pair<Vector2f, Vector3f>> tempPath = new ArrayList<>();
            for (int j = 28; j <= 200; j++) {
                float u = (float) i / 100;
                float v = (float) j / 200;
                Vector3f vec = wing.P(u, v).mul(10).add(0, 15 - 4, 0);
                Vector3f n = wing.N(u, v);
                if(n.equals(new Vector3f(1, 0, 0), 1e-4f) ||
                   n.equals(new Vector3f(-1, 0, 0), 1e-4f)) continue;
                if(vec.isFinite() && vec.y > 16) tempPath.add(Pair.of(new Vector2f(u, v), vec));
            }
            if(tempPath.size() < 3) continue;

            for (int j = 1; j < tempPath.size(); j++) {
                Vector2f vec1 = tempPath.get(j - 1).getKey();
                Vector2f vec2 = tempPath.get(j).getKey();

                if(j == 1) {
                    result.add(tempPath.get(1).getValue().setComponent(1, 50));
                }

                if(doIntersect(vec1, vec2, curve)) {
                    result.add(tempPath.get(j - 1).getValue().setComponent(1, 50));
                    break;
                }

                if(tempPath.get(j).getValue().y > 20)
                    result.add(tempPath.get(j).getValue());
                else System.out.println(i);
            }
        }
        return Pair.of(result, envelope);
    }

    private static Pair<List<Vector3f>, List<Vector3f>> generateBodyPaths(Intersectable body, Intersectable... intersections) {
        List<Vector3f> result = new ArrayList<>();
        List<Vector3f> envelope = new ArrayList<>();
        List<List<Vector2f>> curves = new ArrayList<>();
        for (Intersectable intersection : intersections) {
            Intersection finder = new Intersection(body, intersection);
            curves.add(finder.find(null, vec -> {}, 0.01f, 1).stream().map(v -> new Vector2f(v.x, v.y)).collect(Collectors.toList()));
        }
        int k = 0;

        for (List<Vector2f> curve : curves) {
            List<Vector3f> sceneCurve = curve.stream()
                    .map(v -> body.P(v.x, v.y).mul(10).add(0, 15 - 4, 0))
                    .collect(Collectors.toList());
            for (int i = 0; i < sceneCurve.size(); i++) {
                Vector3f vec = sceneCurve.get(i);
                if(vec.y <= 16.00f) {
                    sceneCurve.set(i, null);
                }
                // triple intersection handling
                if(k == 2) {
                    if(i < 595 || i > 908) sceneCurve.set(i, null);
                }
                if(k == 1) {
                    if(i > 822 && i < 1097) sceneCurve.set(i, null);
                }
            }
            for (int i = 1; i < sceneCurve.size() - 1; i++) {
                Vector3f vec1 = sceneCurve.get(i - 1);
                Vector3f vec2 = sceneCurve.get(i);
                Vector3f vec3 = sceneCurve.get(i + 1);

                if(vec1 == null && vec2 != null) {
                    envelope.add(new Vector3f(vec2.x, 80, vec2.z));
                    envelope.add(vec2);
                }

                if(vec3 != null && vec2 != null && vec1 != null) {
                    envelope.add(vec2);
                }

                if(vec3 == null && vec2 != null) {
                    envelope.add(vec2);
                    envelope.add(new Vector3f(vec2.x, 80, vec2.z));
                }
            }
            k++;
        }

        for (int i = 0; i <= 300; i++) {
            List<Pair<Vector2f, Vector3f>> tempPath = new ArrayList<>();
            for (int j = 0; j <= 200; j++) {
                float u = (float) i / 300;
                float v = (float) j / 200;
                Vector3f vec = body.P(u, v).mul(10).add(0, 15 - 4, 0);
                if(vec.isFinite() && vec.y > 16) tempPath.add(Pair.of(new Vector2f(u, v), vec));
            }
            if(tempPath.size() < 3) continue;

            Set<List<Vector2f>> set = new HashSet<>();

            for (int j = 1; j < tempPath.size(); j++) {
                Vector2f vec1 = tempPath.get(j - 1).getKey();
                Vector2f vec2 = tempPath.get(j).getKey();

                boolean before = j != 1 && set.isEmpty();

                for (List<Vector2f> fs : curves.subList(0, curves.size() - 1)) {
                    if(doIntersect(vec1, vec2, fs)) {
                        if(set.contains(fs)) set.remove(fs);
                        else set.add(fs);
                    }
                }

                boolean after = j != (tempPath.size() - 1) && set.isEmpty();

                if(before != after) {
                    result.add(new Vector3f(tempPath.get(j).getValue()).setComponent(1, 80));
                }

                if(before && after) result.add(tempPath.get(j).getValue());
            }
        }
        return Pair.of(result, envelope);
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

    private static boolean preIntersect(Vector2f p1, Vector2f q1, List<Vector2f> curve) {
        if(curve.get(0).distance(curve.get(1)) * 2 > curve.get(0).distance(curve.get(curve.size() - 1))) {
            // cycle case:
            if(preIntersect(p1, q1, curve.get(0), curve.get(curve.size() - 1))) return true;
        }
        for (int i = 0; i < curve.size() - 1; i++) {
            if(preIntersect(p1, q1, curve.get(i), curve.get(i + 1))) return true;
        }

        return false;
    }

    private static boolean doIntersect(Vector2f p1, Vector2f q1, List<Vector2f> curve) {
        if(curve.get(0).distance(curve.get(1)) * 2 > curve.get(0).distance(curve.get(curve.size() - 1))) {
            // cycle case:
            if(doIntersect(p1, q1, curve.get(0), curve.get(curve.size() - 1))) return true;
        }
        for (int i = 0; i < curve.size() - 1; i++) {
            if(doIntersect(p1, q1, curve.get(i), curve.get(i + 1))) return true;
        }

        return false;
    }

    private static boolean doIntersect(Vector2f p0, Vector2f q0, Vector2f p1, Vector2f q1)
    {
        return owLawd(p0, q0, p1, q1) && get_line_intersection(p0.x, p0.y, q0.x, q0.y, p1.x, p1.y, q1.x, q1.y);
    }

    private static boolean preIntersect(Vector2f p0, Vector2f q0, Vector2f p1, Vector2f q1) {
        Vector2f pq0 = p0.add(q0, new Vector2f()).div(2);
        Vector2f pq1 = p1.add(q1, new Vector2f()).div(2);
        float r0 = p0.distance(q0);
        float r1 = p1.distance(q1);
        if(pq0.distance(pq1) > 0.1) return false;
        if(r0 > 0.1) return false;
        if(r1 > 0.1) return false;
        return pq0.distance(pq1) * 2 < r0 + r1;
    }

    private static boolean owLawd(Vector2f p0, Vector2f q0, Vector2f p1, Vector2f q1) {
        Vector2f pq0 = p0.add(q0, new Vector2f()).div(2);
        Vector2f pq1 = p0.add(q1, new Vector2f()).div(2);
        float r0 = p0.distance(q0);
        float r1 = p0.distance(q0);
        return pq0.distance(pq1) < r0 + r1;
    }

    // Returns 1 if the lines intersect, otherwise 0. In addition, if the lines
    // intersect the intersection point may be stored in the floats i_x and i_y.
    private static boolean get_line_intersection(float p0_x, float p0_y, float p1_x, float p1_y,
                               float p2_x, float p2_y, float p3_x, float p3_y)
    {
        float s1_x, s1_y, s2_x, s2_y;
        s1_x = p1_x - p0_x;     s1_y = p1_y - p0_y;
        s2_x = p3_x - p2_x;     s2_y = p3_y - p2_y;

        float s, t;
        float v = -s2_x * s1_y + s1_x * s2_y;
        s = (-s1_y * (p0_x - p2_x) + s1_x * (p0_y - p2_y)) / v;
        t = ( s2_x * (p0_y - p2_y) - s2_y * (p0_x - p2_x)) / v;

        return s >= 0 && s <= 1 && t >= 0 && t <= 1;
    }
}
