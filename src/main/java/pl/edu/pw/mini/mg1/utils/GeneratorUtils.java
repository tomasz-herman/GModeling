package pl.edu.pw.mini.mg1.utils;

import java.util.concurrent.atomic.AtomicInteger;

public class GeneratorUtils {
    private static final AtomicInteger id = new AtomicInteger(1);

    private GeneratorUtils() {
        throw new RuntimeException("Utility class");
    }

    public static int getID() {
        return id.getAndIncrement();
    }
}
