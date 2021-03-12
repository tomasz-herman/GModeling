package pl.edu.pw.mini.mg1.utils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class IOUtils {
    private IOUtils() {
        throw new RuntimeException("Utility class");
    }

    public static String readResource(String resource) {
        try {
            URI uri = IOUtils.class.getResource(resource).toURI();
            Path path = Paths.get(uri);
            return Files.readString(path);
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }
}
