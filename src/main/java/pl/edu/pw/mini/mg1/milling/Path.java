package pl.edu.pw.mini.mg1.milling;

import org.joml.Vector3f;
import org.joml.Vector3fc;
import pl.edu.pw.mini.mg1.models.Point;
import pl.edu.pw.mini.mg1.models.PolyLine;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import static org.joml.Math.ceil;

public class Path {
    private final List<Vector3f> coords;

    public Path(InputStream stream) throws IOException {
        this(stream, Float.POSITIVE_INFINITY);
    }

    public Path(InputStream stream, float maxDist) throws IOException {
        if(stream == null) throw new IllegalArgumentException("Got null stream.");
        coords = new ArrayList<>();
        coords.add(new Vector3f(0, 0, 80));
        Scanner scanner = new Scanner(stream);
        while(scanner.hasNextLine()) {
            String line = scanner.nextLine();
            Map<Character, String> tokens = split(line);
            Vector3f coord = new Vector3f();
            if(tokens.containsKey('X')) {
                try {
                    coord.x = Float.parseFloat(tokens.get('X'));
                } catch (NumberFormatException nfe) {
                    throw new IOException(nfe);
                }
            } else {
                coord.x = coords.get(coords.size() - 1).x;
            }
            if(tokens.containsKey('Y')) {
                try {
                    coord.y = Float.parseFloat(tokens.get('Y'));
                } catch (NumberFormatException nfe) {
                    throw new IOException(nfe);
                }
            } else {
                coord.y = coords.get(coords.size() - 1).y;
            }
            if(tokens.containsKey('Z')) {
                try {
                    coord.z = Float.parseFloat(tokens.get('Z'));
                } catch (NumberFormatException nfe) {
                    throw new IOException(nfe);
                }
            } else {
                coord.z = coords.get(coords.size() - 1).z;
            }
            coords.addAll(split(coords.get(coords.size() - 1), coord, maxDist));
        }
    }

    private static Collection<Vector3f> split(Vector3fc last, Vector3fc next, float maxDist) {
        float dist = last.distance(next);
        if (dist < maxDist) {
            return Collections.singleton(new Vector3f(next));
        } else {
            int divisions = (int) ceil(dist / maxDist);
            Vector3fc step = next.sub(last, new Vector3f()).div(divisions);
            Collection<Vector3f> vectors = new ArrayList<>();
            for (int i = 0; i < divisions; i++) {
                vectors.add(last.add(step.mul(i + 1, new Vector3f()), new Vector3f()));
            }
            return vectors;
        }
    }

    public List<Vector3fc> getCoords() {
        return Collections.unmodifiableList(coords);
    }

    public PolyLine toPolyLine() {
        return new PolyLine(coords.stream().map(v -> new Vector3f(v.x, v.z, v.y).div(100)).map(Point::new).toList());
    }

    private static Map<Character, String> split(String line) {
        Map<Character, String> map = new HashMap<>();
        char code = '\0';
        StringBuilder temp = new StringBuilder();
        for (int i = 0; i < line.length(); i++) {
            char current = Character.toUpperCase(line.charAt(i));
            if(Character.isAlphabetic(current)) {
                if(temp.length() > 0) {
                    map.put(code, temp.toString());
                    temp = new StringBuilder();
                }
                code = current;
            } else {
                temp.append(current);
            }
        }
        if(temp.length() > 0) map.put(code, temp.toString());
        return map;
    }
}
