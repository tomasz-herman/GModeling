package pl.edu.pw.mini.mg1.layout;

import java.awt.*;

public interface Controller<T> {
    void set(T object);
    Container getMainPane();
}
