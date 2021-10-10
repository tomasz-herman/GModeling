package pl.edu.pw.mini.mg1.milling;

import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class Path {
    private List<Vector3f> coords;

    public Path(InputStream stream) throws IOException {
        if(stream == null) throw new IllegalArgumentException("Got null stream.");
        coords = new ArrayList<>();
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
                if(!coords.isEmpty()) {
                    coord.x = coords.get(coords.size() - 1).x;
                }
            }
            if(tokens.containsKey('Y')) {
                try {
                    coord.y = Float.parseFloat(tokens.get('Y'));
                } catch (NumberFormatException nfe) {
                    throw new IOException(nfe);
                }
            } else {
                if(!coords.isEmpty()) {
                    coord.y = coords.get(coords.size() - 1).y;
                }
            }
            if(tokens.containsKey('Z')) {
                try {
                    coord.z = Float.parseFloat(tokens.get('Z'));
                } catch (NumberFormatException nfe) {
                    throw new IOException(nfe);
                }
            } else {
                if(!coords.isEmpty()) {
                    coord.z = coords.get(coords.size() - 1).z;
                }
            }
            coords.add(coord);
        }
    }

    public List<Vector3fc> getCoords() {
        return Collections.unmodifiableList(coords);
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
